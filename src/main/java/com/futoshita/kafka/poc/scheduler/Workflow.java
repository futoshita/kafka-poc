package com.futoshita.kafka.poc.scheduler;

import java.util.Random;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.kafka.poc.scheduler.monitoring.MonitoringMessage;
import com.futoshita.kafka.poc.scheduler.monitoring.Producer;
import com.futoshita.kafka.util.DateUtil;


public class Workflow extends Thread {
  
  private final Logger LOGGER = LoggerFactory.getLogger(Workflow.class);
  
  private final int MIN_STEP_DELAY = 100;
  private final int MAX_STEP_DELAY = 1000;
  private final int MIN_STEPS_NUMBER = 2;
  private final int MAX_STEPS_NUMBER = 12;
  private final String KAFKA_TOPIC = "mock-scheduler";
  
  private double errorRate = -1;
  
  
  private boolean getError() {
    return new Random().nextDouble() < getErrorRate();
  }
  
  /**
   * Si la valeur de errorRate n'est pas réinitialisée à la création du thread,
   * la valeur est de 0.1 (10%) par défaut.
   * 
   * @return
   */
  public double getErrorRate() {
    if (-1 == errorRate) {
      errorRate = 0.1;
    }
    return errorRate;
  }
  
  public void setErrorRate(double errorRate) {
    this.errorRate = errorRate;
  }
  
  private int getStepDelay() {
    return new Random().nextInt(MAX_STEP_DELAY - MIN_STEP_DELAY + 1) + MIN_STEP_DELAY;
  }
  
  private int getStepsNumber() {
    return new Random().nextInt(MAX_STEPS_NUMBER - MIN_STEPS_NUMBER + 1) + MIN_STEPS_NUMBER;
  }
  
  @Override
  public void run() {
    DateTime workflowStartTime = new DateTime();
    Producer.getInstance().send(KAFKA_TOPIC, new MonitoringMessage(getName(), "start", 0, "successful", DateUtil.formatDateTime(workflowStartTime), null, null, null));
    
    int stepsNumber = getStepsNumber();
    
    boolean onError = false;
    for (int i = 0; i < stepsNumber; i++) {
      DateTime startTime = new DateTime();
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("starting workflow {} step {}: {}", getName(), (i + 1), DateUtil.formatDateTime(startTime));
      }
      
      try {
        int delay = getStepDelay();
        boolean error = getError();
        
        Thread.sleep(delay);
        
        if (error) {
          throw new InterruptedException("workflow " + getName() + " step " + (i + 1) + " in error.");
        }
        
        DateTime endTime = new DateTime();
        long duration = DateUtil.getDuration(startTime, endTime);
        
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("stopping workflow {} step {}: {}", getName(), (i + 1), DateUtil.formatDateTime(endTime));
          LOGGER.debug("workflow {} step {} duration: {} ms", getName(), (i + 1), duration);
        }
        
        Producer.getInstance().send(KAFKA_TOPIC,
            new MonitoringMessage(getName(), String.valueOf(i + 1), (i + 1), "successful", DateUtil.formatDateTime(startTime), DateUtil.formatDateTime(endTime), duration, null));
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
        
        onError = true;
        
        DateTime endTime = new DateTime();
        long duration = DateUtil.getDuration(startTime, endTime);
        
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("stopping workflow {} step {}: {}", getName(), (i + 1), DateUtil.formatDateTime(endTime));
          LOGGER.debug("workflow {} step {} duration: {} ms", getName(), (i + 1), duration);
        }
        
        Producer.getInstance().send(KAFKA_TOPIC,
            new MonitoringMessage(getName(), String.valueOf(i + 1), (i + 1), "error", DateUtil.formatDateTime(startTime), DateUtil.formatDateTime(endTime), duration, e.getMessage()));
        break;
      }
    }
    
    DateTime workflowEndTime = new DateTime();
    Producer.getInstance().send(KAFKA_TOPIC, new MonitoringMessage(getName(), "end", 0, onError ? "error" : "successful", DateUtil.formatDateTime(workflowStartTime),
        DateUtil.formatDateTime(workflowEndTime), DateUtil.getDuration(workflowStartTime, workflowEndTime), null));
  }
}