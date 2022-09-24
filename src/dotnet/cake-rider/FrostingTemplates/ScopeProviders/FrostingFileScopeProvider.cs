using System.Collections.Generic;

using JetBrains.Application;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Context;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;

using net.cakebuild.FrostingTemplates.Scopes;

namespace net.cakebuild.FrostingTemplates.ScopeProviders;

// Provides the scope points that are valid for the given context
[ShellComponent]
public class FrostingProjectScopeProvider : ScopeProvider
{
    public FrostingProjectScopeProvider()
    {
        // Used when creating scope point from settings
        Creators.Add(TryToCreate<InFrostingProject>);
    }

    public override IEnumerable<ITemplateScopePoint> ProvideScopePoints(TemplateAcceptanceContext context)
    {
        var sourceFile = context.SourceFile;
        if (sourceFile == null)
        {
            yield break;
        }

        var solution = context.Solution;
        var host = solution.GetComponent<IDetectFrostingModules>();
        var isFrostingModule = host.IsFrostingModule(sourceFile.PsiModule);
        if (!isFrostingModule)
        {
            // Cake.Frosting is not referenced -> no Frosting project.
            yield break;
        }

        // So, we've found Cake.Frosting
        yield return new InFrostingProject();
    }
}
