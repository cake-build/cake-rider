using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.Application.UI.ActionSystem.ActionsRevised.Menu;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util;

namespace net.cakebuild.Actions
{
    [Action("CakeRiderDotnetTestAction", "")]
    public class CakeRiderDotnetTestAction : IExecutableAction
    {
        public bool Update(
            IDataContext context,
            ActionPresentation presentation,
            DelegateUpdate nextUpdate)
        {
            return true;
        }

        public void Execute(IDataContext context, DelegateExecute nextExecute)
        {
            MessageBox.ShowInfo("Hello world from the dotnet Component!");
        }
    }
}