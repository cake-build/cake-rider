using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.ReSharper.LiveTemplates.CSharp.Scope;

using net.cakebuild.FrostingTemplates.ScopeProviders;

using NUnit.Framework;

namespace net.cakebuild.tests.Features.LiveTemplates;

[TestFixture]
public class FrostingProjectScopeProviderTests : BaseScopeProviderTest
{
    protected override string RelativeTestDataPath => @"CSharp/LiveTemplates/Scope";
    protected override IScopeProvider CreateScopeProvider() => new FrostingProjectScopeProvider(new CSharpScopeProvider());

    [Test]
    public void TestInFrostingProject() { DoNamedTest2(); }
}
