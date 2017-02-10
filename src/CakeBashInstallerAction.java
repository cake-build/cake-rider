import Resources.Paths;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by achapman on 10/02/17.
 */
public class CakeBashInstallerAction extends CakeMenuAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = getProject(event);
        File projectFile = getProjectFile(event);
        Boolean installed = false;
        try {
            File targetFile = new File(projectFile, "build.sh");
            installed = downloadFile(Paths.bashBootstrapper, targetFile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Messages.showErrorDialog(project, "Invalid URI for bootstrapper. Please raise a GitHub issue for this error!", "URI Error");
        }
        if (installed) {
            String message = "Bootstrapper successfully downloaded to " + new File(projectFile, "build.sh").getAbsolutePath() + System.lineSeparator() + "Due to limitations in Rider plugins, you will need to manually add this file to " + project.getName();
            showMessage(project, message);
        } else {
            showMessage(project, "There was an error while downloading the file.");
        }
    }
}
