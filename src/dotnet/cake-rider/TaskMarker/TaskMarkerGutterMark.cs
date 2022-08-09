using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.UI.Controls.BulbMenu.Anchors;
using JetBrains.Application.UI.Controls.BulbMenu.Items;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.ReSharper.Features.Running;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Rider.Backend.Features.RunConfiguration;
using JetBrains.Rider.Model;
using JetBrains.TextControl.DocumentMarkup;
using JetBrains.UI.RichText;
using JetBrains.Util;
using net.cakebuild.Protocol;

namespace net.cakebuild.TaskMarker
{
    public class TaskMarkerGutterMark : IconGutterMarkType
    {
        public TaskMarkerGutterMark() : base(CakeIcons.CakeGutterMark)
        {
        }

        [NotNull]
        public override IAnchor Priority => BulbMenuAnchors.PermanentItem;

        public override IEnumerable<BulbMenuItem> GetBulbMenuItems(IHighlighter highlighter)
        {
            if (highlighter.UserData is not TaskMarkerHighlighting data) yield break;

            var project = data.Project;
            var solution = Shell.Instance.GetComponent<SolutionsManager>().Solution;
            if (solution == null) yield break;

            var configurationHost = solution.GetComponent<RunConfigurationHost>();
            var cakeFrostingProjectsModel = solution.GetProtocolSolution().GetCakeFrostingProjectsModel();

            var configurationExistsForExecutor = new HashSet<string>();

            foreach (var configuration in configurationHost.AvailableConfigurations)
            {
                if (configuration.TypeId != "CakeFrosting") continue;
                if (FileSystemPath.TryParse(configuration.ProjectFilePath) != project.ProjectFileLocation.ToNativeFileSystemPath()) continue;
                if (!cakeFrostingProjectsModel.RunConfigurationTaskNames.TryGetValue(configuration, out var taskName) || taskName != data.TaskName) continue;

                configurationExistsForExecutor.Add(configuration.Executor.Id);
                yield return new BulbMenuItem(new ExecutableItem(() => configurationHost.Run(configuration)), new RichText(configuration.Executor.DisplayName + " '" + configuration.Name + "'"), IconId, BulbMenuAnchors.PermanentItem);
            }

            var runnableProjectFactory = solution.GetComponent<RunnableProjectFactory>();

            if (configurationHost.AvailableTemplates.Count != 0)
            {
                foreach (var template in configurationHost.AvailableTemplates)
                {
                    if (template.TypeId != "CakeFrosting") continue;
                    if (configurationExistsForExecutor.Contains(template.Executor.Id)) continue;

                    foreach (var runnableProject in runnableProjectFactory.CreateRunnableProjects(project).Where(p => p.Kind == CommonRunnableProjectKinds.DotNetCore))
                    {
                        if (template.CompatibleRunnableProjectKinds.Contains(runnableProject.Kind.Name))
                        {
                            var name = $"{runnableProject.Name}: {data.TaskName}";
                            var templateEntries = new List<RunConfigurationTemplateEntry>
                            {
                                new(RunConfigurationTemplateKey.Name, name),
                                new(RunConfigurationTemplateKey.ProjectFilePath, runnableProject.ProjectFilePath),
                                new(RunConfigurationTemplateKey.StaticMethodName, data.TaskName),
                            };
                            yield return new BulbMenuItem(new ExecutableItem(() => configurationHost.CreateAndRun(new RunConfigurationTemplate(template.Executor, template.TypeId, template.CompatibleRunnableProjectKinds, templateEntries))), new RichText($"Create and {template.Executor.DisplayName} '{name}'"), IconId, BulbMenuAnchors.PermanentItem);
                        }
                    }
                }
            }
        }
    }
}
