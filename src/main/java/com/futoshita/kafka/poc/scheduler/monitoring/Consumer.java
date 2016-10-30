package com.futoshita.kafka.poc.scheduler.monitoring;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
  
  public static void main(String[] args) {
    KafkaConsumer<String, byte[]> consumer = null;
    
    try {
      Properties properties = new Properties();
      
      InputStream in = new FileInputStream("src/main/resources/kafka/consumer.properties");
      properties.load(in);
      // group.id random pour lire la liste depuis le début à chaque exécution
      properties.put("group.id", UUID.randomUUID().toString());
      in.close();
      consumer = new KafkaConsumer<>(properties);
      
      consumer.subscribe(Arrays.asList("mock-scheduler"));
      
      SpecificDatumReader<MonitoringMessage> reader = new SpecificDatumReader<MonitoringMessage>(MonitoringMessage.getClassSchema());
      
      boolean running = true;
      
      while (running) {
        ConsumerRecords<String, byte[]> records = consumer.poll(200);
        
        for (ConsumerRecord<String, byte[]> record : records) {
          BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(record.value(), null);
          MonitoringMessage message = reader.read(null, decoder);
          
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("message: {}", message.toString());
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      consumer.close();
    }
  }
  
}
