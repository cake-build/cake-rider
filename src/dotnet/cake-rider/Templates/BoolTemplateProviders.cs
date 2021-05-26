using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Host.Features.ProjectModel.ProjectTemplates.DotNetExtensions;
using JetBrains.ReSharper.Host.Features.ProjectModel.ProjectTemplates.DotNetTemplates;
using JetBrains.Rider.Model;

namespace net.cakebuild.Templates
{
    public abstract class BoolTemplateProviders : DotNetTemplateParameter
    {
        private BoolTemplateProviders(
            [NotNull] string name,
            [NotNull] string presentableName,
            [CanBeNull] string tooltip) 
            : base(name, presentableName, tooltip)
        {
        }

        public override RdProjectTemplateContent CreateContent(DotNetProjectTemplateExpander expander, IDotNetTemplateContentFactory factory,
            int index, IDictionary<string, string> context)
        {
            var parameter = expander.TemplateInfo.GetParameter(Name);
            if (parameter == null)
            {
                return factory.CreateNextParameters(new[] {expander}, index + 1, context);
            }

            var boolOptions = new Dictionary<string, string>
            {
                {"true","Yes"},
                {"false","No"}
            };
            var uiOptions = boolOptions.Select(o =>
            {
                var content = factory.CreateNextParameters(new[] {expander}, index + 1, context);
                return new RdProjectTemplateGroupOption(o.Key, o.Value, null, content);
            }).ToList();
                
            return new RdProjectTemplateGroupParameter(
                Name,
                PresentableName,
                parameter.DefaultValue,
                Tooltip,
                uiOptions);
        }
        
        public class EmptyTemplate : BoolTemplateProviders
        {
            public EmptyTemplate()
                : base("empty", "Create empty", "Create a minimal (empty) project")
            {
            }
        }
        
        public class AddNet5 : BoolTemplateProviders
        {
            public AddNet5()
                : base("addNet5", "Target net5.0", "additionally target net5.0")
            {
            }
        }

        public class AddNet461 : BoolTemplateProviders
        {
            public AddNet461()
                : base("addNet461", "Target net461", "additionally target net461")
            {
            }
        }

    }
}