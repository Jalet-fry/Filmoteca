package org.example.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class LogEventHandler implements ApplicationListener<LogEvent> {

    @Override
    public void onApplicationEvent(LogEvent event) {

    }
}
