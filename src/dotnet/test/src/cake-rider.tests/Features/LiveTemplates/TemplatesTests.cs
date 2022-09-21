using JetBrains.ReSharper.FeaturesTestFramework.Completion;

using NUnit.Framework;

namespace net.cakebuild.tests.Features.LiveTemplates;

[TestFixture]
public class TemplatesTests : CodeCompletionTestBase
{
    protected override string RelativeTestDataPath => @"CSharp/LiveTemplates/Templates";

    protected override CodeCompletionTestType TestType => CodeCompletionTestType.Action;

    protected override bool CheckAutomaticCompletionDefault() => true;

    [Test]
    public void TestListCakeLifeTemplates() { DoNamedTest2(); }
}
