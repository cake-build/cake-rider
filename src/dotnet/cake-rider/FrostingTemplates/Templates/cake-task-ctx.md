---
guid: 63616b65-5a78-4ba7-ae59-e1065ee624f8
type: Live
reformat: True
shortenReferences: True
scopes: InCSharpTypeAndNamespace(minimumLanguageVersion=2.0);InCSharpTypeMember(minimumLanguageVersion=2.0);InFrostingProject
parameterOrder: Task_Name#1, NameType, BuildContext#1
NameType-expression: spacestounderstrokes(Task_Name)
---

# cake-task-ctx

Create a new Cake task with a custom build context

```
[TaskName("$Task_Name$")]
public sealed class $NameType$Task : FrostingTask<$BuildContext$>
{
    public override void Run($BuildContext$ context)
    {
        $END$context.Information("$Task_Name$ runs.");
    }
}
```
