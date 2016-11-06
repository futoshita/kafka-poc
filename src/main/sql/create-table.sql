CREATE TABLE workflow_monitoring (
  id             SERIAL NOT NULL,
  duration       INTEGER,
  end_time       TIMESTAMP WITH TIME ZONE,
  message        VARCHAR(1023),
  start_time     TIMESTAMP WITH TIME ZONE NOT NULL,
  status         VARCHAR(50) NOT NULL,
  step_name      VARCHAR(50) NOT NULL,
  step_rank      INTEGER NOT NULL,
  workflow_name  VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);