#!/bin/bash

export DOTNET_SKIP_FIRST_TIME_EXPERIENCE=1
export DOTNET_CLI_TELEMETRY_OPTOUT=1
export DOTNET_NOLOGO=1

dotnet tool restore

dotnet cake recipe.cake --bootstrap

dotnet cake recipe.cake "$@"