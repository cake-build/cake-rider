using System;
using System.Linq;

using JetBrains.ReSharper.Psi;

namespace net.cakebuild;

public static class Extensions
{
    public static bool IsCakeFrostingTask(this IClass klass)
    {
        if (klass.IsAbstract)
        {
            return false;
        }

        if (!klass.IsDescendantOf(klass.Module.GetCakePredefinedType().IFrostingTask.GetTypeElement()))
        {
            return false;
        }

        return true;
    }

    public static string GetCakeFrostingTaskName(this IClass klass)
    {
        if (!klass.IsCakeFrostingTask())
        {
            throw new ArgumentException("Given type is not a cake task", nameof(klass));
        }

        var taskNameAttribute = klass.GetAttributeInstances(CakePredefinedType.TaskNameAttributeFqn, false).SingleOrDefault();
        return taskNameAttribute != null ? taskNameAttribute.PositionParameter(0).ConstantValue.StringValue : klass.ShortName;
    }
}
