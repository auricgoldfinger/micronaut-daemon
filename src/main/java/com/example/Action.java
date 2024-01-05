package com.example;

import java.time.LocalDateTime;
import io.micronaut.context.annotation.Prototype;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Prototype
@Slf4j
public class Action {

   private LocalDateTime lastInit;

   Action() {
   }

   @PostConstruct
   void init() {
      lastInit = LocalDateTime.now();
   }

   public void run() {
      log.info("Action initialized at " + lastInit);
   }
}
