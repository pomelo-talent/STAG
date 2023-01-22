package edu.uob;

import java.util.HashSet;

public class GameAction
{
    private String actionType=null;
    private String[] actionTypeList = {"subjects", "consumed", "produced", "narration"};
    private String actionName = null;

    public GameAction(String inputAction, String inputActionType) {
        for (int i=0; i<actionTypeList.length; i++) {
            if (inputActionType.equals(actionTypeList[i])) {
                actionType = actionTypeList[i];
                actionName = inputAction;
            }
        }
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionName() {
        return actionName;
    }
}
