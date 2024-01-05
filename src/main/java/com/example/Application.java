package com.example;

import io.micronaut.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("Please add argument --schedule");
        } else {
            try (final ApplicationContext context = ApplicationContext.run()) {
                for (final String arg : args) {
                    if (arg.equalsIgnoreCase("--schedule")) {
                        try {
                            context.getBean(ScheduleReader.class).run();
                        } catch (Exception e) {
                            log.error("Error while starting ScheduleReader: " + e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}