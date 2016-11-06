package com.futoshita.kafka.poc.scheduler.monitoring;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.kafka.util.KafkaIOUtil;


public class Consumer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
  private static final String CSV_FILE_PATH = "/Users/jthomas/Coding/java/kafka-poc/target/monitoring.csv";
  
  
  public static void main(String[] args) {
    KafkaConsumer<String, byte[]> consumer = null;
    
    FileWriter writer = null;
    try {
      Properties properties = new Properties();
      InputStream in = new FileInputStream("src/main/resources/kafka/consumer.properties");
      properties.load(in);
      // group.id random pour lire la liste depuis le début à chaque exécution
      properties.put("group.id", UUID.randomUUID().toString());
      in.close();
      consumer = new KafkaConsumer<>(properties);
      
      consumer.subscribe(Arrays.asList("mock-scheduler"));
      
      writer = getFileOutputStream();
      
      // 50 attempts * 200ms timeout = 10s before stopping
      int attempts = 50;
      boolean running = true;
      while (running) {
        ConsumerRecords<String, byte[]> records = consumer.poll(200);
        
        if (records.isEmpty()) {
          attempts--;
        }
        
        for (ConsumerRecord<String, byte[]> record : records) {
          MonitoringMessage message = KafkaIOUtil.readBytes(record.value(), MonitoringMessage.getClassSchema());
          writeMonitoringMessage(writer, message);
          
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("message: {}", message.toString());
          }
        }
        
        if (0 == attempts) {
          running = false;
        }
        
        writer.flush();
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      try {
        closeFileOutputStream(writer);
      } catch (IOException e) {
        LOGGER.error(e.getMessage(), e);
      }
      consumer.close();
    }
  }
  
  private static FileWriter getFileOutputStream() throws IOException {
    FileWriter writer = new FileWriter(CSV_FILE_PATH);
    
    writer.append("workflow_name");
    writer.append(',');
    writer.append("step_name");
    writer.append(',');
    writer.append("step_rank");
    writer.append(',');
    writer.append("status");
    writer.append(',');
    writer.append("start_time");
    writer.append(',');
    writer.append("end_time");
    writer.append(',');
    writer.append("duration");
    writer.append(',');
    writer.append("message");
    writer.append('\n');
    
    writer.flush();
    
    return writer;
  }
  
  private static void writeMonitoringMessage(FileWriter writer, MonitoringMessage message) throws IOException {
    writer.append(message.getWorkflowName());
    writer.append(',');
    writer.append(message.getStepName());
    writer.append(',');
    writer.append(String.valueOf(message.getStepRank()));
    writer.append(',');
    writer.append(message.getStatus());
    writer.append(',');
    writer.append(message.getStartTime());
    writer.append(',');
    writer.append(message.getEndTime());
    writer.append(',');
    writer.append(String.valueOf(message.getDuration()));
    writer.append(',');
    writer.append(message.getMessage());
    writer.append('\n');
  }
  
  private static void closeFileOutputStream(FileWriter writer) throws IOException {
    writer.flush();
    writer.close();
  }
  
}
