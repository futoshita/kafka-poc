package com.futoshita.kafka.poc.scheduler;

import java.util.Locale;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.kafka.poc.scheduler.monitoring.MonitoringMessage;
import com.futoshita.kafka.poc.scheduler.monitoring.Producer;


public class Workflow extends Thread {
  
  private final Logger LOGGER = LoggerFactory.getLogger(Workflow.class);
  private final int MIN_STEP_DELAY = 100;
  private final int MAX_STEP_DELAY = 1000;
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
    return 4;
  }
  
  @Override
  public void run() {
    for (int i = 0; i < getStepsNumber(); i++) {
      DateTime startTime = new DateTime();
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("starting workflow {} step {}: {}", getName(), (i + 1), startTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH));
      }
      
      try {
        int delay = getStepDelay();
        boolean error = getError();
        
        Thread.sleep(delay);
        
        if (error) {
          throw new InterruptedException("workflow " + getName() + " step " + (i + 1) + " in error.");
        }
        
        DateTime endTime = new DateTime();
        long duration = new Interval(startTime, endTime).toDurationMillis();
        
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("stopping workflow {} step {}: {}", getName(), (i + 1), endTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH));
          LOGGER.debug("workflow {} step {} duration: {} ms", getName(), (i + 1), duration);
        }
        
        Producer.run(KAFKA_TOPIC, new MonitoringMessage(getName(), String.valueOf(i + 1), "successful", startTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH),
            endTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH), String.valueOf(duration), null));
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
        
        DateTime endTime = new DateTime();
        long duration = new Interval(startTime, endTime).toDurationMillis();
        
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("stopping workflow {} step {}: {}", getName(), (i + 1), endTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH));
          LOGGER.debug("workflow {} step {} duration: {} ms", getName(), (i + 1), duration);
        }
        
        Producer.run(KAFKA_TOPIC, new MonitoringMessage(getName(), String.valueOf(i + 1), "error", startTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH),
            endTime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH), String.valueOf(duration), e.getMessage()));
        break;
      }
    }
  }
}