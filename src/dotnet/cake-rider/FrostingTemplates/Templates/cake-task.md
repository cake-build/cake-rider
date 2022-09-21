---
guid: 63616b65-7c30-4970-adcd-e3e3e2c499d9
type: Live
reformat: True
shortenReferences: True
scopes: InCSharpTypeAndNamespace(minimumLanguageVersion=2.0);InFrostingCSharpFile
parameterOrder: Name#1, NameType
NameType-expression: spacestounderstrokes(Name)
---

# cake-task

define a new Cake task

```
[TaskName("$Name$")]
public sealed class $NameType$Task : FrostingTask
{
    public override void Run(ICakeContext context)
    {
        $END$context.Information("$Name$ runs.");
    }
}
```
