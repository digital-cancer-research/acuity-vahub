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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.vo.AesTable;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author ksnd199
 */
public final class AeTableUtil {

    private static final String TERM = "Term";
    private static final String GRADE = "Max. severity grade experienced";
    private static final String NO_INCIDENCE = "No incidence";
    private static final String NUMBER_OF_SUBJECTS = "\nNumber of subjects";
    private static final String ZERO_CELL = "0 (0%)";

    private AeTableUtil() {
    }

    public static void writeAesTableToCsv(List<AesTable> data, Writer writer) {

        if (!data.isEmpty()) {
            List<String> treatArms = data.stream().map(AesTable::getTreatmentArm).distinct().sorted().collect(Collectors.toList());

            List<String> headersList = new ArrayList<>();
            headersList.add(TERM);
            headersList.add(GRADE);
            List<String> treatArmColumnHeaders = treatArms.stream().map(treatArm -> treatArm + NUMBER_OF_SUBJECTS).collect(Collectors.toList());
            headersList.addAll(treatArmColumnHeaders);
            String[] columnHeaders = new String[treatArmColumnHeaders.size() + 2];
            columnHeaders = headersList.toArray(columnHeaders);

            List<Map<String, String>> rows = prepareTableExportData(data, treatArms);
            try (ICsvMapWriter csvMapWriter = new CsvMapWriter(writer, CsvPreference.EXCEL_PREFERENCE)) {
                csvMapWriter.writeHeader(columnHeaders);
                for (Map<String, String> row : rows) {
                    csvMapWriter.write(row, columnHeaders);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static List<Map<String, String>> prepareTableExportData(List<AesTable> data, List<String> treatArms) {
        Map<String, Integer> treatArmSubjectCountPerArm = treatArms.stream().collect(Collectors.toMap(treatArm -> treatArm,
                treatArm -> data.stream().filter(aeTable -> treatArm.equals(aeTable.getTreatmentArm())).findFirst().get().getSubjectCountPerArm()));

        List<Map<String, String>> rows = new ArrayList<>();
        List<String> terms = data.stream().map(AesTable::getTerm).distinct().collect(Collectors.toList());
        Map<String, List<AesTable>> termAesTablesMap = terms.stream().collect(Collectors.toMap(term -> term,
                term -> data.stream().filter(table -> term.equals(table.getTerm())).collect(Collectors.toList())));

        Map<String, List<AesTable>> sortedMap = new TreeMap<>(Comparator.naturalOrder());
        sortedMap.putAll(termAesTablesMap);

        sortedMap.forEach((key, value) -> {
            putTermRow(key, treatArms, rows);
            putGradeAndNoIncidenceRow(rows, value, treatArms, treatArmSubjectCountPerArm);
        });
        return rows;
    }

    private static String getExportPercentString(Integer num, AesTable item) {
        return String.format("%d (%.2f%%)", num, ((double) (num * 100)) / (item.getSubjectCountPerTerm() + item.getNoIncidenceCount()));
    }

    private static void putTermRow(String term, List<String> treatArms, List<Map<String, String>> rows) {
        Map<String, String> termRow = new HashMap<>();
        termRow.put(TERM, term);
        termRow.put(GRADE, "");
        for (String header : treatArms) {
            termRow.put(header, "");
        }
        rows.add(termRow);
    }

    private static void putGradeAndNoIncidenceRow(List<Map<String, String>> rows, List<AesTable> aesTables, List<String> treatArms,
                                                  Map<String, Integer> treatArmSubjectCountPerArm) {
        Map<String, String> noIncidenceRow = new HashMap<>();
        noIncidenceRow.put(TERM, "");
        noIncidenceRow.put(GRADE, NO_INCIDENCE);

        Map<String, List<AesTable>> gradeAesTablesMap = aesTables.stream().map(AesTable::getGrade).distinct()
                .collect(Collectors.toMap(grade -> grade, grade
                        -> aesTables.stream().filter(table -> grade.equals(table.getGrade())).collect(Collectors.toList())));

        Map<String, List<AesTable>> sortedGradeAesTablesMap = new TreeMap<>(Comparator.naturalOrder());
        sortedGradeAesTablesMap.putAll(gradeAesTablesMap);

        Set<String> existingTreatmentArms = new HashSet<>();
        sortedGradeAesTablesMap.values().forEach(values ->
                existingTreatmentArms.addAll(values.stream().map(AesTable::getTreatmentArm).collect(Collectors.toList())));

        List<String> missedArms = treatArms.stream().filter(arm -> !existingTreatmentArms.contains(arm)).collect(Collectors.toList());

        sortedGradeAesTablesMap.forEach((key, value) -> {
            Map<String, String> row = new HashMap<>();
            row.put(TERM, "");
            row.put(GRADE, key);
            value.forEach(aeTable -> {
                treatArms.stream().filter(treatArm -> treatArm.equals(aeTable.getTreatmentArm())).forEach(
                        treatArm -> row.put(treatArm + NUMBER_OF_SUBJECTS, getExportPercentString(aeTable.getSubjectCountPerGrade(), aeTable)));
                noIncidenceRow.put(aeTable.getTreatmentArm() + NUMBER_OF_SUBJECTS, getExportPercentString(aeTable.getNoIncidenceCount(), aeTable));
                if (row.size() < treatArms.size() + 2) {
                    treatArms.stream().filter(treatArm -> row.get(treatArm + NUMBER_OF_SUBJECTS) == null)
                            .forEach(treatArm -> row.put(treatArm + NUMBER_OF_SUBJECTS, ZERO_CELL));
                }
            });

            missedArms.forEach(treatArm ->
                    noIncidenceRow.put(treatArm + NUMBER_OF_SUBJECTS, treatArmSubjectCountPerArm.get(treatArm).toString() + " (100%)"));

            rows.add(row);
        });
        rows.add(noIncidenceRow);
    }
}
