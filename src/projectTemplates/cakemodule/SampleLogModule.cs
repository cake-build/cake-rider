using Cake.Core.Annotations;
using Cake.Core.Composition;
using Cake.Core.Diagnostics;

[assembly: CakeModule(typeof(SampleLogModule.Module))]

namespace SampleLogModule
{
    public class Module : ICakeModule
    {
        public void Register(ICakeContainerRegistrar registrar)
        {
            registrar.RegisterType<ReverseLog>().As<ICakeLog>().Singleton();
        }
    }
}
