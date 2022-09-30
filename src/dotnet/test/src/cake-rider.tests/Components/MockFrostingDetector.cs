using JetBrains.Application.Components;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.ProjectsHost;
using JetBrains.ReSharper.Psi.CSharp.Tree;

namespace net.cakebuild.tests.Components;

[SolutionComponent]
public class MockFrostingDetector : IDetectFrostingModules
{
    public static bool EverythingIsFrosting = true;

    public bool IsCakeFrostingProject(IProject project) => EverythingIsFrosting;

}

[SolutionComponent]
public class MockProjectHost : ICakeFrostingProjectsHost
{
    public void ProcessTasks(ICSharpFile file, IDocument document)
    {
        // nothing
    }

    public void Refresh(IProjectMark changeProjectMark)
    {
        // nothing
    }
}

[SolutionComponent]
public class DoNothingMock : IHideImplementation<CakeFrostingProjectsHost>
{
    // this simply prevents the engine from trying to instantiate CakeFrostingProjectsHost
}
