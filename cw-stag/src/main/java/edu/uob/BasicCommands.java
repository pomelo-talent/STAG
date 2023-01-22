package edu.uob;

import java.util.HashSet;

public class BasicCommands {
    private HashSet<String> basicCommands = new HashSet<>();

    public BasicCommands() {
        basicCommands.add("inventory");
        basicCommands.add("inv");
        basicCommands.add("get");
        basicCommands.add("drop");
        basicCommands.add("goto");
        basicCommands.add("look");
        basicCommands.add("health");
    }

    public HashSet<String> getBasicCommandsHashset() {
        return basicCommands;
    }
}
