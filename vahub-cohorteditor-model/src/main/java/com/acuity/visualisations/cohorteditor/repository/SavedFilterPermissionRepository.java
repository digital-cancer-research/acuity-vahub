package com.acuity.visualisations.cohorteditor.repository;

import com.acuity.visualisations.cohorteditor.entity.SavedFilterPermission;
import org.springframework.data.repository.CrudRepository;

public interface SavedFilterPermissionRepository extends CrudRepository<SavedFilterPermission, Long> {
    
}
