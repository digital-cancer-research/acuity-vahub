package com.acuity.visualisations.common.config;

/**
 *
 * @author ksnd199
 */
public final class Constants {

    private Constants() {
    }

    /**
     * Prefix of all cache names that can be refreshed on a schedule.
     */
    public static final String ACUITY_DAILY_REFRESHABLE_CACHE = "acuity-refreshable-";
    public static final String DETECT_PERSISTENT_CACHE = "detect-persistent-";

    /**
     * Request parameters for VA-Security permissionEvaluator
     */
    public static final String VIEW_ONCOLOGY_PERMISSION = "VIEW_ONCOLOGY_PACKAGE";
    public static final String VIEW_VISUALISATIONS_PERMISSION = "VIEW_VISUALISATIONS";

    /**
     * @PreAuthorize annotation parameters from vahub application module.
     * Values are duplicated from "vahub" module to avoid creation of maven circular dependency.
     */
    public static final String HAS_PERMISSION_VIEW_ONCOLOGY_PACKAGE =
            "@permissionEvaluator.hasViewDatasetWithExtraPermission(authentication, #requestBody.datasets, 'VIEW_ONCOLOGY_PACKAGE')";
    public static final String HAS_VIEW_DATASET_PERMISSION =
            "@permissionEvaluator.hasViewDatasetPermission(authentication, #requestBody.datasets, 3)"; // 3 = AUTHORISED_USER

}
