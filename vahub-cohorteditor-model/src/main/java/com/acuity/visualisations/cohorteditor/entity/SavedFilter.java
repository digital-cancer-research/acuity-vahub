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

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

@Entity
@Table(name = "saved_filter")
@Data
@ToString(exclude = {"permissions", "instances"})
@NoArgsConstructor
public class SavedFilter implements Serializable {
    /**
     * SQL like operator for the filter
     */
    public enum Operator {
        AND, OR;
    }

    /**
     * type of filter, ie is it a event filter or part of the filters to get the population cohort
     */
    public enum Type {
        EVENT, COHORT;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /*
     * name of saved filter
     */
    //@NotNull
    private String name;
    /*
     * time of creation of this filter
     */
    // @NotNull
    @Column(name = "created_date", updatable = false)
    private Date createdDate;
    /*
     * owner prid
     */
    //@NotNull
    @Column(updatable = false)
    private String owner;
    /*
     * operator of all filters when applied
     */
    //@NotNull
    @Enumerated(EnumType.STRING)
    private SavedFilter.Operator operator = SavedFilter.Operator.AND;

    @Column(name = "dataset_id", updatable = false)
    private String datasetId;

    @Column(name = "dataset_class", updatable = false)
    private String datasetClass;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "savedFilter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedFilterPermission> permissions = newArrayList();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "savedFilter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedFilterInstance> instances = newArrayList();

    public SavedFilter(String name, Date createdDate, String owner) {
        this.name = name;
        this.createdDate = createdDate;
        this.owner = owner;
    }

    @JsonIgnore
    public List<SavedFilterPermission> getPermissions() {
        return permissions;
    }

    public void addSavedFilterInstance(SavedFilterInstance savedFilterInstance) {
        this.instances.add(savedFilterInstance);
    }

    public void addSavedFilterPermission(SavedFilterPermission savedFilterPermission) {
        this.permissions.add(savedFilterPermission);
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
    }

    public List<Filters> getFilters() {
        return this.instances.stream().map(SavedFilterInstance::getFilters).collect(toList());
    }

    /**
     * This is required so that orphans can be deleted on an update
     * @param permissions All of the permissions to add
     */
    public void setPermissions(List<SavedFilterPermission> permissions) {
        this.permissions.clear();
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }

    /**
     * This is required so that orphans can be deleted on an update
     * @param instances All of the instances to add
     */
    public void setInstances(List<SavedFilterInstance> instances) {
        this.instances.clear();
        if (instances != null) {
            this.instances.addAll(instances);
        }
    }
}
