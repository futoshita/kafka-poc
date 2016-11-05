package com.futoshita.kafka.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;


public class KafkaIOUtil {
  
  public static <T> byte[] writeBytes(T entity, Schema schema) throws IOException {
    byte[] byteData = null;
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
      DatumWriter<T> writer = new SpecificDatumWriter<T>(schema);
      writer.write(entity, encoder);
      encoder.flush();
      byteData = out.toByteArray();
    } finally {
      out.close();
    }
    
    return byteData;
  }
  
  public static <T> T readBytes(byte[] stream, Schema schema) throws IOException {
    T entity = null;
    
    DatumReader<T> reader = new SpecificDatumReader<T>(schema);
    Decoder decoder = DecoderFactory.get().binaryDecoder(stream, null);
    entity = reader.read(null, decoder);
    
    return entity;
  }
  
}
