---
guid: 63616b65-5eb1-47ca-809e-7a8e39a162de
type: Live
reformat: True
shortenReferences: True
scopes: InFrostingCSharpFile
parameterOrder: Name#1, NameType, BuildContext#1
NameType-expression: spacestounderstrokes(Name)
---

# cake-async-task-ctx

Create a new async Cake task with a custom build context

```
[TaskName("$Name$")]
public sealed class $NameType$Task : AsyncFrostingTask<$BuildContext$>
{
    public override Task RunAsync($BuildContext$ context)
    {
        context.Information("$Name$ runs.");

        $END$return Task.CompletedTask;
    }
}
```
