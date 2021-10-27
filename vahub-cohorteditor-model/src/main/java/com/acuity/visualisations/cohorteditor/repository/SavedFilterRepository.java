package com.acuity.visualisations.cohorteditor.repository;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SavedFilterRepository extends CrudRepository<SavedFilter, Long>, SavedFilterRepositoryCustom {

    @Query("SELECT DISTINCT sf "
            + "FROM SavedFilter sf "
            + "JOIN FETCH sf.instances "
            + "WHERE sf.id IN ("
            + "   SELECT sf.id "
            + "       FROM SavedFilter sf "
            + "     JOIN sf.permissions p "
            + "     WHERE p.prid = :prid"
            + ") "
            + "OR sf.owner = :prid "
            + "ORDER BY sf.createdDate DESC")
    List<SavedFilter> listByUser(@Param("prid") String prid);

    List<SavedFilter> findByName(String name);
}
