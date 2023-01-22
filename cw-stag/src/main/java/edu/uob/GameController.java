package edu.uob;

import java.util.*;

public class GameController {
    private GameParser ThisGame;
    private EntityLocation currentLocation;
    private EntityPlayer currentPlayer;
    private ArrayList<EntityPlayer> playersList;
    private String basicText;
    private String narrationText;
    private String errorText;

    public GameController(GameParser parser) {
        ThisGame = parser;
        currentLocation = ThisGame.getLocationList().get(0);
        currentPlayer = null;
        playersList = new ArrayList<>();
    }

    public GameParser getThisGame() {
        return ThisGame;
    }

    public EntityLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(EntityLocation locationName) {
        if (locationName == null) {
            currentLocation = ThisGame.getLocationList().get(0);
        } else {
            currentLocation = locationName;
        }
    }

    public ArrayList<EntityPlayer> getPlayersList() {
        return playersList;
    }

    public EntityPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(EntityPlayer playerName) {
        currentPlayer = playerName;
    }

    public void addNewPlayer(EntityPlayer newPlayerName) {
        playersList.add(newPlayerName);
    }

    public boolean isCommandValid(String[] commandArray) {
        resetErrorText();

        if (containsBasicCommand(commandArray)) {
            resetNarrationText();
            basicText = generateBasicText(commandArray);
            if (basicText!=null) {
                return true;
            }
            return false;
        }

        resetBasicText();
        String TriggerActionName = getTriggerActionName(commandArray);
        if (TriggerActionName!=null) {
            if (hasSubject(TriggerActionName,commandArray)) {
                performAction(TriggerActionName);
                generateNarrationText(TriggerActionName);
                checkDeath();
                return true;
            }
        }
        return false;
    }

    public boolean containsBasicCommand(String[] commandArray) {
        //System.out.println(commandArray[1]);
        //System.out.println(ThisGame.getBasicCommands().getBasicCommandsHashset().size());

        if (ThisGame.getBasicCommands().getBasicCommandsHashset().contains(commandArray[0])) {
            return true;
        }
        return false;
    }

    private String generateBasicText(String[] commandArray) {
        GameText currentText = new GameText(this);
        String basicCommand = getBasicCommand(commandArray);
        if (basicCommand==null) {return null;}
        if (basicCommand.equals("look") ||
            basicCommand.equals("inventory") ||
            basicCommand.equals("inv") ||
            basicCommand.equals("health")) {
            if (commandArray.length>1) {
                errorText = "Your basic command is too long";
                return null;
            }
            return currentText.generateText(basicCommand);
        }
        else if (commandArray.length>1) {
            int commandArrayLength = commandArray.length;
            //String entityPhrase = String.join(" ", Arrays.copyOfRange(commandArray, 1,commandArrayLength));
            //System.out.println(commandArray[1]);
            String entityForBasicCommand = extractEntityForBasicCommand(commandArray);
            if (entityForBasicCommand==null) {return null;}
            //System.out.println(entityForBasicCommand);
            if (basicCommand.equals("get") && isGetEntity(entityForBasicCommand)) {
                return currentText.generateText(basicCommand, entityForBasicCommand);
            } else if (basicCommand.equals("drop") && isDropEntity(entityForBasicCommand)) {
                return currentText.generateText(basicCommand, entityForBasicCommand);
            } else if (basicCommand.equals("goto") && isGotoNewLocation(entityForBasicCommand)) {
                return currentText.generateText(basicCommand, entityForBasicCommand);
            }
        }
        return null;
    }

    private String getBasicCommand(String[] commandArray) {
        if (commandArray.length > 2) {
            errorText = "Your basic command is too long";
            return null;
        } else if(commandArray.length < 1) {
            errorText = "Your basic command is too short";
            return null;
        }
        return commandArray[0];
    }

    private String extractEntityForBasicCommand(String[] commandArray) {
        HashSet<String> allEntitiesNameInCommand = getEntitiesAndLocationInCommand(commandArray);
        if (allEntitiesNameInCommand.size()>1) {
            System.out.println(allEntitiesNameInCommand);
            errorText = "There are some useless entities for this basic command";
            return null;
        }
        if (allEntitiesNameInCommand.size()<0) {
            errorText = "There is no entity for this basic command";
        }
        String result = String.join("", allEntitiesNameInCommand);
        return result;
    }

    private HashSet<String> getEntitiesAndLocationInCommand(String[] commandArray) {
        HashSet<String> result = new HashSet<>();
        result.addAll(getEntitiesListInCommand(commandArray));
        result.addAll(getLocationsListInCommand(commandArray));
        return result;
    }

    private HashSet<String> getEntitiesListInCommand(String[] commandArray) {
        HashSet<String> allEntitiesName = new HashSet<>();
        for (int i=0; i<commandArray.length; i++) {
            for (GameEntity entity:ThisGame.getEntitiesList().keySet()) {
                if (commandArray[i].equals(entity.getName())) {
                    allEntitiesName.add(entity.getName());
                }
            }
        }
        return allEntitiesName;
    }

    private HashSet<String> getLocationsListInCommand(String[] commandArray) {
        HashSet<String> allLocationsName = new HashSet<>();
        for (int i=0; i<commandArray.length; i++) {
            for (GameEntity location:ThisGame.getLocationList()) {
                if (commandArray[i].equals(location.getName())) {
                    allLocationsName.add(location.getName());
                }
            }
        }
        return allLocationsName;
    }

    // check whether the entity can be "got".
    private boolean isGetEntity(String entity) {
        HashMap<GameEntity, String> entities =ThisGame.getEntitiesList();
        for (GameEntity entityInList:entities.keySet()) {
            if (entity.equals(entityInList.getName()) &&
                entities.get(entityInList).equals(currentLocation.getName())) {
                if (!entityInList.getIsArtefacts()) {
                    errorText = "The entity cannot be picked up as it isn't artefact";
                    return false;
                }
                entities.put(entityInList, currentPlayer.getName());
                return true;
            }
        }
        errorText = "The entity cannot be found in the current location";
        return false;
    }

    private boolean isDropEntity(String entity) {
        HashMap<GameEntity, String> entities =ThisGame.getEntitiesList();
        for (GameEntity entityInList:entities.keySet()) {
            if (entity.equals(entityInList.getName()) &&
                entities.get(entityInList).equals(currentPlayer.getName())) {
                entities.put(entityInList, currentLocation.getName());
                return true;
            }
        }
        errorText = "The entity cannot be found in your inventory";
        return false;
    }

    private boolean isGotoNewLocation(String entity) {
        if (entity.equals(currentLocation.getName())) {
            errorText = "You have already been in this location";
            return false;
        }

        HashMap<String, HashSet<String>> paths = ThisGame.getPathList();
        for (String sourceLocation:paths.keySet()) {
            if (currentLocation.getName().equals(sourceLocation) &&
                paths.get(sourceLocation).contains(entity)) {
                for (int i=0; i<ThisGame.getLocationList().size(); i++) {
                    if (entity.equals(ThisGame.getLocationList().get(i).getName())){
                        currentLocation = ThisGame.getLocationList().get(i);
                        return true;
                    }
                }
            }
        }
        errorText = "The entity(new location) cannot be found";
        return false;
    }

    public String getBasicText() {
        if (basicText!=null) {
            return basicText;}
        else {
            return "";
        }
    }

    private void resetBasicText() {
        basicText = "";
    }

    private String getTriggerActionName(String[] commandArray) {
        HashSet<String> triggerActionNameList= new HashSet<>();
        for (int i=0; i<commandArray.length; i++) {
            if (commandArray[i].equals("look") || commandArray[i].equals("inv") ||
                    commandArray[i].equals("inventory") || commandArray[i].equals("goto") ||
                    commandArray[i].equals("get") || commandArray[i].equals("drop")) {
                errorText = "Your command has some mistakes";
                return null;
            }
            for (String triggerActionName:ThisGame.getActionList().keySet()) {
                if (commandArray[i].equals(triggerActionName)) {
                    triggerActionNameList.add(triggerActionName);
                }
            }
        }

        //System.out.println("this is"+triggerActionNameList);
        if (triggerActionNameList.size()>1) {
            // in case: chop cutdown tree
            //System.out.println(ThisGame.getSynonymousTriggersMap().keySet());
            for (String key:ThisGame.getSynonymousTriggersMap().keySet()) {
                int count = 0;
                for (String triggerActionName:triggerActionNameList) {
                    //System.out.println(triggerActionName);
                    //System.out.println(ThisGame.getSynonymousTriggersMap().get(key));
                    if (ThisGame.getSynonymousTriggersMap().get(key).contains(triggerActionName)) {
                        count++;
                    }
                }
                if (count==triggerActionNameList.size()) {
                    String arr[] = new String[triggerActionNameList.size()];
                    triggerActionNameList.toArray(arr);
                    return arr[0];
                }
            }
            //System.out.println(triggerActionNameList);
            errorText = "One action at a time please";
            return null;
        } else if (triggerActionNameList.size()<1) {
            errorText = "Your action cannot be found";
            return null;
        }
        //System.out.println(String.join("", triggerActionNameList));
        return String.join("", triggerActionNameList);
    }

    private boolean hasSubject(String actionName, String[] commandArray) {
        TreeMap<String, HashSet<GameAction>> actionList = ThisGame.getActionList();
        boolean hasSubject = false;

        HashSet<String> allEntitiesInCommand = getEntitiesListInCommand(commandArray);
        HashSet<String> entitiesInLocation = getEntitiesListByPlace(currentLocation.getName());
        HashSet<String> entitiesInPlayerInv = getEntitiesListByPlace(currentPlayer.getName());
        HashSet<String> subjectListInActionList = new HashSet<>();
        HashSet<String> subjectList = new HashSet<>();
        for (int i=0; i<commandArray.length; i++) {
            for (GameAction action:actionList.get(actionName)) {
                if (action.getActionType().equals("subjects")) {
                    if (commandArray[i].equals(action.getActionName())) {
                        hasSubject = true;
                    }
                    subjectListInActionList.add(action.getActionName());
                }
            }
        }

        for (String entityInCommand:allEntitiesInCommand) {
            if (!subjectListInActionList.contains(entityInCommand)) {
                errorText = "There are some useless subjects in this action";
                return false;
            }
        }

        //if (subjectListInActionList==null) {return true;}
        if (hasSubject) {
            subjectList.addAll(entitiesInLocation);
            subjectList.addAll(entitiesInPlayerInv);
            subjectList.retainAll(subjectListInActionList);
            if (subjectList.size()==subjectListInActionList.size()) {
                return true;
            } else {
                errorText = "The location does not have enough entities to do this action or \n" +
                        "You do not have enough artefacts in your inventory to do this action";
                return false;
            }
        }
        errorText = "Your command does not contain a subject";
        return false;
    }

    private HashSet<String> getEntitiesListByPlace(String place) {
        HashSet<String> entitiesInPlace = new HashSet<>();
        for (GameEntity entity:ThisGame.getEntitiesList().keySet()) {
            if (ThisGame.getEntitiesList().get(entity).equals(place)) {
                entitiesInPlace.add(entity.getName());
            }
        }
        return entitiesInPlace;
    }

    private void performAction(String triggerActionName) {
        TreeMap<String, HashSet<GameAction>> actionList = ThisGame.getActionList();

        HashSet<String> consumedActionNameList = new HashSet<>();
        for (GameAction action:actionList.get(triggerActionName)) {
            if (action.getActionType().equals("consumed")) {
                if (action.getActionName().equals("health")) {
                    currentPlayer.decreaseHealthLevel();
                } else {
                    consumedActionNameList.add(action.getActionName());
                }
            }
        }
        if (consumedActionNameList!=null) {
            for (String consumedActionName : consumedActionNameList) {
                if (consumePath(consumedActionName)) {
                    continue;
                } else {
                    consumeEntity(consumedActionName);
                }
            }
        }

        HashSet<String> producedActionNameList = new HashSet<>();
        for (GameAction action:actionList.get(triggerActionName)) {
            if (action.getActionType().equals("produced")) {
                if (action.getActionName().equals("health")) {
                    CheckAndIncreaseHealth();
                } else {
                    producedActionNameList.add(action.getActionName());
                }
            }
        }
        if (producedActionNameList!=null) {
            for (String producedActionName : producedActionNameList) {
                if (produceNewPath(producedActionName)) {
                    continue;
                } else {produceNewEntity(producedActionName);}
            }
        }
    }

    private void CheckAndIncreaseHealth() {
        if (currentPlayer.getHealthLevel()<3) {
            currentPlayer.increaseHealthLevel();
        }
    }

    private boolean consumePath(String consumedLocation) {
        for (EntityLocation location:ThisGame.getLocationList()) {
            if (location.getName().equals(consumedLocation)) {
                ThisGame.getPathList().get(currentLocation.getName()).remove(consumedLocation);
                return true;
            }
        }
        return false;
    }

    private void consumeEntity(String consumedEntity) {
        HashMap<GameEntity, String> entities =ThisGame.getEntitiesList();
        for (GameEntity entity : entities.keySet()) {
            if (consumedEntity.equals(entity.getName())) {
                entities.put(entity, "storeroom");
            }
        }
    }

    private boolean produceNewPath(String newLocation) {
        for (EntityLocation location:ThisGame.getLocationList()) {
            if (location.getName().equals(newLocation)) {
                ThisGame.getPathList().get(currentLocation.getName()).add(newLocation);
                return true;
            }
        }
        return false;
    }

    private void produceNewEntity(String newEntity) {
        //System.out.println("newEntity is "+newEntity);
        HashMap<GameEntity, String> entities =ThisGame.getEntitiesList();
        for (GameEntity entity:entities.keySet()) {
            if (entity.getName().equals(newEntity)) {
                entities.put(entity, currentLocation.getName());
                //System.out.println("yes");
            }
            /*
            if (entity.getName().equals(newEntity) && entity.isArtefacts) {
                entities.put(entity, currentPlayer.getName());
            } else if(entity.getName().equals(newEntity) && !entity.isArtefacts) {
                entities.put(entity, currentLocation.getName());
            }*/
        }
    }


    private void generateNarrationText(String triggerActionName) {
        TreeMap<String, HashSet<GameAction>> actionList = new TreeMap<>();
        actionList = ThisGame.getActionList();

        for (GameAction action:actionList.get(triggerActionName)) {
            if (action.getActionType().equals("narration")) {
                narrationText = action.getActionName() + "\n";
            }
        }
    }

    private void checkDeath() {
        HashMap<GameEntity, String> entities =ThisGame.getEntitiesList();
        if (currentPlayer.getHealthLevel()==0) {
            for (GameEntity entity : entities.keySet()) {
                if (entities.get(entity).equals(currentPlayer.getName())) {
                    // drop entities in this location
                    entities.put(entity, currentLocation.getName());
                }
            }

            // reset health level
            currentPlayer.resetHealthLevel();
            // switch to the original location
            currentLocation = ThisGame.getLocationList().get(0);
            appendNarrationText("you died and lost all of your items, " +
                    "you must return to the start of the game\n");
        }
    }

    public String getNarrationText() {
        if (narrationText!=null) {
           return narrationText;}
        else {
            return "";
        }
    }

    public void appendNarrationText(String inputText) {
        if (narrationText==null) {
            narrationText = "";
        }
        narrationText = narrationText.concat(inputText);
    }

    private void resetNarrationText() {
        narrationText = "";
    }

    public String getErrorText() {
        if (errorText!=null) {
            return errorText;}
        else {
            return "";
        }
    }

    public void resetErrorText() {
        errorText = "";
    }

}
