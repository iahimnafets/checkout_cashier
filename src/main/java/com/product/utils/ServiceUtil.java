package com.product.utils;

import com.product.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceUtil {

    public static BigDecimal bigDecimalZero = new BigDecimal("0.0000");

    public static Date addDaysToDate(Date dateInput, Integer days){
        if(dateInput == null){
            return null;
        }
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(dateInput);
        // add days to date
        c.add(Calendar.DATE, days);
        // convert calendar to date
        Date datePlusDays = c.getTime();
        return datePlusDays;
    }


    public static Date getDateByStringDate(String date) {
        Date newDate = null;
        try {
             newDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            log.error("getDateByStringDate -> date {}", date );
            throw new ApiRequestException( "Error parsing date, not valid format ( expect: dd/MM/YYYY)  received: " +  date );
        }
        return newDate;
    }

    public static String getStrDateByDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        String dateTimeStr = dateFormat.format(date);
        return dateTimeStr;
    }


    public static boolean isThisDateWithinDateRange(Date startDate, Date endDate, Date dateInput ) {
        int result1 = dateInput.compareTo(startDate);
        int result2 = endDate.compareTo(endDate);

        if (result1 >= 0 && result2 <= 0 ) {
            // "dateInput is equals after startDate"
            // "dateInput is equals before endDate"
            return true;
        }else{
            return false;
        }
    }


}
