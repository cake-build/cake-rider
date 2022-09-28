using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Rider.Backend.Env;
using JetBrains.Rider.Model;

namespace net.cakebuild;

[ZoneMarker]
public class ZoneMarker :
    IRequire<IFrostingZone>
{
}

[ZoneDefinition(ZoneFlags.AutoEnable)]
public interface IFrostingZone : IZone,
    IRequire<IRiderPlatformZone>,
    IRequire<IRiderModelZone>,
    IRequire<DaemonZone>,
    IRequire<IProjectModelZone>,
    IRequire<IDocumentModelZone>,
    IRequire<PsiFeaturesImplZone>
{
}
