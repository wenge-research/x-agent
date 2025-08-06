package com.wenge.model.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateRangeExample {

    public static List<String> getDatesBetween(String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr.substring(0, 10), formatter);
        LocalDate endDate = LocalDate.parse(endDateStr.substring(0, 10), formatter);

        List<String> dateList = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }

        return dateList;
    }

    public static void main(String[] args) {
        String startDate = "2023-10-01";
        String endDate = "2023-10-05";

        List<String> dates = getDatesBetween(startDate, endDate);

        for (String date : dates) {
            System.out.println(date);
        }
    }
}