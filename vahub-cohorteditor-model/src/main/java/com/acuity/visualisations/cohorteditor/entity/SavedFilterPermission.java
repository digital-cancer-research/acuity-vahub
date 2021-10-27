package com.acuity.visualisations.cohorteditor.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_filter_permission")
@Data
@ToString(exclude = "savedFilter")
@NoArgsConstructor
@AllArgsConstructor
public class SavedFilterPermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // parent saved filter
    @ManyToOne
    @JoinColumn(name = "saved_filter_id")
    private SavedFilter savedFilter;
    /*
     * the prid of who has access to view/use this saved filter
     */
    private String prid;

    public SavedFilterPermission(SavedFilter savedFilter, String prid) {
        this.savedFilter = savedFilter;
        this.prid = prid;
    }
}
