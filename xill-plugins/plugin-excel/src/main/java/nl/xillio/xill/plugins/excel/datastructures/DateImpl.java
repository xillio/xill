package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.data.Date;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Daan Knoope
 */
public class DateImpl implements Date{

    java.util.Date date;

    public DateImpl(java.util.Date date){
        this.date = date;
    }

    @Override
    public ZonedDateTime getZoned() {
        return ZonedDateTime.ofInstant(this.date.toInstant(), ZoneId.systemDefault());
    }
}
