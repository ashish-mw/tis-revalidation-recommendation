package uk.nhs.hee.tis.revalidation.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DateUtilTest {

    @Test
    public void shouldFormatDateTime() {
        final var dateToFormat = "2017-07-19 12:58:02";
        final var localDateTime = DateUtil.formatDateTime(dateToFormat);
        assertThat(localDateTime, is(notNullValue()));
        assertThat(localDateTime.getYear(), is(2017));
        assertThat(localDateTime.getMonthValue(), is(07));
        assertThat(localDateTime.getDayOfMonth(), is(19));
    }

    @Test
    public void shouldFormatDate() {
        final var dateToFormat = "2017-07-19";
        final var localDate = DateUtil.formatDate(dateToFormat);
        assertThat(localDate, is(notNullValue()));
        assertThat(localDate.getYear(), is(2017));
        assertThat(localDate.getMonthValue(), is(07));
        assertThat(localDate.getDayOfMonth(), is(19));
    }

    @Test
    public void shouldReturnNullWhenDateTimeIsEmpty() {
        final String dateToFormat = null;
        final var localDateTime = DateUtil.formatDateTime(dateToFormat);
        assertNull(localDateTime);
    }

    @Test
    public void shouldReturnNullWhenDateIsEmpty() {
        final String dateToFormat = null;
        final var localDateTime = DateUtil.formatDate(dateToFormat);
        assertNull(localDateTime);
    }

    @Test
    public void shouldFormatDateToGmcFormat() {
        final var dateToFormat = "2017-07-19";
        final var localDateTime = DateUtil.formatDate(dateToFormat);

        final var gmcDate = DateUtil.convertDateInGmcFormat(localDateTime);
        assertNotNull(gmcDate);
        assertThat(gmcDate, is("19/07/2017"));
    }

}