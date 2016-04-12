package nl.xillio.xill.plugins.date.services;

import nl.xillio.xill.api.data.Date;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZoneId;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Paul_2 on 26-Nov-15.
 */
public class DateServiceImplTest {

    @Test
    public void testBaseDateServiceMethods() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Assert.assertEquals(ds.formatDate(justOneDate, null), "2015-02-14 12:32:15");
        Assert.assertEquals(ds.formatDate(justOneDate, "yyyy-MM-dd HH:mm:ss.nnn z"), "2015-02-14 12:32:15.012 GMT");
    }

    @Test
    public void testParseDate() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Assert.assertEquals(ds.parseDate("2015-02-14 12:32:15.012 GMT", "yyyy-MM-dd HH:mm:ss.nnn z").getZoned(), justOneDate.getZoned());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "No dateStyle or timeStyle was provided")
    public void testLocalizedFormatter() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Assert.assertEquals(ds.formatDateLocalized(justOneDate, null, FormatStyle.SHORT, Locale.US), "12:32 PM");
        Assert.assertEquals(ds.formatDateLocalized(justOneDate, FormatStyle.SHORT, null, Locale.US), "2/14/15");
        Assert.assertEquals(ds.formatDateLocalized(justOneDate, FormatStyle.SHORT, FormatStyle.SHORT, Locale.US), "2/14/15 12:32 PM");
        Assert.assertEquals(ds.formatDateLocalized(justOneDate, null, null, null), "2015-02-14 12:32:15");
        ds.formatDateLocalized(justOneDate, null, null, Locale.US);
    }

    @Test
    public void testAdd() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Map<ChronoUnit, Long> addValues = new HashMap<>();
        addValues.put(ChronoUnit.HOURS, 12L);
        Date newDate = ds.add(justOneDate, addValues);
        Assert.assertEquals(ds.formatDate(justOneDate, null), "2015-02-14 12:32:15");
        Assert.assertEquals(ds.formatDate(newDate, null), "2015-02-15 00:32:15");
    }

    @Test
    public void testChangeTimeZone() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Date newDate = ds.changeTimeZone(justOneDate, ZoneId.of("CET"));
        Assert.assertEquals(ds.formatDate(justOneDate, null), "2015-02-14 12:32:15");
        Assert.assertEquals(ds.formatDate(newDate, null), "2015-02-14 13:32:15");
    }

    @Test
    public void testGetFieldValues() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Map<String, Long> values = ds.getFieldValues(justOneDate);
        Assert.assertEquals(values.get("year"), new Long(2015L));
        Assert.assertEquals(values.get("monthOfYear"), new Long(2L));
        Assert.assertEquals(values.get("dayOfMonth"), new Long(14L));
        Assert.assertEquals(values.get("hourOfDay"), new Long(12L));
        Assert.assertEquals(values.get("minuteOfHour"), new Long(32L));
        Assert.assertEquals(values.get("secondOfMinute"), new Long(15L));
        Assert.assertEquals(values.get("nanoOfSecond"), new Long(12L));
        Assert.assertEquals(values.get("minuteOfDay"), new Long(12L * 60 + 32L));
    }

    @Test
    public void testDifferenceNonAbsolute() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Date justAnotherDate = ds.constructDate(2015, 2, 13, 12, 32, 15, 12, ZoneId.of("GMT"));
        Map<String, Double> diffs = ds.difference(justOneDate, justAnotherDate, false);
        diffs.forEach((k, v) -> System.out.printf("Key: %s, Value: %f%n", k, v));
        Assert.assertEquals(diffs.get("totalNanos"), -86400000000000.0);
        Assert.assertEquals(diffs.get("totalMicros"), -86400000000.0);
        Assert.assertEquals(diffs.get("totalMillis"), -86400000.0);
        Assert.assertEquals(diffs.get("totalSeconds"), -86400.0);
        Assert.assertEquals(diffs.get("totalMinutes"), -1440.0);
        Assert.assertEquals(diffs.get("totalHours"), -24.0);
        Assert.assertEquals(diffs.get("totalHalfDays"), -2.0);
        Assert.assertEquals(diffs.get("totalDays"), -1.0);

        Assert.assertEquals(diffs.get("days"), -1.0);
        Assert.assertEquals(diffs.get("minutes"), 0.0);
    }

    @Test
    public void testDifferenceAbsolute() {
        DateService ds = new DateServiceImpl();
        Date justOneDate = ds.constructDate(2015, 2, 14, 12, 32, 15, 12, ZoneId.of("GMT"));
        Date justAnotherDate = ds.constructDate(2015, 2, 13, 12, 32, 15, 12, ZoneId.of("GMT"));
        Map<String, Double> diffs = ds.difference(justOneDate, justAnotherDate, true);
        diffs.forEach((k, v) -> System.out.printf("Key: %s, Value: %f%n", k, v));
        Assert.assertEquals(diffs.get("totalNanos"), 86400000000000.0);
        Assert.assertEquals(diffs.get("totalMicros"), 86400000000.0);
        Assert.assertEquals(diffs.get("totalMillis"), 86400000.0);
        Assert.assertEquals(diffs.get("totalSeconds"), 86400.0);
        Assert.assertEquals(diffs.get("totalMinutes"), 1440.0);
        Assert.assertEquals(diffs.get("totalHours"), 24.0);
        Assert.assertEquals(diffs.get("totalHalfDays"), 2.0);
        Assert.assertEquals(diffs.get("totalDays"), 1.0);

        Assert.assertEquals(diffs.get("days"), 1.0);
        Assert.assertEquals(diffs.get("minutes"), 0.0);
    }
}
