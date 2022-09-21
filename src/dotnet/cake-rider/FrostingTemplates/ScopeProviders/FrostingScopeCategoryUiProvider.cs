using System.Collections.Generic;

using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.UI.Icons;

using net.cakebuild.FrostingTemplates.Scopes;

namespace net.cakebuild.FrostingTemplates.ScopeProviders;

[ScopeCategoryUIProvider(Priority = Priority)]
public class FrostingScopeCategoryUiProvider : ScopeCategoryUIProvider
{
    // Needs to be less than other priorities in R#'s built in ScopeCategoryUIProvider
    // to push it to the end of the list
    private const int Priority = -200;

    public FrostingScopeCategoryUiProvider()
    {
        MainPoint = new InFrostingCSharpProject();
    }

    public override string CategoryCaption => "Frosting";

    public override IconId Icon => CakeIcons.CakeFile;

    public override IEnumerable<ITemplateScopePoint> BuildAllPoints()
    {
        yield return new InFrostingCSharpProject();
        yield return new InFrostingCSharpFile();
    }

    public override string Present(ITemplateScopePoint point)
    {
        return point switch
        {
            InFrostingCSharpFile => "In a Frosting C# File",
            InFrostingCSharpProject => "In a Frosting C# project",
            _ => base.Present(point),
        };
    }
}
