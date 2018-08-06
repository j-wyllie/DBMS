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

    // profile Commands
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

    // user commands TODO need to replace most clinician stuff with user, works for now

    // Organ Commands
    ORGANADD,
    RECEIVERADD,
    ORGANREMOVE,
    RECEIVEREMOVE,
    ORGANDONATE,

    // Sql Commands
    SQLREADONLY,
    RESET,
    RESAMPLE;


    public static ArgumentCompleter commandAutoCompletion() {
        return new ArgumentCompleter(

                new StringsCompleter("create-clinician"),
                new StringsCompleter("create-profile"),

                new StringsCompleter("delete-clinician"),
                new StringsCompleter("delete-profile"),

                new StringsCompleter("help"),
                new StringsCompleter("print all"),
                new StringsCompleter("print donors"),

                new StringsCompleter("export"),
                new StringsCompleter("import"),

                new StringsCompleter("print all profiles"),
                new StringsCompleter("print donors"),
                new StringsCompleter("print all users"),
                new StringsCompleter("print clinicians"),

                new StringsCompleter("db-read"),
                new StringsCompleter("reset"),
                new StringsCompleter("resample")
        );
    }
}
