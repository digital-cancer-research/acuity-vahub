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

package com.acuity.visualisations.cohorteditor.entity;

import com.acuity.visualisations.cohorteditor.util.FiltersObjectMapper;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

import static com.acuity.visualisations.cohorteditor.util.FiltersObjectMapper.fromString;

@Entity
@Table(name = "saved_filter_instance")
@Data
@ToString(exclude = "savedFilter")
@NoArgsConstructor
public class SavedFilterInstance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // parent saved filter
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "saved_filter_id")
    private SavedFilter savedFilter;
    // view type of filter, ie Aes, Labs
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private FilterTable filterView;
    // type of filter, ie is it a event filter or part of the filters to get the population cohort
    @JsonIgnore
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private SavedFilter.Type type;
    // the json of the filter
    @Column(length = Integer.MAX_VALUE)
    private String json;

    @JsonIgnore
    @Transient
    private Filters filters;

    public SavedFilterInstance(SavedFilter savedFilter, SavedFilter.Type type, Filters filters) {
        this.savedFilter = savedFilter;
        this.type = type;
        this.json = FiltersObjectMapper.toString(filters);

        if (filters instanceof LabFilters) {
            this.filterView = FilterTable.LABS;
        } else if (filters instanceof AeFilters) {
            this.filterView = FilterTable.AES;
        } else if (filters instanceof PopulationFilters) {
            this.filterView = FilterTable.POPULATION;
        }
    }

    @PostLoad
    public void postLoad() {
        getFilters();
    }

    public Filters<HasSubject> getFilters() {
        if (filters == null) {
            switch (filterView) {
                case AES:
                    return fromString(json, AeFilters.class);
                case LABS:
                    return fromString(json, LabFilters.class);
                case POPULATION:
                    return fromString(json, PopulationFilters.class);
                default:
                    throw new IllegalArgumentException("Invalid filterView: " + filterView);
            }
        } else {
            return filters;
        }
    }

    public enum FilterTable {
        AES, LABS, POPULATION
    }
}
