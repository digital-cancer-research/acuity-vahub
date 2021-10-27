package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.JSON_TIMESTAMP_FORMAT;

/**
 * Implementation of data range filter that truncates and adds days to the to and from automatically
 */
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DateRangeFilter extends RangeFilter<Date> {

    public DateRangeFilter(Date from, Date to) {
        setFrom(from);
        setTo(to);
        setIncludeEmptyValues(false);
    }

    public DateRangeFilter(Date from, Date to, boolean includeEmptyValues) {
        setFrom(from);
        setTo(to);
        setIncludeEmptyValues(includeEmptyValues);
    }

    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_TIMESTAMP_FORMAT, timezone = DaysUtil.GMT_TIMEZONE)
    public Date getTo() {
        return super.getTo();
    }

    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_TIMESTAMP_FORMAT, timezone = DaysUtil.GMT_TIMEZONE)
    public Date getFrom() {
        return super.getFrom();
    }

    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_TIMESTAMP_FORMAT, timezone = DaysUtil.GMT_TIMEZONE)
    public void setTo(Date to) {
        super.setTo(toEndOfDay(to));
    }

    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_TIMESTAMP_FORMAT, timezone = DaysUtil.GMT_TIMEZONE)
    public void setFrom(Date from) {
        super.setFrom(toStartOfDay(from));
    }

    /**
     * Returns a day at the start of the day
     */
    private Date toStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        return DaysUtil.truncLocalTime(date);
    }

    /**
     * Returns a day at the end of the day
     */
    private Date toEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Date startOfDay = toStartOfDay(date);
        return DateUtils.addMilliseconds(DaysUtil.addDays(startOfDay, 1), -1);
    }

    @Override
    public boolean canBeHidden() {
        boolean canBeHidden = false;
        if (this.getFrom() == null && this.getTo() == null) {
            canBeHidden = true;
        }
        return canBeHidden;
    }
}
