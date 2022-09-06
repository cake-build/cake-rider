using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;

namespace net.cakebuild.TaskMarker;

[StaticSeverityHighlighting(Severity.INFO, typeof(HighlightingGroupIds.GutterMarks), OverlapResolve = OverlapResolveKind.NONE, AttributeId = TaskMarkerAttributeIds.TASK_MARKER_ID)]
public class TaskMarkerHighlighting : IHighlighting
{
    private readonly DocumentRange _documentRange;

    public TaskMarkerHighlighting(IClassDeclaration classDeclaration, DocumentRange documentRange, string taskName)
    {
        _documentRange = documentRange;
        TaskName = taskName;
        Project = classDeclaration.GetSourceFile()?.GetProject();
    }

    public string TaskName { get; }

    // Do not dispose IProject.
#pragma warning disable IDISP006
    public IProject Project { get; }
#pragma warning restore IDISP006

    public string ToolTip => $"Task '{TaskName}'";

    public string ErrorStripeToolTip => null;

    public bool IsValid() => true;

    public DocumentRange CalculateRange() => _documentRange;
}
