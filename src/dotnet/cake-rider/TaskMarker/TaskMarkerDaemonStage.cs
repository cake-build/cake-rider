using System.Collections.Generic;
using System.Linq;

using JetBrains.Application.Settings;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;

namespace net.cakebuild.TaskMarker;

[DaemonStage(StagesBefore = new[] { typeof(SmartResolverStage) })]
public class TaskMarkerDaemonStage : IDaemonStage
{
    public IEnumerable<IDaemonStageProcess> CreateProcess(IDaemonProcess process, IContextBoundSettingsStore settings, DaemonProcessKind processKind)
    {
        if (processKind != DaemonProcessKind.VISIBLE_DOCUMENT)
        {
            return Enumerable.Empty<IDaemonStageProcess>();
        }

        return new[]
        {
            new TaskMarkerDaemonStageProcess(process, settings),
        };
    }
}
