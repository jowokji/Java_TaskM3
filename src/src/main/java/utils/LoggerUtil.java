package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    private static final Logger LOGGER = LogManager.getLogger(LoggerUtil.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logError(String message, Exception e) {
        LOGGER.error(message, e);
    }
}
