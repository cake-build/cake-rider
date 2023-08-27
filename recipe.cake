#load nuget:?package=Cake.IntelliJ.Recipe&version=0.2.4

Environment.SetVariableNames(
  githubTokenVariable: "GITHUB_PAT"
);

IntelliJBuildParameters.SetParameters(
  context: Context,
  buildSystem: BuildSystem,
  sourceDirectoryPath: "./src/rider",
  title: "Cake for Rider",
  repositoryName: "Cake-Rider",
  repositoryOwner: "cake-build",
  marketplaceId: "15729-cake-rider",
  webLinkRoot: "", // do *not* create a virtual directory for wyam docs. This setting will break gh-pages. (But work for preview)
  wyamConfigurationFile: MakeAbsolute((FilePath)"docs/wyam.config"),
  preferredBuildProviderType: BuildProviderType.GitHubActions,
  preferredBuildAgentOperatingSystem: PlatformFamily.Linux
);

BuildParameters.IsDotNetCoreBuild = true; // so we get all the cool dotnet tools
IntelliJBuildParameters.PrintParameters(Context);

ToolSettings.SetToolSettings(context: Context);

IntelliJBuild.Run();
