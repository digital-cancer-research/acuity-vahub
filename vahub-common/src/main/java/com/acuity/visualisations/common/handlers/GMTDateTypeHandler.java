package com.acuity.visualisations.common.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GMTDateTypeHandler extends BaseTypeHandler<Date> {

    public static final String GMT = "GMT";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.TIMESTAMP);
        } else {
            Timestamp timestamp = new Timestamp(parameter.getTime());
            ps.setTimestamp(i, timestamp, Calendar.getInstance(TimeZone.getTimeZone(GMT)));
        }
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTimestamp(columnName, Calendar.getInstance(TimeZone.getTimeZone(GMT)));
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTimestamp(columnIndex, Calendar.getInstance(TimeZone.getTimeZone(GMT)));
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getTimestamp(columnIndex, Calendar.getInstance(TimeZone.getTimeZone(GMT)));
    }
}
