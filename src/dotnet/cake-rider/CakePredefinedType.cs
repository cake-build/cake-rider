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
    // ReSharper disable MemberCanBePrivate.Global
    public static readonly IClrTypeName TaskNameAttributeFqn = new ClrTypeName("Cake.Frosting.TaskNameAttribute");

    // ReSharper disable once InconsistentNaming
    public static readonly IClrTypeName IFrostingTaskFqn = new ClrTypeName("Cake.Frosting.IFrostingTask");

    // ReSharper restore MemberCanBePrivate.Global
    private static readonly Dictionary<IClrTypeName, int>
        PredefinedTypeNamesIndex = new Dictionary<IClrTypeName, int>();

    private readonly IPsiModule _module;

    private readonly IDeclaredType[] _types = new IDeclaredType[PredefinedTypeNamesIndex.Count];

    static CakePredefinedType()
    {
        foreach (var fieldInfo in typeof(CakePredefinedType).GetFields())
        {
            if (fieldInfo.IsStatic && typeof(IClrTypeName).IsAssignableFrom(fieldInfo.FieldType))
            {
                var clrTypeName = (IClrTypeName)fieldInfo.GetValue(null);
                PredefinedTypeNamesIndex.Add(clrTypeName, PredefinedTypeNamesIndex.Count);
            }
        }
    }

    internal CakePredefinedType(IPsiModule module)
    {
        _module = module;
    }

    // ReSharper disable once InconsistentNaming
    public IDeclaredType IFrostingTask => CreateType(IFrostingTaskFqn);

    public IDeclaredType TaskNameAttribute => CreateType(TaskNameAttributeFqn);

    public IDeclaredType TryGetType([NotNull] IClrTypeName clrTypeName)
    {
        return PredefinedTypeNamesIndex.TryGetValue(clrTypeName, out var index) ? CreateType(index, clrTypeName) : null;
    }

    private IDeclaredType CreateType(IClrTypeName clrName)
    {
        return CreateType(PredefinedTypeNamesIndex[clrName], clrName);
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

    private class PredefinedDeclaredTypeFromClrName : DeclaredTypeFromCLRName
    {
        public PredefinedDeclaredTypeFromClrName(IClrTypeName clrName, NullableAnnotation annotation, IPsiModule module)
            : base(clrName, annotation, module)
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
