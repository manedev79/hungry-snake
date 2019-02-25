package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.Coordinates;

public class HungrySnake extends AbstractSnake {
    @Override
    protected String moveIntoDirection(JsonNode moveRequest) {
        Coordinates myPosition = myPosition(moveRequest);
        Coordinates someFoodLocation = someFoodLocation(moveRequest);

        return moveToFood(myPosition, someFoodLocation);
    }

    private String moveToFood(Coordinates myPosition, Coordinates someFoodLocation) {
        if (myPosition.x > someFoodLocation.x) return "left";
        if (myPosition.x < someFoodLocation.x) return "right";
        if (myPosition.y < someFoodLocation.y) return "down";
        if (myPosition.y > someFoodLocation.y) return "up";

        return "up";
    }

    private Coordinates myPosition(JsonNode moveRequest) {
        JsonNode snakeHead = moveRequest.get("you").get("body").get(0);
        int x = snakeHead.get("x").asInt();
        int y = snakeHead.get("y").asInt();

        return new Coordinates(x, y);
    }

    private Coordinates someFoodLocation(JsonNode moveRequest) {
        JsonNode firstFood = moveRequest.get("board").get("food").get(0);
        int x = firstFood.get("x").asInt();
        int y = firstFood.get("y").asInt();

        return new Coordinates(x, y);
    }
}
