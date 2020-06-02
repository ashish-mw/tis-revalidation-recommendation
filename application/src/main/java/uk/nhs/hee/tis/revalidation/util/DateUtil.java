package uk.nhs.hee.tis.revalidation.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter GMC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate formatDateTime(final String date) {
        log.info("Parsing date time for given date: {}", date);
        if (!StringUtils.isEmpty(date)) {
            final var localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
            return LocalDate.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth());
        }

        return null;
    }

    public static LocalDate formatDate(final String date) {
        log.info("Parsing date for given date: {}", date);
        if (!StringUtils.isEmpty(date)) {
            return LocalDate.parse(date, DATE_FORMATTER);
        }

        return null;
    }

    public static String convertDateInGmcFormat(final LocalDate date) {
        log.info("Format date to GMC format: {}", date);
        if (date != null) {
            return date.format(GMC_DATE_FORMATTER);
        }

        return null;
    }

}
