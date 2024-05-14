package org.robotoop.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private LogLevel level;
    private String message;
    private LocalDateTime timestamp;

    public LogEntry(LogLevel logLevel, String strMessage) {
        this.message = strMessage;
        this.level = logLevel;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedEntry() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s - %s", timestamp.format(formatter), level, message);
    }
}

