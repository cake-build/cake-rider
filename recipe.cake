#load nuget:?package=Cake.IntelliJ.Recipe&version=0.1.0

Environment.SetVariableNames();

BuildParameters.SetParameters(
  context: Context,
  buildSystem: BuildSystem,
  sourceDirectoryPath: "./rider",
  title: "Cake-Rider",
  repositoryOwner: "cake-contrib",
  marketplaceId: "15729-cake-rider"
);

BuildParameters.PrintParameters(Context);

ToolSettings.SetToolSettings(context: Context);

Build.Run();
