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

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 9)
public class AssessmentRaw implements HasStringId, HasSubjectId, Serializable {

    private static final String[] POSITIVE = {"yes", "true"};
    private static final String[] NEGATIVE = {"no", "false"};

    private String id;
    private String subjectId;
    @Column(order = 1, columnName = "assessmentDate", displayName = "Date assessed", type = Column.Type.SSV)
    private Date assessmentDate;
    @Column(order = 1.5, columnName = "visitDate", displayName = "Visit date", type = Column.Type.SSV)
    private Date visitDate;
    @Column(order = 3, columnName = "visitNumber", displayName = "Visit number", type = Column.Type.SSV)
    private Integer visitNumber;
    @Column(order = 4, columnName = "lesionSite", displayName = "Site of lesion", type = Column.Type.SSV)
    private String lesionSite;
    @Builder.Default()
    private String newLesionSinceBaseline = "";

    private String assessmentMethod; // currently absents in DB
    private String response;
    private String responseShort;
    private Integer responseRank;
    private Date baselineDate;
    private String newLesionResponse;
    private Integer assessmentFrequency;

    @Column(order = 5, columnName = "assessmentMethod", displayName = "Method of assessment", type = Column.Type.SSV)
    public String getAssessmentMethod() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 6, columnName = "newLesionResponse", displayName = "Response at assessment", type = Column.Type.SSV)
    public String getNewLesionResponse() {

        if (newLesionSinceBaseline == null) {
            newLesionSinceBaseline = "";
        }

        if (Arrays.asList(POSITIVE).contains(newLesionSinceBaseline.toLowerCase())) {
            return YES;
        } else if (Arrays.asList(NEGATIVE).contains(newLesionSinceBaseline.toLowerCase())) {
            return NO;
        }
        return "";
    }

    public enum Response {
        NO_EVIDANCE_OF_DISEASE("No Evidence of Disease", 1, "NED"),
        COMPLETE_RESPONSE("Complete Response", 2, "CR"),
        PARTIAL_RESPONSE("Partial Response", 3, "PR"),
        STABLE_DISEASE("Stable Disease", 4, "SD"),
        PROGRESSIVE_DISEASE("Progressive Disease", 5, "PD"),
        NOT_EVALUABLE("Not Evaluable", 6, "NE"),
        NO_ASSESSMENT("No Assessment", Integer.MAX_VALUE - 1),
        MISSING_TARGET_LESIONS_RESPONSE("Missing Target Lesions", Integer.MAX_VALUE);

        @Getter
        private String name;
        @Getter
        private Integer rank;
        @Getter
        private String shortName;

        Response(String name, Integer rank, String shortName) {
            this.name = name;
            this.rank = rank;
            this.shortName = shortName;
        }

        Response(String name, Integer rank) {
            this.name = name;
            this.rank = rank;
            this.shortName = name;
        }

        public static Response getInstance(String name) {
            for (Response resp : Response.values()) {
                if (resp.getName().equals(name)) {
                    return resp;
                }
            }
            return null;
        }

        public static String getShortName(String name) {
            for (Response resp : Response.values()) {
                if (resp.getName().equals(name)) {
                    return resp.getShortName();
                }
            }
            return null;
        }
    }
}
