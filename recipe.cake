#load nuget:?package=Cake.IntelliJ.Recipe&version=0.1.2

Environment.SetVariableNames();

BuildParameters.SetParameters(
  context: Context,
  buildSystem: BuildSystem,
  sourceDirectoryPath: "./rider",
  title: "Cake-Rider",
  repositoryOwner: "cake-build",
  marketplaceId: "15729-cake-rider"
);

BuildParameters.PrintParameters(Context);

ToolSettings.SetToolSettings(context: Context);

Build.Run();
