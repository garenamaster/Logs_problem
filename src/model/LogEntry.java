package model;

import java.time.LocalDateTime;

// Строка лога + её время, нужно только для сортировки.
public final class LogEntry implements Comparable<LogEntry> {

    public final LocalDateTime timestamp;
    public final String line;

    public LogEntry(LocalDateTime timestamp, String line) {
        this.timestamp = timestamp;
        this.line = line;
    }

    @Override
    public int compareTo(LogEntry other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
