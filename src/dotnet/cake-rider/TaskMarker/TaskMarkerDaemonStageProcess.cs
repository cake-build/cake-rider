using System;

using JetBrains.Application.Settings;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi.Caches.SymbolCache;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;

namespace net.cakebuild.TaskMarker;

public class TaskMarkerDaemonStageProcess : IDaemonStageProcess
{
    public TaskMarkerDaemonStageProcess(IDaemonProcess daemonProcess, IContextBoundSettingsStore settings)
    {
        DaemonProcess = daemonProcess;
        Settings = settings;
    }

    public IDaemonProcess DaemonProcess { get; }

    public IContextBoundSettingsStore Settings { get; }

    public void Execute(Action<DaemonStageResult> committer)
    {
        var sourceFile = DaemonProcess.SourceFile;
        var primaryPsiFile = sourceFile.GetPrimaryPsiFile();
        if (primaryPsiFile == null || sourceFile.Properties.IsNonUserFile)
        {
            committer(null);
        }
        else
        {
            var consumer = new DefaultHighlightingConsumer(sourceFile);
            CollectTaskMarkers(primaryPsiFile, consumer);
            committer(new DaemonStageResult(consumer.Highlightings));
        }
    }

    private static void CollectTaskMarkers(IFile file, IHighlightingConsumer consumer)
    {
        if (file is not ICSharpFile context)
        {
            return;
        }

        foreach (var declaration in CachedDeclarationsCollector.Run<IClassDeclaration>(context))
        {
            var declaredElement = declaration.DeclaredElement;
            if (declaredElement != null)
            {
                if (!declaredElement.IsCakeFrostingTask())
                {
                    continue;
                }

                var name = declaredElement.GetCakeFrostingTaskName();

                var nameDocumentRange = declaration.GetNameDocumentRange();
                var markerHighlighting = new TaskMarkerHighlighting(declaration, nameDocumentRange, name);
                consumer.AddHighlighting(markerHighlighting, nameDocumentRange);
            }
        }
    }
}
