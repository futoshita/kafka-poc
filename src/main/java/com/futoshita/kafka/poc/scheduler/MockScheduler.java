package com.futoshita.kafka.poc.scheduler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.kafka.poc.scheduler.monitoring.Producer;


public class MockScheduler {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MockScheduler.class);
  
  private static final int WORKFLOWS_NUMBER = 100000;
  
  
  public static void main(String[] args) {
    try {
      Producer.getInstance().init();
      
      for (int i = 0; i < WORKFLOWS_NUMBER; i++) {
        Workflow we = new Workflow();
        we.setName(UUID.randomUUID().toString());
        we.start();
        
        if (0 == i % 1000) {
          Thread.sleep(5000);
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
      }
      Producer.getInstance().close();
    }
  }
  
}
