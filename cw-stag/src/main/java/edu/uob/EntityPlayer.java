package edu.uob;

public class EntityPlayer extends GameEntity{
    private EntityLocation playerLocation;
    private int healthLevel;

    public EntityPlayer(String playerName, String playersDescription) {
        super(playerName, playersDescription);
        playerLocation = null;
        healthLevel = 3;
    }

    public EntityLocation getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(EntityLocation locationName) {
        playerLocation = locationName;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public void increaseHealthLevel() {
        healthLevel++;
    }

    public void decreaseHealthLevel() {
        healthLevel--;
    }

    public void resetHealthLevel() {
        healthLevel = 3;
    }
}
