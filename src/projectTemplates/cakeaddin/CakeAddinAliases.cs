using Cake.Core;
using Cake.Core.Annotations;
using Cake.Core.Diagnostics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CakeAddin
{
    [CakeAliasCategory("Sample")]
    public static class AddinAliases
    {
        [CakeMethodAlias]
        public static void Hello(this ICakeContext ctx, string name)
        {
            ctx.Log.Information("Hello " + name);
        }
    }
}
