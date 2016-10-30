package com.futoshita.kafka.poc.scheduler;

import java.util.UUID;


public class MockScheduler {
  
  private static final int WORKFLOWS_NUMBER = 4;
  
  
  public static void main(String[] args) {
    for (int i = 0; i < WORKFLOWS_NUMBER; i++) {
      Workflow we = new Workflow();
      we.setName(UUID.randomUUID().toString());
      we.start();
    }
  }
  
}
