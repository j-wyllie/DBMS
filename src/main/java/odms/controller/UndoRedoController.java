package odms.controller;

import odms.commandlineview.CommandUtils;

public class UndoRedoController {

    public static void undo(){
        CommandUtils.undo(Main.getCurrentDatabase());
    }

    public static void redo(){
        CommandUtils.redo(Main.getCurrentDatabase());
    }

}
