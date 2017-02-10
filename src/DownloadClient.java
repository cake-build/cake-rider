import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

/**
 * Created by achapman on 6/02/17.
 */
class DownloadClient {
    private final URL _url;
    private final Project _project;

    DownloadClient(String url) throws MalformedURLException {
        this(null, new URL(url));
    }

    DownloadClient(Project project, String url) throws MalformedURLException {
        this(project, new URL(url));
    }

    DownloadClient(Project project, URL url) {
        this._url = url;
        this._project = project;
    }

    void downloadToFile(File targetFile) throws IOException {
        File file = File.createTempFile("download", "file");
        FileUtils.copyURLToFile(_url, file);
        FileUtils.copyFile(file, targetFile);
    }

    void downloadToFile(String filePath) throws IOException {
        downloadToFile(new File(filePath));
    }
}
