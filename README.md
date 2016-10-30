# Kafka use-cases proof of concept

## Scheduler monitoring

`MockScheduler.main` method starts `x` workflows composed of `y` steps.

Each step has a duration of execution between 100ms and 1000ms with a rate of error of 10%.

When a step stops, a producer sends a `MonitoringMessage` object to [Kafka](https://kafka.apache.org).