#load nuget:?package=Cake.IntelliJ.Recipe&version=0.3.0

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
  shouldRunPluginVerifier: !IsRunningOnLinux(),
  intelliJAnalyzerTasks: new[]{ "detekt", "verifyPlugin" }, // skip ktlintCheck for now
  preferredBuildProviderType: BuildProviderType.GitHubActions,
  preferredBuildAgentOperatingSystem: PlatformFamily.Windows
);

IntelliJBuildParameters.PrintParameters(Context);

ToolSettings.SetToolPreprocessorDirectives(
  gitReleaseManagerGlobalTool: "#tool dotnet:?package=GitReleaseManager.Tool&version=0.17.0");

ToolSettings.SetToolSettings(context: Context);

IntelliJBuild.Run();
