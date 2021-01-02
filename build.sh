#!/bin/bash
dotnet tool restore

dotnet cake recipe.cake --bootstrap

dotnet cake recipe.cake "$@"