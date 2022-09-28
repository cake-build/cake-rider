using JetBrains.Application.BuildScript.Application.Zones;

namespace net.cakebuild.tests;

[ZoneMarker]
public sealed class ZoneMarker : IRequire<IFrostingTestsZone>
{
}

[ZoneDefinition]
public interface IFrostingTestsZone : IZone,
    IRequire<IFrostingZone>
{
}
