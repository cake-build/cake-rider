using System.Collections.Generic;
using System.Linq;

using JetBrains.Application;
using JetBrains.Application.Threading;
using JetBrains.Collections;
using JetBrains.DocumentModel;
using JetBrains.Metadata.Utils;
using JetBrains.Platform.RdFramework.Impl;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.ProjectsHost;
using JetBrains.ProjectModel.Tasks;
using JetBrains.Rd.Base;
using JetBrains.RdBackend.Common.Features;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.Caches.SymbolCache;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using JetBrains.Util.Reflection;

using net.cakebuild.Protocol;

namespace net.cakebuild;

[SolutionComponent]
public class CakeFrostingProjectsHost : IDetectFrostingModules, ICakeFrostingProjectsHost
{
    private static readonly AssemblyNameInfo CakeFrostingAssemblyName =
        AssemblyNameInfoFactory.Create2("Cake.Frosting", null);

    private readonly ILogger _logger;
    private readonly ISolution _solution;
    private readonly CakeFrostingProjectsModel _model;
    private readonly ISymbolCache _symbolCache;
    private readonly ShellRdDispatcher _shellRdDispatcher;

    private readonly Dictionary<IProjectMark, CakeFrostingProject> _cakeFrostingProjects =
        new Dictionary<IProjectMark, CakeFrostingProject>();

    private readonly Dictionary<IProjectMark, Dictionary<IPsiSourceFile, HashSet<string>>> _cakeFrostingTasks =
        new Dictionary<IProjectMark, Dictionary<IPsiSourceFile, HashSet<string>>>();

    private bool _isSolutionLoaded;

    public CakeFrostingProjectsHost(
        ILogger logger,
        ISolution solution,
        ISymbolCache symbolCache,
        ShellRdDispatcher shellRdDispatcher,
        ISolutionLoadTasksScheduler solutionLoadTasksScheduler)
    {
        _logger = logger;
        _solution = solution;
        _symbolCache = symbolCache;
        _shellRdDispatcher = shellRdDispatcher;
        _model = solution.GetProtocolSolution().GetCakeFrostingProjectsModel();

        solutionLoadTasksScheduler.EnqueueTask(new SolutionLoadTask(
            "Initialize cake projects",
            SolutionLoadTaskKinds.AfterDone,
            () =>
            {
                LoadAllTasks();
                _isSolutionLoaded = true;
            }));
    }

    public void Refresh(IProjectMark projectMark)
    {
        using (_solution.Locks.UsingReadLock())
        {
            _logger.Trace("Handle change for project: {0}", projectMark.Location);

            // Do not dispose IProject!
#pragma warning disable IDISP001
            var projectByMark = _solution.GetProjectByMark(projectMark);
#pragma warning restore IDISP001
            if (projectByMark == null)
            {
                _logger.Trace("project == null");
                ProjectRemoved(projectMark);
            }
            else
            {
                ProjectRemoved(projectMark);
                if (IsCakeFrostingProject(projectByMark))
                {
                    ProjectAdded(projectByMark);
                }
            }
        }
    }

    public void ProcessTasks(ICSharpFile file, IDocument document)
    {
        // Do not dispose IProject!
#pragma warning disable IDISP001
        var project = file.GetProject();
#pragma warning restore IDISP001
        var projectMark = project?.GetProjectMark();
        if (projectMark == null)
        {
            return;
        }

        if (!_cakeFrostingProjects.TryGetValue(projectMark, out var cakeFrostingProject))
        {
            return;
        }

        var fileTaskMap = _cakeFrostingTasks[projectMark];

        var psiSourceFile = document.GetPsiSourceFile(_solution)!;
        var currentFileTasks = fileTaskMap.GetOrCreateValue(psiSourceFile, () => new HashSet<string>());
        var newTasks = new HashSet<string>();

        var toAdd = new HashSet<string>();
        var toRemove = new HashSet<string>();

        var classDeclarations = CachedDeclarationsCollector.Run<IClassDeclaration>(file);
        foreach (var classDeclaration in classDeclarations)
        {
            var declaredElement = classDeclaration.DeclaredElement;
            if (declaredElement != null)
            {
                if (!declaredElement.IsCakeFrostingTask())
                {
                    continue;
                }

                var name = declaredElement.GetCakeFrostingTaskName();

                if (!cakeFrostingProject.Tasks.Contains(name))
                {
                    toAdd.Add(name);
                }

                currentFileTasks.Add(name);
                newTasks.Add(name);
            }
        }

        foreach (var currentFileTask in currentFileTasks)
        {
            if (!newTasks.Contains(currentFileTask))
            {
                currentFileTasks.Remove(currentFileTask);
                toRemove.Add(currentFileTask);
            }
        }

        _shellRdDispatcher.Queue(() =>
        {
            cakeFrostingProject.Tasks.RemoveRange(toRemove);
            cakeFrostingProject.Tasks.AddRange(toAdd);
        });
    }

    public bool IsFrostingModule(IPsiModule module)
    {
        return _cakeFrostingProjects.Keys
            .Select(_solution.GetProjectByMark)
            .Where(p => p != null)
            .SelectMany(p => p.GetPsiModules())
            .Any(m => m.GetPersistentID() == module.GetPersistentID());
    }

    private static bool IsCakeFrostingProject(IProject project)
    {
        return !project.IsSharedProject()
               && project.ProjectProperties.ProjectKind == ProjectKind.REGULAR_PROJECT
               && ReferencedAssembliesService.IsProjectReferencingAssemblyByName(
                   project,
                   project.GetCurrentTargetFrameworkId(),
                   CakeFrostingAssemblyName,
                   out _);
    }

    private void ProjectRemoved(IProjectMark projectMark)
    {
        if (_cakeFrostingProjects.TryGetValue(projectMark, out var cakeFrostingProject) && _isSolutionLoaded)
        {
            _model.Projects.Remove(cakeFrostingProject);
            _cakeFrostingProjects.Remove(projectMark);
            _cakeFrostingTasks.Remove(projectMark);
            return;
        }

        _logger.Trace("Skip project remove handling because project not contains in map or solution isn't loaded");
    }

    private void ProjectAdded(IProject project)
    {
        var projectMark = project.GetProjectMark();
        if (projectMark == null)
        {
            return;
        }

        var cakeFrostingProject = new CakeFrostingProject();
        cakeFrostingProject.Name.Set(project.Name);
        cakeFrostingProject.ProjectFilePath.Set(
            project.ProjectFileLocation.NormalizeSeparators(FileSystemPathEx.SeparatorStyle.Unix));
        _model.Projects.Add(cakeFrostingProject);
        _cakeFrostingProjects.Add(projectMark, cakeFrostingProject);
        _cakeFrostingTasks.Add(projectMark, new Dictionary<IPsiSourceFile, HashSet<string>>());
    }

    private void LoadAllTasks()
    {
        foreach ((IProjectMark projectMark, CakeFrostingProject cakeFrostingProject) in _cakeFrostingProjects)
        {
            var fileTaskMap = _cakeFrostingTasks[projectMark];

            foreach (var psiModule in _solution.GetProjectByMark(projectMark)!.GetPsiModules())
            {
                using (CompilationContextCookie.OverrideOrCreate(psiModule.GetContextFromModule()))
                {
                    foreach (var sourceFile in psiModule.SourceFiles)
                    {
                        var tasks = fileTaskMap[sourceFile] = new HashSet<string>();

                        foreach (var klass in _symbolCache.GetTypesAndNamespacesInFile(sourceFile).OfType<IClass>())
                        {
                            Interruption.Current.CheckAndThrow();

                            if (klass.IsCakeFrostingTask())
                            {
                                var name = klass.GetCakeFrostingTaskName();
                                cakeFrostingProject.Tasks.Add(name);
                                tasks.Add(name);
                            }
                        }
                    }
                }
            }
        }
    }
}

public interface IDetectFrostingModules
{
    bool IsFrostingModule(IPsiModule module);
}

public interface ICakeFrostingProjectsHost
{
    void ProcessTasks(ICSharpFile file, IDocument document);

    void Refresh(IProjectMark changeProjectMark);
}
