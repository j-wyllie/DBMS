package odms.commandlineview;

public enum Commands {
    INVALID,

    // General Commands
    HELP,
    PRINTALL,
    PRINTDONORS,

    // IO Commands
    EXPORT,
    IMPORT,

    // Profile Commands
    PROFILECREATE,
    PROFILEDELETE,
    PROFILEVIEW,

    // Donor Commands
    DONORDATECREATED,
    DONORDONATIONS,
    DONORUPDATE,

    // Orgon Commands
    ORGANADD,
    ORGANREMOVE,
    ORGANDONATE

}
