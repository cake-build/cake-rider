#$ErrorActionPreference = 'Stop'
# $ErrorActionPreference = 'Stop' will break the build on *any* output to stderr.
# i.e. calling `java -version` will break the build in that case.
# This might be solved in PS 7.1, see https://docs.microsoft.com/en-us/powershell/scripting/whats-new/what-s-new-in-powershell-71?view=powershell-7.1#breaking-changes-and-improvements

Set-Location -LiteralPath $PSScriptRoot

$env:DOTNET_SKIP_FIRST_TIME_EXPERIENCE = '1'
$env:DOTNET_CLI_TELEMETRY_OPTOUT = '1'
$env:DOTNET_NOLOGO = '1'

dotnet tool restore
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

dotnet cake recipe.cake @args
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
