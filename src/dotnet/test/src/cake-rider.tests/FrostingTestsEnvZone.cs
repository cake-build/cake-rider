using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Application.Environment;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.TestFramework;
using JetBrains.Rider.Backend.Env;
using JetBrains.TestFramework.Application.Zones;

using net.cakebuild.tests;

#pragma warning disable CheckNamespace

// This activator can not be in the same Namespace as
// the rest of the tests!
namespace net.cakebuild.TestsActivator;

[ZoneDefinition]
public class FrostingTestsEnvZone : ITestsEnvZone
{
    [ZoneActivator]
    [ZoneMarker(typeof(FrostingTestsEnvZone))]
    public class FrostingTestsZoneActivator
        : IActivate<PsiFeatureTestZone>,
            IActivate<IFrostingTestsZone>,
            IActivate<IRiderPlatformZone>,
            IActivate<IRiderFeatureZone>,
            IActivate<IRiderFeatureEnvironmentZone>,
            IActivate<DaemonZone>,
            IActivate<IPsiLanguageZone>,
            IActivate<ILanguageCSharpZone>
    {
    }
}
