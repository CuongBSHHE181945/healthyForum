package com.healthyForum.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class DateValidate {
    public static boolean dateIsInFuture(LocalDate date){
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) return true;
        else return false;
    }

    public static boolean hasValidDateRange(LocalDate startDate, LocalDate endDate){
        return (startDate != null && endDate != null && startDate.isBefore(endDate));
    }

    public static long timeCalMin(LocalTime start, LocalTime end, LocalDate date){
        LocalDateTime startDateTime = LocalDateTime.of(date, start);
        LocalDateTime endDateTime = end.isAfter(start) || end.equals(start)
                ? LocalDateTime.of(date, end)
                : LocalDateTime.of(date.plusDays(1), end);

        long durationInMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);
        return durationInMinutes;
    }
}
