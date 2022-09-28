using System.Collections.Generic;

using JetBrains.Application;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Context;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.ReSharper.Psi;

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

        // Do not dispose IProject!
#pragma warning disable IDISP001
        var project = sourceFile?.GetProject();
#pragma warning restore IDISP001
        if (project == null)
        {
            // not a "real" SourceFile or not in a project.
            yield break;
        }

        var solution = context.Solution;
        var host = solution.GetComponent<IDetectFrostingModules>();
        var isFrostingProject = host.IsCakeFrostingProject(project);
        if (!isFrostingProject)
        {
            // Cake.Frosting is not referenced -> no Frosting project.
            yield break;
        }

        // So, we've found Cake.Frosting
        yield return new InFrostingProject();
    }
}
