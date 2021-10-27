package com.acuity.visualisations.rawdatamodel.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.google.common.collect.Lists.newArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public abstract class UsedInTflFilters<T> extends Filters<T> {
          
    protected SetFilter<Boolean> usedInTfl = new SetFilter<>(); 
    
    @JsonIgnore
    public void addUsedInTflFilter() {
        usedInTfl = new SetFilter(newArrayList(true));
    }
    
    @JsonIgnore
    public void removeUsedInTflFilter() {
        usedInTfl = new SetFilter<>(); 
    }
}
