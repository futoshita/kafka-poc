select count(*) from workflow_monitoring;


select step_name, count(id), avg(duration)::float as avg_duration
from workflow_monitoring
where step_name <> 'start'
group by step_name order by step_name;


select date_trunc('minute', start_time) as start_time, count(id)
from workflow_monitoring
where step_name = 'start'
group by date_trunc('minute', start_time)
order by date_trunc('minute', start_time) asc;


with filled_dates as (
  select minute, 0 as idle
  from generate_series('2016-11-06 13:50:00'::timestamptz, '2016-11-06 17:05'::timestamptz, '1 minute') as minute
),
workflow_starts as (
  select date_trunc('minute', start_time) as minute, count(id) as starts
  from workflow_monitoring
  where step_name = 'start'
  group by date_trunc('minute', start_time)
)
select
  filled_dates.minute,
  coalesce(workflow_starts.starts, filled_dates.idle) as starts
from filled_dates
left outer join workflow_starts on workflow_starts.minute = filled_dates.minute
order by filled_dates.minute;


with filled_dates as (
  select minute, 0 as idle
  from generate_series('2016-11-06 13:50:00'::timestamptz, '2016-11-06 17:05'::timestamptz, '1 minute') as minute
),
workflow_errors as (
  select date_trunc('minute', start_time) as minute, count(id) as errors
  from workflow_monitoring
  where step_name = 'end' and status = 'error'
  group by date_trunc('minute', start_time)
)
select
  filled_dates.minute,
  coalesce(workflow_errors.errors, filled_dates.idle) as errors
from filled_dates
left outer join workflow_errors on workflow_errors.minute = filled_dates.minute
order by filled_dates.minute;


with filled_dates as (
  select minute, 0 as idle
  from generate_series('2016-11-06 13:50:00'::timestamptz, '2016-11-06 17:05'::timestamptz, '1 minute') as minute
),
workflow_starts as (
  select date_trunc('minute', start_time) as minute, count(id) as starts
  from workflow_monitoring
  where step_name = 'start'
  group by date_trunc('minute', start_time)
),
workflow_errors as (
  select date_trunc('minute', start_time) as minute, count(id) as errors
  from workflow_monitoring
  where step_name = 'end' and status = 'error'
  group by date_trunc('minute', start_time)
)
select
  filled_dates.minute,
  coalesce(workflow_starts.starts, filled_dates.idle) as starts,
  coalesce(workflow_errors.errors, filled_dates.idle) as errors
from filled_dates
left outer join workflow_starts on workflow_starts.minute = filled_dates.minute
left outer join workflow_errors on workflow_errors.minute = filled_dates.minute
order by filled_dates.minute;