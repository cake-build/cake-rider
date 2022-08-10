using System;
using System.Collections.Generic;

using JetBrains.Annotations;
using JetBrains.Rider.Backend.Features.ProjectModel.ProjectTemplates.DotNetExtensions;
using JetBrains.Rider.Backend.Features.ProjectModel.ProjectTemplates.DotNetTemplates;
using JetBrains.Rider.Model;

namespace net.cakebuild.Templates;

public abstract class TextTemplateProviders : DotNetTemplateParameter
{
    private TextTemplateProviders(
        [NotNull] string name,
        [NotNull] string presentableName,
        [CanBeNull] string tooltip)
        : base(name, presentableName, tooltip)
    {
    }

    public override RdProjectTemplateContent CreateContent(
        DotNetProjectTemplateExpander expander,
        IDotNetTemplateContentFactory factory,
        int index,
        IDictionary<string, string> context)
    {
        if (expander is null)
        {
            throw new ArgumentNullException(nameof(expander));
        }

        if (factory is null)
        {
            throw new ArgumentNullException(nameof(factory));
        }

        if (context is null)
        {
            throw new ArgumentNullException(nameof(context));
        }

        var parameter = expander.TemplateInfo.GetParameter(Name);
        if (parameter == null)
        {
            return factory.CreateNextParameters(new[] { expander }, index + 1, context);
        }

        // TODO: This is a file-picker. Free-Text seems to be impossible currently.
        return new RdProjectTemplateTextParameter(
            Name,
            PresentableName,
            parameter.DefaultValue,
            Tooltip,
            RdTextParameterStyle.FileChooser,
            factory.CreateNextParameters(new[] { expander }, index + 1, context));
    }

    public class CakeAliasCategory : TextTemplateProviders
    {
        public CakeAliasCategory()
            : base("cakeAliasCategory", "Category", "Cake alias category")
        {
        }
    }
}
