package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.manedev79.game.Pathfinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SnakeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
    private static final Map<String, String> EMPTY = new HashMap<>();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
     * snake is still alive.
     *
     * @return an empty response.
     */
    private Map<String, String> ping() {
        return EMPTY;
    }

    /**
     * Generic processor that prints out the request and response from the methods.
     *
     * @param req request
     * @param res response
     * @return whatever
     */
    Map<String, String> process(Request req, Response res) {
        try {
            String uri = req.uri();
            LOG.info("{} called with: {}", uri, req.body());
            Map<String, String> snakeResponse;
            switch (uri) {
                case "/ping":
                    snakeResponse = ping();
                    break;
                case "/move":
                    snakeResponse = move(JSON_MAPPER.readTree(req.body()));
                    break;
                case "/end":
                    snakeResponse = end(JSON_MAPPER.readTree(req.body()));
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

    /**
     * /move is called by the engine for each turn the snake has.
     *
     * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing snake movement values.
     */
    private Map<String, String> move(JsonNode moveRequest) {
        LOG.info(moveRequest.asText());
        Map<String, String> response = new HashMap<>();
        response.put("move", getSnake().determineNextMove(moveRequest));
        return response;
    }

    /**
     * /end is called by the engine when a game is complete.
     *
     * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return responses back to the engine are ignored.
     */
    private Map<String, String> end(JsonNode endRequest) {
        return EMPTY;
    }

    private SnakeAI getSnake() {
        return new HungrySnake(new Pathfinder(executorService));
    }
}
