package com.acuity.visualisations.common.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FormattedStringToCommaListHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        List<String> list = Arrays.asList((rs.getString(columnName) == null ? "" : rs.getString(columnName)).split("###"));
        return list.stream().filter(x -> !x.isEmpty())
                .map(String::trim).collect(Collectors.joining(", "));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        List<String> list = Arrays.asList((rs.getString(columnIndex) == null ? "" : rs.getString(columnIndex)).split("###"));
        return list.stream().filter(x -> !x.isEmpty())
                .map(String::trim).collect(Collectors.joining(", "));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        List<String> list = Arrays.asList((cs.getString(columnIndex) == null ? "" : cs.getString(columnIndex)).split("###"));
        return list.stream().filter(x -> !x.isEmpty())
                .map(String::trim).collect(Collectors.joining(", "));
    }



}
