using System.Collections.Concurrent;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.Metadata.Reader.API;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.Impl;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.Util;

namespace net.cakebuild;

public static class CakePredefinedTypeExtensions
{
    public static bool IsPredefinedType([CanBeNull] IType type, [NotNull] IClrTypeName clrName)
    {
        var declaredType = type as IDeclaredType;
        return declaredType != null && IsPredefinedTypeElement(declaredType.GetTypeElement(), clrName);
    }

    private static bool IsPredefinedTypeElement([CanBeNull] ITypeElement typeElement, [NotNull] IClrTypeName clrName)
    {
        if (typeElement == null)
        {
            return false;
        }

        var typeElement2 = typeElement.Module.GetCakePredefinedType().TryGetType(clrName).NotNull("NOT PREDEFINED").GetTypeElement();
        return DeclaredElementEqualityComparer.TypeElementComparer.Equals(typeElement, typeElement2);
    }

    private static readonly Key<CakePredefinedTypeCache> PredefinedTypeCacheKey = new("PredefinedTypeCache");

    public static CakePredefinedType GetCakePredefinedType([NotNull] this IPsiModule module)
    {
        return module.GetOrCreateDataNoLock(PredefinedTypeCacheKey, module, static m => m.GetPsiServices().GetComponent<CakePredefinedTypeCache>()).GetOrCreatePredefinedType(module);
    }

    [PsiComponent]
    internal class CakePredefinedTypeCache : InvalidatingPsiCache
    {
        private readonly ConcurrentDictionary<IPsiModule, CakePredefinedType> _predefinedTypes = new();

        public CakePredefinedType GetOrCreatePredefinedType(IPsiModule module)
        {
            return _predefinedTypes.GetOrAdd(module, m => new CakePredefinedType(m));
        }

        protected override void InvalidateOnPhysicalChange(PsiChangedElementType elementType)
        {
            if (elementType == PsiChangedElementType.InvalidateCached)
            {
                return;
            }

            _predefinedTypes.Clear();
        }
    }
}
