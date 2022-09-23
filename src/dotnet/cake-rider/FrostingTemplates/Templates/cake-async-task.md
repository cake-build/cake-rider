---
guid: 63616b65-caa3-4f7f-ae10-06bcbe3d482c
type: Live
image: Cake
reformat: True
shortenReferences: True
scopes: InFrostingCSharpFile
parameterOrder: Name#1, NameType
xxNameType-expression: spacestounderstrokes(Name)
---

# cake-async-task

Create a new async Cake task.

```
[TaskName("$Name$")]
public sealed class $NameType$Task : AsyncFrostingTask
{
    public override Task RunAsync(ICakeContext context)
    {
        context.Information("$Name$ runs.");

        $END$return Task.CompletedTask;
    }
}
```
