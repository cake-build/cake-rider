package Language;

import com.intellij.lang.Language;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by achapman on 6/02/17.
 */
public class CakeLanguage extends Language {
    public static final CakeLanguage INSTANCE = new CakeLanguage();
    static final String Name = "Cake";
    protected CakeLanguage() {
        super(Name);
    }

    protected CakeLanguage(@NotNull String... mimeTypes) {
        super(Name, mimeTypes);
    }

    protected CakeLanguage(@Nullable Language baseLanguage, @NotNull String... mimeTypes) {
        super(baseLanguage, Name, mimeTypes);
    }

    protected CakeLanguage(boolean register) {
        super(Name, register);
    }
}
