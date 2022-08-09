using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Metadata.Reader.API;
using JetBrains.Metadata.Reader.Impl;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Impl.Types;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.Util;

namespace net.cakebuild;

public class CakePredefinedType
{
    public static readonly IClrTypeName IFrostingTaskFqn = new ClrTypeName("Cake.Frosting.IFrostingTask");
    public IDeclaredType IFrostingTask => CreateType(IFrostingTaskFqn);
    
    public static readonly IClrTypeName TaskNameAttributeFqn = new ClrTypeName("Cake.Frosting.TaskNameAttribute");
    public IDeclaredType TaskNameAttribute => CreateType(TaskNameAttributeFqn);

    private static readonly Dictionary<IClrTypeName, int> _predefinedTypeNamesIndex = new();

    static CakePredefinedType()
    {
        foreach (var fieldInfo in typeof(CakePredefinedType).GetFields())
        {
            if (fieldInfo.IsStatic && typeof(IClrTypeName).IsAssignableFrom(fieldInfo.FieldType))
            {
                var clrTypeName = (IClrTypeName)fieldInfo.GetValue(null);
                _predefinedTypeNamesIndex.Add(clrTypeName, _predefinedTypeNamesIndex.Count);
            }
        }
    }

    private readonly IPsiModule _module;
    private readonly IDeclaredType[] _types = new IDeclaredType[_predefinedTypeNamesIndex.Count];

    internal CakePredefinedType(IPsiModule module)
    {
        _module = module;
    }

    private IDeclaredType CreateType(IClrTypeName clrName)
    {
        return CreateType(_predefinedTypeNamesIndex[clrName], clrName);
    }

    private IDeclaredType CreateType(int index, IClrTypeName clrName)
    {
        if (_types[index] == null)
        {
            lock (_types)
            {
                _types[index] ??= new PredefinedDeclaredTypeFromClrName(clrName, NullableAnnotation.Unknown, _module);
            }
        }

        return _types[index];
    }

    public IDeclaredType TryGetType([NotNull] IClrTypeName clrTypeName)
    {
        return _predefinedTypeNamesIndex.TryGetValue(clrTypeName, out var index) ? CreateType(index, clrTypeName) : null;
    }

    private class PredefinedDeclaredTypeFromClrName : DeclaredTypeFromCLRName
    {
        public PredefinedDeclaredTypeFromClrName(IClrTypeName clrName, NullableAnnotation annotation, IPsiModule module) : base(clrName, annotation, module)
        {
        }

        protected override ITypeElement ChooseBestCandidate(ICollection<ITypeElement> candidates)
        {
            var typeElement = System.Linq.Enumerable.FirstOrDefault(candidates);
            if (typeElement != null)
            {
                return base.ChooseBestCandidate(candidates);
            }

            var localList = default(LocalList<ITypeElement>);
            foreach (var typeElement2 in candidates)
            {
                var moduleReference = Module.GetModuleReference(typeElement2.Module);
                if (moduleReference == null || moduleReference.HasGlobalExternAlias())
                {
                    localList.Add(typeElement2);
                }
            }

            if (localList.Count == 1)
            {
                return localList[0];
            }

            localList.Clear();
            foreach (var typeElement3 in candidates)
            {
                if (typeElement3.Module is IAssemblyPsiModule)
                {
                    localList.Add(typeElement3);
                }
            }

            if (localList.Count == 1)
            {
                return localList[0];
            }

            return base.ChooseBestCandidate(candidates);
        }
    }
}
