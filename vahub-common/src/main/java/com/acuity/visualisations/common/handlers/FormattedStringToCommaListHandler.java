/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
