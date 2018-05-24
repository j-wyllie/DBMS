package odms.cli;

import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

public enum Commands {
    INVALID,

    // General Commands
    HELP,
    PRINTALLUSERS,
    PRINTALLPROFILES,
    PRINTCLINICIANS,
    PRINTDONORS,
    REDO,
    UNDO,

    // IO Commands
    EXPORT,
    IMPORT,

    // Profile Commands
    PROFILECREATE,
    PROFILEDATECREATED,
    PROFILEDELETE,
    PROFILEDONATIONS,
    PROFILEUPDATE,
    PROFILEVIEW,

    // Clinician Commands
    CLINICIANCREATE,
    CLINICIANDATECREATED,
    CLINICIANDELETE,
    PCLINICIANDONATIONS,
    CLINICIANUPDATE,
    CLINICIANEVIEW,

    // User commands TODO need to replace most clinician stuff with user, works for now

    // Organ Commands
    ORGANADD,
    ORGANREMOVE,
    ORGANDONATE;

    public static ArgumentCompleter commandAutoCompletion() {
        return new ArgumentCompleter(

            new StringsCompleter("create-clinician"),
            new StringsCompleter("create-profile"),

            new StringsCompleter("delete-clinician"),
            new StringsCompleter("delete-profile"),

            new StringsCompleter("help"),

            new StringsCompleter("export"),
            new StringsCompleter("import"),

            new StringsCompleter("print all profiles"),
            new StringsCompleter("print donors"),
            new StringsCompleter("print all users"),
            new StringsCompleter("print clinicians")
        );
    }
}
