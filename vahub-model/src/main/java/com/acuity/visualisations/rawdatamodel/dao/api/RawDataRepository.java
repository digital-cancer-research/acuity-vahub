package com.acuity.visualisations.rawdatamodel.dao.api;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Transactional(readOnly = true)
public interface RawDataRepository<T> {

    Collection<T> getRawData(@Param("datasetId") long datasetId);
}
