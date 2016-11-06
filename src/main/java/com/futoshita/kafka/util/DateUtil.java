package com.futoshita.kafka.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;


public class DateUtil {
  
  private static final String DATETIME_PATTERN = "dd-MMM-yyyy HH:mm:ss.SSS";
  
  public static String formatDateTime(DateTime datetime) {
    return datetime.toString(DATETIME_PATTERN, Locale.ENGLISH);
  }
  
  public static Long getDuration(DateTime start, DateTime end) {
    return new Interval(start, end).toDurationMillis();
  }
  
  public static DateTime parseToDateTime(String datetime) {
    return DateTimeFormat.forPattern(DATETIME_PATTERN).withLocale(Locale.ENGLISH).parseDateTime(datetime);
  }
  
  public static Timestamp parseToTimestamp(String datetime) throws ParseException {
    return new Timestamp(new SimpleDateFormat(DATETIME_PATTERN).parse(datetime).getTime());
  }
  
}
