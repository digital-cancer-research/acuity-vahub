package com.acuity.visualisations.common.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringToListTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        String string;
        string = parameter.stream().map(s -> String.format("%1$-30s", s)).collect(Collectors.joining("###"));
        ps.setString(i, string);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (result == null || "ALL".equals(result) || " ".equals(result)) {
            return new ArrayList<>();
        } else {
            List<String> list = Arrays.asList(rs.getString(columnName).split("###"));
            return list.stream().map(String::trim).filter(c -> !c.isEmpty()).collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (result == null || "ALL".equals(result) || " ".equals(result)) {
            return new ArrayList<>();
        } else {
            List<String> list = Arrays.asList(rs.getString(columnIndex).split("###"));
            return list.stream().map(String::trim).filter(c -> !c.isEmpty()).collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (result == null || "ALL".equals(result) || " ".equals(result)) {
            return new ArrayList<>();
        } else {
            List<String> list = Arrays.asList(cs.getString(columnIndex).split("###"));
            return list.stream().map(String::trim).filter(c -> !c.isEmpty()).collect(Collectors.toList());
        }
    }
}
