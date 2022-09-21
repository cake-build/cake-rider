using System.Collections.Generic;
using System.Linq;

using JetBrains.Application;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Context;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.ReSharper.LiveTemplates.CSharp.Scope;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

using net.cakebuild.FrostingTemplates.Scopes;

namespace net.cakebuild.FrostingTemplates.ScopeProviders;

// Provides the scope points that are valid for the given context
[ShellComponent]
public class FrostingProjectScopeProvider : ScopeProvider
{
    private readonly CSharpScopeProvider _cSharpScopeProvider;

    public FrostingProjectScopeProvider(CSharpScopeProvider cSharpScopeProvider)
    {
        _cSharpScopeProvider = cSharpScopeProvider;

        // Used when creating scope point from settings
        Creators.Add(TryToCreate<InFrostingCSharpFile>);
        Creators.Add(TryToCreate<InFrostingCSharpProject>);
    }

    public override IEnumerable<ITemplateScopePoint> ProvideScopePoints(TemplateAcceptanceContext context)
    {
        var sourceFile = context.SourceFile;
        if (sourceFile == null ||
            sourceFile.PrimaryPsiLanguage.Name != ((PsiLanguageType)CSharpLanguage.Instance)?.Name)
        {
            // not a c# project.
            yield break;
        }

        var solution = context.Solution;
        var host = solution.GetComponent<IListFrostingModules>();
        var frostingModuleIds = host.GetFrostingModules()
            .Select(m => m.GetPersistentID())
            .ToList();
        var isFrostingModule = frostingModuleIds.Contains(sourceFile.PsiModule.GetPersistentID());
        if (!isFrostingModule)
        {
            // Cake.Frosting is not referenced -> no Frosting project.
            yield break;
        }

        // So, we've found Cake.Frosting
        yield return new InFrostingCSharpProject();

        // get some information on the c#-context
        // not sure if this is allowed :-)
        var csScopes = _cSharpScopeProvider
            .ProvideScopePoints(context)
            .Select(s => s.GetType().Name)
            .ToArray();

        if (csScopes.Any(s => s is "InCSharpTypeMember" or "InCSharpTypeAndNamespace"))
        {
            yield return new InFrostingCSharpFile();
        }
    }
}
