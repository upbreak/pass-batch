package com.jinwoo.pass.passbatch.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtils {
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String format(final LocalDateTime localDateTime) {
        return localDateTime.format(YYYY_MM_DD_HH_MM);

    }
}
