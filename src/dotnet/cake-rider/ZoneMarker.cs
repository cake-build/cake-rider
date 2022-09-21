using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Rider.Backend.Product;
using JetBrains.Rider.Model;

namespace net.cakebuild;

[ZoneMarker]
public class ZoneMarker :
    IRequire<IFrostingZone>
{
}

[ZoneDefinition(ZoneFlags.AutoEnable)]
public interface IFrostingZone : IZone,
    IRequire<IRiderProductEnvironmentZone>,
    IRequire<IProjectModelZone>,
    IRequire<IRiderModelZone>,
    IRequire<IDocumentModelZone>,
    IRequire<PsiFeaturesImplZone>
{
}
