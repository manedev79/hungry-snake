package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import spark.Request;
import spark.Response;

import java.util.Map;

public interface SnakeHandler {
    /**
     * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
     * snake is still alive.
     *
     * @return an empty response.
     */
    Map<String, String> ping();

    /**
     * Generic processor that prints out the request and response from the methods.
     *
     * @param req request
     * @param res response
     * @return whatever
     */
    Map<String, String> process(Request req, Response res);

    /**
     * /start is called by the engine when a game is first run.
     *
     * @param startRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing the snake setup values.
     */
    Map<String, String> start(JsonNode startRequest);

    /**
     * /move is called by the engine for each turn the snake has.
     *
     * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing snake movement values.
     */
    Map<String, String> move(JsonNode moveRequest);

    /**
     * /end is called by the engine when a game is complete.
     *
     * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return responses back to the engine are ignored.
     */
    Map<String, String> end(JsonNode endRequest);
}
