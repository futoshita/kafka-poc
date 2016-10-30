package com.futoshita.kafka.poc.scheduler.monitoring;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Producer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
  
  
  public static void run(String topicName, MonitoringMessage message) {
    KafkaProducer<String, byte[]> producer = null;
    
    try {
      Properties properties = new Properties();
      
      InputStream in = new FileInputStream("src/main/resources/kafka/producer.properties");
      properties.load(in);
      in.close();
      
      producer = new KafkaProducer<>(properties);
      
      GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(MonitoringMessage.SCHEMA$);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      BinaryEncoder e = EncoderFactory.get().binaryEncoder(os, null);
      writer.write(message, e);
      e.flush();
      byte[] byteData = os.toByteArray();
      os.close();
      
      producer.send(new ProducerRecord<String, byte[]>(topicName, byteData));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      producer.close();
    }
  }
  
}