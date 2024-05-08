using System;

using JetBrains.Annotations;
using JetBrains.Application.Settings;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.CSharp.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi.CSharp.Tree;

namespace net.cakebuild;

/// <summary>
/// Used to update tasks after initial solution load.
/// </summary>
public sealed class CakeTaskCollectorStage : CSharpDaemonStageBase
{
    private readonly ICakeFrostingProjectsHost _host;

    public CakeTaskCollectorStage(ICakeFrostingProjectsHost host)
    {
        _host = host;
    }

    protected override IDaemonStageProcess CreateProcess(IDaemonProcess process, IContextBoundSettingsStore settings, DaemonProcessKind processKind, ICSharpFile file)
    {
        if (processKind != DaemonProcessKind.SOLUTION_ANALYSIS)
        {
            return null;
        }

        return new CakeTaskCollectorProcess(process, file, _host);
    }
}

public sealed class CakeTaskCollectorProcess : CSharpDaemonStageProcessBase
{
    private readonly ICakeFrostingProjectsHost _host;

    public CakeTaskCollectorProcess([NotNull] IDaemonProcess process, [NotNull] ICSharpFile file, ICakeFrostingProjectsHost host)
        : base(process, file)
    {
        _host = host;
    }

    public override void Execute(Action<DaemonStageResult> committer)
    {
        _host.ProcessTasks(File, Document);
    }
}
