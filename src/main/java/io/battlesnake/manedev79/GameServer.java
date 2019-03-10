package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;

import static spark.Spark.*;

/**
 * GameServer server that deals with requests from the snake engine.
 * Just boiler plate code.  See the readme to get started.
 * It follows the spec here: https://github.com/battlesnakeio/docs/tree/master/apis/snake
 */
public class GameServer {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final SnakeHandler HANDLER = new SnakeHandler();
    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(String[] args) {
        String port = System.getProperty("PORT");
        if (port != null) {
            LOG.info("Found system provided port: {}", port);
        } else {
            port = "8080";
            LOG.info("Using default port: {}", port);
        }
        port(Integer.parseInt(port));
        get("/", (req, res) -> "Battlesnake documentation can be found at " +
                "<a href=\"https://docs.battlesnake.io\">https://docs.battlesnake.io</a>.");
        post("/start", GameServer::start, JSON_MAPPER::writeValueAsString);
        post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
    }

    /**
     * /start is called by the engine when a game is first run.
     *
     * @param startRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing the snake setup values.
     */
    private static JsonNode start(Request startRequest, Response res) {
        LOG.debug(startRequest.toString());

        try {
            return JSON_MAPPER.readTree(GameServer.class.getResourceAsStream("/snakeConfig.json"));
        } catch (IOException e) {
            LOG.error("Cannot read snake config", e);
            return NullNode.getInstance();
        }
    }

}
