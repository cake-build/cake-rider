import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.jetbrains.rider.util.reflection.P;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;

/**
 * Created by achapman on 9/02/17.
 */
public abstract class CakeMenuAction extends AnAction {
    protected String getResource(String resourceName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceStream = this.getClass().getResourceAsStream(resourceName);
        StringWriter writer = new StringWriter();
        IOUtils.copy(resourceStream, writer);
        resourceStream.close();
        return writer.toString();
    }

    protected File getProjectFile(AnActionEvent event) {
        String virtualProjectFile = getProject(event).getBasePath();
        File projectFile = new File(virtualProjectFile);
        return projectFile;
    }

    protected Project getProject(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        return project;
    }

    protected Boolean addResource(String source, File targetFile) throws IOException {
        if (!targetFile.exists()) try {
            targetFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        String content = getResource(source);
        FileWriter writer = new FileWriter(targetFile);
        writer.write(content);
        writer.close();
        return true;
    }

    protected Boolean downloadFile(String source, File targetFile) throws MalformedURLException {
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            Boolean delete = targetFile.delete();
            if (!delete) return false;
        }
        DownloadClient client = new DownloadClient(source);
        try {
            client.downloadToFile(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void showMessage(AnActionEvent event, String message) {
        showMessage(event, message);
    }

    void showMessage(AnActionEvent event, String message, String title) {
        showMessage(getProject(event), message);
    }

    void showMessage(Project project, String message) {
        showMessage(project, message, "Information");
    }

    void showMessage(Project project, String message, String title) {
        Messages.showMessageDialog(project, message, title, Messages.getInformationIcon());
    }
}
