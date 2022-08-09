using JetBrains.TextControl.DocumentMarkup;

namespace net.cakebuild.TaskMarker;

[RegisterHighlighter(TASK_MARKER_ID, Layer = (HighlighterLayer)2001, EffectType = EffectType.GUTTER_MARK, GutterMarkType = typeof(TaskMarkerGutterMark))]
public static class TaskMarkerAttributeIds
{
    public const string TASK_MARKER_ID = "Cake Task Gutter Mark";
}
