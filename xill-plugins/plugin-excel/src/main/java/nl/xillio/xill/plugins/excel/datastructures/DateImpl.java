package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.data.Date;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Implementation of {@link nl.xillio.xill.api.data.Date} to use the Xill Date Plugin
 * with the Excel Plugin
 *
 * @author Daan Knoope
 */
public class DateImpl implements Date {

    java.util.Date date;

    public DateImpl(java.util.Date date) {
        this.date = date;
    }

    @Override
    public ZonedDateTime getZoned() {
        return ZonedDateTime.ofInstant(this.date.toInstant(), ZoneId.systemDefault());
    }
}
