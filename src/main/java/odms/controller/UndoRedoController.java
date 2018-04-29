package odms.controller;

import odms.cli.CommandUtils;

public class UndoRedoController {

    public static void undo(){
        CommandUtils.undo(GuiMain.getCurrentDatabase());
    }

    public static void redo(){
        CommandUtils.redo(GuiMain.getCurrentDatabase());
    }

}
