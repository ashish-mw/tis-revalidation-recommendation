package uk.nhs.hee.tis.revalidation.util;

import static java.time.LocalDate.of;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.convertDateInGmcFormat;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.convertGmcDateToLocalDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDateTime;

import org.junit.jupiter.api.Test;


public class DateUtilTest {

  @Test
  public void shouldFormatDateTime() {
    final var dateToFormat = "2017-07-19 12:58:02";
    final var localDateTime = formatDateTime(dateToFormat);
    assertThat(localDateTime, is(notNullValue()));
    assertThat(localDateTime.getYear(), is(2017));
    assertThat(localDateTime.getMonthValue(), is(07));
    assertThat(localDateTime.getDayOfMonth(), is(19));
  }

  @Test
  public void shouldFormatDateTimeWithoutTime() {
    final var dateToFormat = "2017-07-19";
    final var localDateTime = formatDateTime(dateToFormat);
    assertThat(localDateTime, is(notNullValue()));
    assertThat(localDateTime.getYear(), is(2017));
    assertThat(localDateTime.getMonthValue(), is(07));
    assertThat(localDateTime.getDayOfMonth(), is(19));
  }

  @Test
  public void shouldFormatDate() {
    final var dateToFormat = "2017-07-19";
    final var localDate = formatDate(dateToFormat);
    assertThat(localDate, is(notNullValue()));
    assertThat(localDate.getYear(), is(2017));
    assertThat(localDate.getMonthValue(), is(07));
    assertThat(localDate.getDayOfMonth(), is(19));
  }

  @Test
  public void shouldReturnNullWhenDateTimeIsEmpty() {
    final String dateToFormat = null;
    final var localDateTime = formatDateTime(dateToFormat);
    assertNull(localDateTime);
  }

  @Test
  public void shouldReturnNullWhenDateIsEmpty() {
    final String dateToFormat = null;
    final var localDateTime = formatDate(dateToFormat);
    assertNull(localDateTime);
  }

  @Test
  public void shouldFormatDateToGmcFormat() {
    final var dateToFormat = "2017-07-19";
    final var localDateTime = formatDate(dateToFormat);

    final var gmcDate = convertDateInGmcFormat(localDateTime);
    assertNotNull(gmcDate);
    assertThat(gmcDate, is("19/07/2017"));
  }

  @Test
  public void shouldFormatGmcFormatStringDateToLocalDate() {
    final var dateToFormat = "04/07/2017";
    final var localDate = of(2017, 07, 04);

    final var gmcDate = convertGmcDateToLocalDate(dateToFormat);
    assertNotNull(gmcDate);
    assertThat(gmcDate, is(localDate));
  }

  @Test
  public void shouldReturnNullWhenDateIsEmptyString() {
    final var dateToFormat = "";
    final var gmcDate = convertGmcDateToLocalDate(dateToFormat);
    assertNull(gmcDate);
  }

}