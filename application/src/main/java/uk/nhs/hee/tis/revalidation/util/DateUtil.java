package uk.nhs.hee.tis.revalidation.util;

import static java.time.LocalDate.parse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class DateUtil {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter GMC_DATE_FORMATTER = DateTimeFormatter
      .ofPattern("dd/MM/yyyy");

  public static LocalDate formatDateTime(final String date) {
    log.info("Parsing date time for given date: {}", date);
    if (!StringUtils.isEmpty(date)) {
      final var dateWithoutTime = date.contains(" ") ? date.substring(0, date.indexOf(" ")) : date;
      return formatDate(dateWithoutTime);
    }

    return null;
  }

  public static LocalDate formatDate(final String date) {
    log.info("Parsing date for given date: {}", date);
    if (!StringUtils.isEmpty(date)) {
      return parse(date, DATE_FORMATTER);
    }

    return null;
  }

  public static String parseDate(final LocalDate date) {
    return date != null ? date.toString() : null;
  }

  public static String convertDateInGmcFormat(final LocalDate date) {
    log.info("Format date to GMC format: {}", date);
    if (!StringUtils.isEmpty(date)) {
      return date.format(GMC_DATE_FORMATTER);
    }

    return null;
  }

  public static LocalDate convertGmcDateToLocalDate(final String date) {
    log.info("Format date to GMC format: {}", date);
    if (!StringUtils.isEmpty(date)) {
      return parse(date, GMC_DATE_FORMATTER);
    }

    return null;
  }

}
