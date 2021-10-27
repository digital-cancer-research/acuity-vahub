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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerData;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerParameters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

public enum BiomarkerGroupByOptions implements GroupByOption<Biomarker> {

    /**
     * This attribute should be supplied with {@code java.util.Map<String, Integer>}
     * object in {@code com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.CONTEXT} parameter
     */
    @AcceptsAttributeContext(required = true)
    GENE_PERCENTAGE(Biomarker.Attributes.GENE) {
        @Override
        public EntityAttribute<Biomarker> getAttribute() {
            //this cannot be used without params
            throw new IllegalStateException("Cannot use GENE_PERCENTAGE without context param");
        }

        @Override
        @SuppressWarnings("unchecked")
        public EntityAttribute<Biomarker> getAttribute(Params params) {
            final Object o = params.get(Param.CONTEXT);
            Map<String, Integer> genePercentage = (Map<String, Integer>) o;
            return EntityAttribute.attribute("GENE_PERCENTAGE", (Biomarker b) -> new GenePercentage(b.getEvent().getGene(),
                    genePercentage.get(b.getEvent().getGene())));
        }
    },
    SUBJECT(Biomarker.Attributes.SUBJECT),

    @AcceptsAttributeContext
    BIOMARKER_DATA(Biomarker.Attributes.MUTATION) {
        @Override
        public EntityAttribute<Biomarker> getAttribute() {
            //this cannot be used without params
            throw new IllegalStateException("Cannot use BIOMARKER_DATA without context param");
        }

        @Override
        @SuppressWarnings("unchecked")
        public EntityAttribute<Biomarker> getAttribute(Params params) {
            final Object o = params.get(Param.CONTEXT);
            Map<String, Map<String, List<BiomarkerParameters>>> subjectGeneBioParams = (Map<String, Map<String, List<BiomarkerParameters>>>) o;

            return EntityAttribute.attribute("BIOMARKER_DATA", (Biomarker b) -> BiomarkerData.builder()
                    .subjectCode(b.getSubjectCode())
                    .gene(b.getEvent().getGene())
                    .biomarkerParameters(subjectGeneBioParams.get(b.getSubjectCode()) == null ? EMPTY_LIST
                            : subjectGeneBioParams.get(b.getSubjectCode()).getOrDefault(b.getEvent().getGene(), EMPTY_LIST))
                    .totalNumberOfAlterations(subjectGeneBioParams.get(b.getSubjectCode()) == null ? 0
                            : (subjectGeneBioParams.get(b.getSubjectCode()).get(b.getEvent().getGene()) == null ? 0
                            : subjectGeneBioParams.get(b.getSubjectCode()).get(b.getEvent().getGene()).size()))
                    .build());
        }
    },
    ALTERATION_TYPE(Biomarker.Attributes.MUTATION) {
        @Override
        public EntityAttribute<Biomarker> getAttribute() {
            //this cannot be used without params
            throw new IllegalStateException("Cannot use ALTERATION_TYPE without context param");
        }

        @Override
        @SuppressWarnings("unchecked")
        public EntityAttribute<Biomarker> getAttribute(Params params) {
            final Object o = params.get(Param.CONTEXT);
            Map<String, Map<String, List<BiomarkerParameters>>> subjectGeneBioParams = (Map<String, Map<String, List<BiomarkerParameters>>>) o;
            return EntityAttribute.attribute("ALTERATION_TYPE", (Biomarker b) -> {
                List<BiomarkerParameters> bioparams = subjectGeneBioParams.get(b.getSubjectCode()) == null ? EMPTY_LIST
                        : subjectGeneBioParams.get(b.getSubjectCode()).getOrDefault(b.getEvent().getGene(), EMPTY_LIST);
                return BiomarkerData.builder().biomarkerParameters(bioparams).build().getPriorityMutation();
            });
        }
    };

    private Biomarker.Attributes originAttribute;

    BiomarkerGroupByOptions(Biomarker.Attributes originAttribute) {
        this.originAttribute = originAttribute;
    }


    @Override
    public EntityAttribute<Biomarker> getAttribute() {
        return originAttribute.getAttribute();
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class GenePercentage implements Comparable<GenePercentage> {
        private String gene;
        private Integer percentage;

        @Override
        public String toString() {
            return percentage + "% " + gene;
        }

        @Override
        public int compareTo(GenePercentage o) {
            // Y categories must be sorted on the plot by percentage DESC, than by gene name ASC;
            // Y categories are displayed from bottom to top, that's why sorting order is reversed in this function
            return Comparator.comparing(GenePercentage::getPercentage).reversed().thenComparing(GenePercentage::getGene).reversed().compare(this, o);
        }

        // calculate percentage of alteration in each gene within the filtered population
        public static Map<String, Integer> getGenePercentageMap(FilterResult<Biomarker> filtered) {

            long populationCount = filtered.getPopulationFilterResult().size();
            Map<String, Set<String>> geneSubjects = filtered.stream().collect(groupingBy((Biomarker b) -> b.getEvent().getGene(),
                    mapping(Biomarker::getSubjectCode, Collectors.toSet())));

            return geneSubjects.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    e -> (int) Math.round(e.getValue().size() * 100.0 / populationCount)));
        }
    }
}
