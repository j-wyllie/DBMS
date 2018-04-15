package odms.cli;

import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

public enum Commands {
    INVALID,

    // General Commands
    HELP,
    PRINTALL,
    PRINTDONORS,
    UNDO,
    REDO,

    // IO Commands
    EXPORT,
    IMPORT,

    // Profile Commands
    PROFILECREATE,
    PROFILEDELETE,
    PROFILEVIEW,

    // Profile Commands
    PROFILEDATECREATED,
    PROFILEDONATIONS,
    PROFILEUPDATE,

    // Orgon Commands
    ORGANADD,
    ORGANREMOVE,
    ORGANDONATE;

    public static ArgumentCompleter commandAutoCompletion() {
        return new ArgumentCompleter(
            new StringsCompleter("help"),
            new StringsCompleter("print-all"),
            new StringsCompleter("print-donors"),

            new StringsCompleter("export"),
            new StringsCompleter("import"),

            new StringsCompleter("create-profile"),
            new StringsCompleter("delete-profile")
        );
    }
}
