package io.battlesnake.manedev79;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatch {
    private static final Logger LOG = LoggerFactory.getLogger(StopWatch.class.getName());
    private long start;
    private long end;

    public StopWatch() {
        LOG.info("Stopwatch created");
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        end = System.currentTimeMillis();
        LOG.info(this.toString());
    }

    @Override
    public String toString() {
        return "Duration: " + (end - start) + "ms";
    }
}
