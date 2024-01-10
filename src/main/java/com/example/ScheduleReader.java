package com.example;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.micronaut.context.BeanContext;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ScheduleReader implements Runnable {

   private final BeanContext beanContext;

   private LocalDateTime lastInit;
   private ScheduledExecutorService scheduler;

   @Inject
   ScheduleReader(final BeanContext beanContext) {
      this.beanContext = beanContext;
   }

   @PostConstruct
   void init() {
      lastInit = LocalDateTime.now();
      scheduler = Executors.newScheduledThreadPool(2);
   }

   @Override
   @SneakyThrows
   public void run() {
      log.info("ScheduleReader initialized at " + lastInit);
      scheduler.schedule(this::runPlanner, 2000, TimeUnit.MILLISECONDS);

      try {
         Thread.currentThread().join();
     } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
     }
   }

   private void runPlanner() {
      try {
         beanContext.getBean(Action.class).run();
      } catch (Exception e) {
         log.error("Error while starting Action: " + e.getMessage(), e);
      }
      scheduler.schedule(this::runPlanner, 2000, TimeUnit.MILLISECONDS);
   }

}
