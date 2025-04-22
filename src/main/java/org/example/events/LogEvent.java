package org.example.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LogEvent extends ApplicationEvent {
    private final String date;
    private final String time;
    private final Long id;

    public LogEvent(Object source, String date, String time, Long id) {
        super(source);
        this.date = date;
        this.time = time;
        this.id = id;
    }
}
