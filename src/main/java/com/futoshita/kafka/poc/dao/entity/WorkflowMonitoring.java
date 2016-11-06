package com.futoshita.kafka.poc.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "workflow_monitoring")
public class WorkflowMonitoring {
  
  private Integer id;
  private Integer duration;
  private Timestamp endTime;
  private String message;
  private Timestamp startTime;
  private String status;
  private String stepName;
  private Integer stepRank;
  private String workflowName;
  
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workflow_monitoring_id_seq_gen")
  @SequenceGenerator(name = "workflow_monitoring_id_seq_gen", sequenceName = "workflow_monitoring_id_seq", allocationSize = 1)
  public Integer getId() {
    return id;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }
  
  public Integer getDuration() {
    return duration;
  }
  
  public void setDuration(Integer duration) {
    this.duration = duration;
  }
  
  @Column(name = "end_time")
  public Timestamp getEndTime() {
    return endTime;
  }
  
  public void setEndTime(Timestamp endTime) {
    this.endTime = endTime;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  @Column(name = "start_time")
  public Timestamp getStartTime() {
    return startTime;
  }
  
  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  @Column(name = "step_name")
  public String getStepName() {
    return stepName;
  }
  
  public void setStepName(String stepName) {
    this.stepName = stepName;
  }
  
  @Column(name = "step_rank")
  public Integer getStepRank() {
    return stepRank;
  }
  
  public void setStepRank(Integer stepRank) {
    this.stepRank = stepRank;
  }
  
  @Column(name = "workflow_name")
  public String getWorkflowName() {
    return workflowName;
  }
  
  public void setWorkflowName(String workflowName) {
    this.workflowName = workflowName;
  }
  
}
