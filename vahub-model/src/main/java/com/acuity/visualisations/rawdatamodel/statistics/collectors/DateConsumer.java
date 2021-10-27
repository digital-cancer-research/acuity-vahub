package com.acuity.visualisations.rawdatamodel.statistics.collectors;

import java.util.Date;

/**
 *
 * @author ksnd199
 */
@FunctionalInterface
public interface DateConsumer {

    void accept(Date i);

}
