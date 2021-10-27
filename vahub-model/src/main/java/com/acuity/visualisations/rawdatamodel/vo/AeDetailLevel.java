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

package com.acuity.visualisations.rawdatamodel.vo;

/**
 * Created by knml167 on 11/7/2016.
 */
public enum AeDetailLevel {
    
    PER_INCIDENCE("Per AE Incidence"),
    PER_SEVERITY_CHANGE("Per AE Severity Change");

    private final String display;

    AeDetailLevel(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
