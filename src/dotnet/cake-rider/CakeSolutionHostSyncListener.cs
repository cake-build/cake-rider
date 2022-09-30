using JetBrains.ProjectModel;
using JetBrains.ProjectModel.ProjectsHost.Impl;
using JetBrains.ProjectModel.ProjectsHost.SolutionHost;

namespace net.cakebuild;

[SolutionInstanceComponent]
public class CakeSolutionHostSyncListener : SolutionHostSyncListener
{
    private readonly ISolution _solution;

    public CakeSolutionHostSyncListener(ISolution solution)
    {
        _solution = solution;
    }

    private ICakeFrostingProjectsHost Host => _solution.TryGetComponent<ICakeFrostingProjectsHost>();

    public override void AfterUpdateProject(ProjectHostChange change) => Host?.Refresh(change.ProjectMark);

    public override void AfterRemoveProject(ProjectHostChange change) => Host?.Refresh(change.ProjectMark);
}
