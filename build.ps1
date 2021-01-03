#$ErrorActionPreference = 'Stop'
# $ErrorActionPreference = 'Stop' will break the build on *any* output to stderr. 
# i.e. calling `java -version` will break the build in that case.
# This might be solved in PS 7.1, see https://docs.microsoft.com/en-us/powershell/scripting/whats-new/what-s-new-in-powershell-71?view=powershell-7.1#breaking-changes-and-improvements

function Run([string[]]$arguments) {
	$cmd = @("& dotnet")
	$cmd += $arguments
	$cmdLine = $cmd -join " "
	Write-Verbose "> $cmdLine"
	Invoke-Expression $cmdLine

	if ($LASTEXITCODE -ne 0) {
		Write-Host "Non-Zero exit code ($($LASTEXITCODE)), exiting..."
		exit $LASTEXITCODE
	}
}

Run tool, restore

Run cake, recipe.cake, --bootstrap

$arguments = @("cake"; "recipe.cake")
$arguments += @($args)

Run $arguments