using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;

using net.cakebuild.FrostingTemplates.ScopeProviders;
using net.cakebuild.tests.Components;

using NUnit.Framework;

namespace net.cakebuild.tests.Features.LiveTemplates;

[TestFixture]
[Category("Lve Templates")]
public class FrostingProjectLiveTemplateTests : BaseScopeProviderTest
{
    protected override string RelativeTestDataPath => @"CSharp/LiveTemplates";
    protected override IScopeProvider CreateScopeProvider() => new FrostingProjectScopeProvider();

    [TestCase(true)]
    [TestCase(false)]
    public void TestInFrostingProject(bool inFrosting)
    {
        var fullName = $"{(!inFrosting ? "Not" : string.Empty)}inFrosting"; 
        MockFrostingDetector.EverythingIsFrosting = inFrosting; 
        DoOneTest(fullName);
    }
}
