package edu.uob;

import java.io.File;
import java.util.*;

public class GameParser {
    private ArrayList<EntityLocation> ThisGameLocationList = new ArrayList<>();
    private HashMap<GameEntity, String> ThisGameEntitiesList = new HashMap<>();
    private HashMap<String, HashSet<String>> ThisGamePathList= new HashMap<>();
    private TreeMap<String, HashSet<GameAction>> ThisGameActionList = new TreeMap<>();
    private HashMap<String, HashSet<String>> ThisGameSynonymousTriggersMap= new HashMap<>();
    private BasicCommands ThisGameBasicCommands;

    public GameParser(File entitiesFile, File actionsFile) {
        entityParser entitiesParser = new entityParser(entitiesFile);
        ThisGameLocationList = entitiesParser.getLocationList();
        ThisGameEntitiesList = entitiesParser.getEntitiesList();
        ThisGamePathList = entitiesParser.getPathList();

        actionParser actionParser = new actionParser(actionsFile);
        ThisGameActionList = actionParser.getActionList();
        ThisGameSynonymousTriggersMap = actionParser.getSynonymousTriggersMap();
        ThisGameBasicCommands = new BasicCommands();
    }

    public ArrayList<EntityLocation> getLocationList() {
        return ThisGameLocationList;
    }

    public HashMap<GameEntity, String> getEntitiesList() {
        return ThisGameEntitiesList;
    }

    public HashMap<String, HashSet<String>> getPathList() {
        return ThisGamePathList;
    }

    public TreeMap<String, HashSet<GameAction>> getActionList() {
        return ThisGameActionList;
    }

    public HashMap<String, HashSet<String>> getSynonymousTriggersMap() {return ThisGameSynonymousTriggersMap;}

    public BasicCommands getBasicCommands() {
        return ThisGameBasicCommands;
    }
}
