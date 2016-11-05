package com.futoshita.kafka.poc.scheduler.monitoring;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.kafka.util.KafkaIOUtil;


public class Producer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
  private KafkaProducer<String, byte[]> producer;
  
  
  private static class ProducerHolder {
    
    private final static Producer instance = new Producer();
  }
  
  
  public static Producer getInstance() {
    return ProducerHolder.instance;
  }
  
  
  public void close() {
    if (null != producer) {
      producer.close();
    }
  }
  
  
  public void init() {
    try {
      Properties properties = new Properties();
      
      InputStream in = new FileInputStream("src/main/resources/kafka/producer.properties");
      properties.load(in);
      in.close();
      
      producer = new KafkaProducer<>(properties);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  public void send(String topicName, MonitoringMessage message) {
    try {
      producer.send(new ProducerRecord<String, byte[]>(topicName, KafkaIOUtil.writeBytes(message, MonitoringMessage.getClassSchema())));
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
  
}