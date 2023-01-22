package edu.uob;

public abstract class GameEntity
{
    String name;
    String description;
    boolean isArtefacts;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
        this.isArtefacts = false;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean getIsArtefacts() {
        return isArtefacts;
    }
}
