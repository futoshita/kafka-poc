package com.futoshita.kafka.poc.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.kafka.poc.dao.entity.WorkflowMonitoring;
import com.futoshita.kafka.util.DateUtil;


public class DaoService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DaoService.class);
  private static final String CSV_FILE_PATH = "/Users/jthomas/Coding/java/kafka-poc/target/monitoring.csv";
  
  
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("monitoring");
    EntityManager em = emf.createEntityManager();
    
    try {
      em.getTransaction().begin();
      
      try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
        String line;
        
        // skip file header
        br.readLine();
        
        while ((line = br.readLine()) != null) {
          String[] data = line.split(",");
          
          if (8 == data.length) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("line: {}", line);
              LOGGER.debug("data: {}", data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " + data[4] + " " + data[5] + " " + data[6] + " " + data[7]);
            }
            WorkflowMonitoring monitoring = new WorkflowMonitoring();
            monitoring.setWorkflowName(data[0] != null && !data[0].isEmpty() && !"null".equals(data[0]) ? data[0] : null);
            monitoring.setStepName(data[1] != null && !data[1].isEmpty() && !"null".equals(data[1]) ? data[1] : null);
            monitoring.setStepRank(data[2] != null && !data[2].isEmpty() && !"null".equals(data[2]) ? Integer.valueOf(data[2]) : null);
            monitoring.setStatus(data[3] != null && !data[3].isEmpty() && !"null".equals(data[3]) ? data[3] : null);
            monitoring.setStartTime(data[4] != null && !data[4].isEmpty() && !"null".equals(data[4]) ? DateUtil.parseToTimestamp(data[4]) : null);
            monitoring.setEndTime(data[5] != null && !data[5].isEmpty() && !"null".equals(data[5]) ? DateUtil.parseToTimestamp(data[5]) : null);
            monitoring.setDuration(data[6] != null && !data[6].isEmpty() && !"null".equals(data[6]) ? Integer.valueOf(data[6]) : null);
            monitoring.setMessage(data[7] != null && !data[7].isEmpty() && !"null".equals(data[7]) ? data[7] : null);
            
            em.persist(monitoring);
          }
        }
      } catch (IOException e) {
        throw e;
      }
      
      em.getTransaction().commit();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      em.getTransaction().rollback();
    } finally {
      em.close();
      emf.close();
    }
  }
  
}
