package edu.uob;

import java.util.HashMap;

public class GameText {
    private GameController controller;

    public GameText(GameController inputController) {
        controller = inputController;
    }

    public String generateText(String trigger) {
        if (trigger.equals("look")) {
            return generateLookText();
        } else if (trigger.equals("inventory") || trigger.equals("inv")) {
            return generateInvText();
        } else if (trigger.equals("health")) {
            return generateHealthText();
        }
        return null;
    }

    public String generateText(String trigger, String entityInBasicCommand) {

        if (trigger.equals("get")) {
            return generateGetText(entityInBasicCommand);
        } else if (trigger.equals("drop")) {
            return generateDropText(entityInBasicCommand);
        } else if(trigger.equals("goto")) {

            return generateGotoText();
        }
        return null;
    }

    private String generateLookText() {
        EntityLocation currentLocation = controller.getCurrentLocation();
        HashMap<GameEntity, String> entities = controller.getThisGame().getEntitiesList();
        String text = "You are in a " + currentLocation.getName() + " ("
                + currentLocation.getDescription() + "). You can see:\n";
        if (entities.containsValue(currentLocation.getName())) {
            for (GameEntity entity : entities.keySet()) {
                if (entities.get(entity).equals(currentLocation.getName())) {
                    //text = text.concat(entity.getName()) + " ";
                    text = text.concat(entity.getName()+ " ("+entity.getDescription() + ")\n");
                }
            }
        }

        text = text.concat("You can assess from here:\n");
        for (String foundLocation:controller.getThisGame().getPathList().keySet()) {
            if (currentLocation.getName().equals(foundLocation)) {
                String result = String.join("\n", controller.getThisGame().getPathList().get(foundLocation));
                text = text.concat(result)+"\n";
            }
        }

        text = text.concat("You can see other players in this place:\n");
        boolean noOtherPlayers = true;
        for (int i=0; i<controller.getPlayersList().size(); i++) {
            if (currentLocation.getName().equals(controller.getPlayersList().get(i).getPlayerLocation().getName()) &&
                !controller.getPlayersList().get(i).getName().equals(controller.getCurrentPlayer().name)) {
                text = text.concat(controller.getPlayersList().get(i).getName() + "\n");
                noOtherPlayers = false;
            }
        }
        if (noOtherPlayers) {
            text =text.concat("No other players here \n");
        }
        return text;
    }

    private String generateInvText() {
        String text = "You have these artefacts:\n";
        HashMap<GameEntity, String> entities = controller.getThisGame().getEntitiesList();
        boolean isInvEmpty = true;
        for (GameEntity entity : entities.keySet()) {
            if (entities.get(entity).equals(controller.getCurrentPlayer().getName())) {
                text = text.concat(entity.getName() + "(" + entity.getDescription() + ")\n");
                isInvEmpty = false;
            }
        }
        if (!isInvEmpty) {
            return text;
        } else {
            return "Your inventory is empty now" + "\n";
        }
    }

    private String generateHealthText() {
        String text = "You health level is " + controller.getCurrentPlayer().getHealthLevel() +" \n";
        return text;
    }

    private String generateGetText(String entityInBasicCommand) {
        String text = "You picked up a "+ entityInBasicCommand + "\n";
        return text;
    }

    private String generateDropText(String entityInBasicCommand) {
        String text = "You dropped a " + entityInBasicCommand + "\n";
        return text;
    }

    private String generateGotoText() {
        String text = generateLookText();
        return text;
    }
}
