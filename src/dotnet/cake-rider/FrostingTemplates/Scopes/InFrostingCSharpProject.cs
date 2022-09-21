using System;

using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace net.cakebuild.FrostingTemplates.Scopes;

// Defines a scope point, but has no inherent behaviour, other than to compare against
// other scope points. A template can declare that it requires this scope point, and
// the template will only be made available if a ScopeProvider "publishes" this scope
// point based on the current context (e.g. the project is a Frosting project)
public class InFrostingCSharpProject : InLanguageSpecificProject
{
    private static readonly Guid DefaultGuid = new Guid("B37325A3-4F0A-405B-8A5C-00ECA4ED3B30");
    private static readonly Guid QuickGuid = new Guid("D32F297F-E422-4612-839A-FE76D9914B34");

    public InFrostingCSharpProject()
        : base(JetBrains.ProjectModel.Properties.ProjectLanguage.CSHARP)
    {
        AdditionalSuperTypes.Add(typeof(InLanguageSpecificProject));
    }

    public override string PresentableShortName => "Frosting projects";

    public override PsiLanguageType RelatedLanguage => CSharpLanguage.Instance;

    // Define the name and UID of the QuickList we'll use for Frosting projects. Any
    // scope points that are subsets will appear in this QuickList (I think)
    public override string QuickListTitle => PresentableShortName;

    public override Guid QuickListUID => QuickGuid;

    public override Guid GetDefaultUID() => DefaultGuid;

    public override string ToString() => PresentableShortName;
}
