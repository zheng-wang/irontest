package io.irontest.upgrade;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
    private static final String format = "[%1$tF %1$tT.%1$tL] %2$s %n";

    @Override
    public synchronized String format(LogRecord lr) {
        return String.format(format,
                new Date(lr.getMillis()),
                lr.getMessage()
        );
    }
}
