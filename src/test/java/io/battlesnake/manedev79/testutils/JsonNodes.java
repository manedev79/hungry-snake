package io.battlesnake.manedev79.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonNodes {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(JsonNodes.class);

    private JsonNodes() {
        // Utility class
    }

    public static JsonNode fromFile(String fileName) {
        try {
            return MAPPER.readTree(JsonNodes.class.getResourceAsStream(fileName));
        } catch (IOException e) {
            LOG.error("Cannot read JSON nodes from {}", fileName, e);
            return NullNode.getInstance();
        }
    }
}
