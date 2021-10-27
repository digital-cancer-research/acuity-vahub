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
