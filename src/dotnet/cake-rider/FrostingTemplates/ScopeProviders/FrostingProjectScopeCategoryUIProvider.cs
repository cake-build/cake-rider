using System.Collections.Generic;

using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.UI.Icons;

using net.cakebuild.FrostingTemplates.Scopes;

namespace net.cakebuild.FrostingTemplates.ScopeProviders;

// Defines a category for the UI, and the scope points that it includes
[ScopeCategoryUIProvider(Priority = Priority, ScopeFilter = ScopeFilter.Project)]
public class FrostingProjectScopeCategoryUiProvider : ScopeCategoryUIProvider
{
    // Needs to be less than other priorities in R#'s built in ScopeCategoryUIProvider
    // to push it to the end of the list
    private const int Priority = -200;

    public FrostingProjectScopeCategoryUiProvider()
    {
        // The main scope point is used to the UID of the QuickList for this category.
        // It does nothing unless there is also a QuickList stored in settings.
        MainPoint = new InFrostingCSharpProject();
    }

    public override string CategoryCaption => "Frosting";

    public override IconId Icon => CakeIcons.CakeFile;

    public override IEnumerable<ITemplateScopePoint> BuildAllPoints()
    {
        // Only Project-related scopes. (i.e. scopes used in File-Templates)
        yield return new InFrostingCSharpProject();
    }
}
