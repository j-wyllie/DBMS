package odms.controller;

import odms.data.ProfileDatabase;
import odms.history.History;
import odms.profile.OrganConflictException;

public abstract class UndoRedoController{
    public void redirect(ProfileDatabase currentDatabase, History action) throws Exception{
        if (action.getHistoryAction().equals("added")) {
            added(currentDatabase, action);
        } else if (action.getHistoryAction().equals("deleted")) {
            deleted(currentDatabase, action);
        } else if (action.getHistoryAction().equals("removed")) {
            removed(currentDatabase, action);
        } else if (action.getHistoryAction().equals("set")) {
            set(currentDatabase, action);
        } else if (action.getHistoryAction().equals("donate")) {
            donate(currentDatabase, action);
        } else if (action.getHistoryAction().equals("update")) {
            update(currentDatabase, action);
        } else if (action.getHistoryAction().equals("EDITED")) {
            edited(currentDatabase, action);
        } else if (action.getHistoryAction().equals("updated")) {
            updated(action);
        } else if (action.getHistoryAction().equals("added drug")) {
            addDrug(currentDatabase, action);
        } else if (action.getHistoryAction().equals("removed drug")) {
            deleteDrug(currentDatabase, action);
        } else if (action.getHistoryAction().equals("stopped")) {
            stopDrug(currentDatabase, action);
        } else if (action.getHistoryAction().equals("started")) {
            renewDrug(currentDatabase, action);
        } else if (action.getHistoryAction().equals("added condition")) {
            addCondition(currentDatabase,action);
        } else if (action.getHistoryAction().equals("removed condition")) {
            removedCondition(currentDatabase,action);
        } else if (action.getHistoryAction().equals("received")) {
            addedReceived(currentDatabase, action);
        } else if (action.getHistoryAction().equals("donated")) {
            addedDonated(currentDatabase, action);
        }
    }

    public abstract void added(ProfileDatabase currentDatabase, History action);

    public abstract void deleted(ProfileDatabase currentDatabase, History action);

    public abstract void removed(ProfileDatabase currentDatabase, History action) throws Exception;

    public abstract void set(ProfileDatabase currentDatabase, History action) throws OrganConflictException;

    public abstract void donate(ProfileDatabase currentDatabase, History action);

    public abstract void update(ProfileDatabase currentDatabase, History action);

    public abstract void edited(ProfileDatabase currentDatabase, History action);

    public abstract void updated(History action);

    public abstract void addDrug(ProfileDatabase currentDatabase, History action) throws IndexOutOfBoundsException;

    public abstract void deleteDrug(ProfileDatabase currentDatabase, History action)throws IndexOutOfBoundsException;

    public abstract void stopDrug(ProfileDatabase currentDatabase, History action)throws IndexOutOfBoundsException;

    public abstract void renewDrug(ProfileDatabase currentDatabase, History action)throws IndexOutOfBoundsException;

    public abstract void addCondition(ProfileDatabase currentDatabase, History action);

    public abstract void removedCondition(ProfileDatabase currentDatabase, History action);

    public abstract void addedReceived(ProfileDatabase currentDatabase, History action);

    public abstract void addedDonated(ProfileDatabase currentDatabase, History action);


}
