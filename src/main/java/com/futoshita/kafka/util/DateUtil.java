package com.futoshita.kafka.util;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;


public class DateUtil {
  
  public static String formatDateTime(DateTime datetime) {
    return datetime.toString("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH);
  }
  
  public static Long getDuration(DateTime start, DateTime end) {
    return new Interval(start, end).toDurationMillis();
  }
  
}
