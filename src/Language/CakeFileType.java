package Language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by achapman on 6/02/17.
 */
public class CakeFileType extends LanguageFileType {
    public static final CakeFileType INSTANCE = new CakeFileType();

    private CakeFileType() {
        super(CakeLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Cake Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Cake build script";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "cake";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return CakeIcons.FILE;
    }
}
