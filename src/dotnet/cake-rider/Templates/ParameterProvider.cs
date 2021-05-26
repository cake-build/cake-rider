using System.Collections.Generic;
using JetBrains.Application;
using JetBrains.ReSharper.Host.Features.ProjectModel.ProjectTemplates.DotNetExtensions;

namespace net.cakebuild.Templates
{
    [ShellComponent]
    public class ParameterProvider : IDotNetTemplateParameterProvider
    {
        public int Priority => 50;

        public IReadOnlyCollection<DotNetTemplateParameter> Get()
        {
            return new DotNetTemplateParameter[]
            {
                new BoolTemplateProviders.EmptyTemplate(),
                new BoolTemplateProviders.AddNet5(),
                new BoolTemplateProviders.AddNet461(),
                //new TextTemplateProviders.CakeAliasCategory()
            };
        }
    }
}