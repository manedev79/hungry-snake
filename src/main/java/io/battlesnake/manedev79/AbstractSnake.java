package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSnake implements SnakeHandler {
    private static final Map<String, String> EMPTY = new HashMap<>();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

    @Override
    public Map<String, String> ping() {
        return EMPTY;
    }

    @Override
    public Map<String, String> process(Request req, Response res) {
        try {
            JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
            String uri = req.uri();
            LOG.info("{} called with: {}", uri, req.body());
            Map<String, String> snakeResponse;
            switch (uri) {
                case "/start":
                    snakeResponse = start(parsedRequest);
                    break;
                case "/ping":
                    snakeResponse = ping();
                    break;
                case "/move":
                    snakeResponse = move(parsedRequest);
                    break;
                case "/end":
                    snakeResponse = end(parsedRequest);
                    break;
                default:
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
            }
            LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
            return snakeResponse;
        } catch (Exception e) {
            LOG.warn("Something went wrong!", e);
            return null;
        }
    }

    @Override
    public Map<String, String> start(JsonNode startRequest) {
        Map<String, String> response = new HashMap<>();
        response.put("color", "#ff00ff");
        return response;
    }

    @Override
    public Map<String, String> move(JsonNode moveRequest) {
        Map<String, String> response = new HashMap<>();
        response.put("move", moveIntoDirection(moveRequest));
        return response;
    }

    protected abstract String moveIntoDirection(JsonNode moveRequest);

    @Override
    public Map<String, String> end(JsonNode endRequest) {
        return EMPTY;
    }

}
