package io.irontest;

import com.github.tomakehurst.wiremock.common.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write WireMock logs to a file.
 * Find the logging config in config.yml.
 */
public class WireMockFileNotifier implements Notifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(WireMockFileNotifier.class);

    @Override
    public void info(String message) {
        LOGGER.info(message);
    }

    @Override
    public void error(String message) {
        LOGGER.error(message);
    }

    @Override
    public void error(String message, Throwable t) {
        LOGGER.error(message, t);
    }
}
