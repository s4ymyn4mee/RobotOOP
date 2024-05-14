package org.robotoop.log;

import java.util.*;

public class LogWindowSource {
    private int m_iQueueLength;

    private Queue<LogEntry> m_messages;
    private final ArrayList<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayDeque<LogEntry>(iQueueLength);
        m_listeners = new ArrayList<LogChangeListener>();
    }

    public synchronized void registerListener(LogChangeListener listener) {
        m_listeners.add(listener);
    }

    public synchronized void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    public synchronized void append(LogLevel logLevel, String strMessage) {
        if (m_messages.size() >= m_iQueueLength) {
            m_messages.poll();
        }
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.add(entry);
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    public synchronized int size() {
        return m_messages.size();
    }

    public synchronized Iterable<LogEntry> range(int startFrom, int count) {
        LogEntry[] messageArray = m_messages.toArray(new LogEntry[0]);
        if (startFrom < 0 || startFrom >= messageArray.length) {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, messageArray.length);
        return Arrays.asList(Arrays.copyOfRange(messageArray, startFrom, indexTo));
    }

    public synchronized Iterable<LogEntry> all() {
        return new ArrayList<LogEntry>(m_messages);
    }
}