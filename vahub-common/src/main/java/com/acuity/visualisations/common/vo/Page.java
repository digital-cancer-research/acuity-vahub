package com.acuity.visualisations.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Support pagination
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page  implements Serializable {
    /**
     * Number of the current page, first page is 1
     */
    private Integer pageNumber;

    /**
     * Maximum number of items per page.
     */
    private Integer itemsPerPage;

    /**
     * Total number of items in all pages.
     */
    private Integer totalItems;
}
