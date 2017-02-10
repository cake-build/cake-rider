import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by achapman on 6/02/17.
 */
public class CakeScriptInstallerAction extends CakeMenuAction {

    public void actionPerformed(AnActionEvent event) {
        Project project = getProject(event);
        File projectFile = getProjectFile(event);
        Boolean installed = false;
        try {
            File targetFile = new File(projectFile, "build.cake");
            installed = addResource("Resources/build.cake", targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog(project, "Failed to write script template to file", "IO Error");
        }
        if (installed) {
            String message = "Cake build script successfully downloaded to " + new File(projectFile, "build.cake").getAbsolutePath() + System.lineSeparator() + "Due to limitations in Rider plugins, you will need to manually add this file to " + project.getName();
            showMessage(project, message);
        } else {
            showMessage(project, "There was an error while downloading the file.");
        }
    }
}