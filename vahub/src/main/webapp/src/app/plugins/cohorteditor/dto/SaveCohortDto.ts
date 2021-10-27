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

import {PopulationFiltersModel, AesFiltersModel} from '../../../filters/dataTypes/module';
import UserVO = Request.UserVO;

/**
 * DTO class to make calls to saving cohorts cleaner
 */
export class SaveCohortDto {
    cohortId: number;
    cohortName: string;
    populationFilterId: number;
    aeFilterId: number;
    localPopulationFiltersModel: PopulationFiltersModel;
    localAeFiltersModel: AesFiltersModel;
    shareWith: UserVO[];

    constructor(cohortId: number, cohortName: string, shareWith: UserVO[], populationFilterId: number, aeFilterId: number,
        localPopulationFiltersModel: PopulationFiltersModel, localAeFiltersModel: AesFiltersModel) {

        this.cohortId = cohortId;
        this.cohortName = cohortName;
        this.shareWith = shareWith;
        this.populationFilterId = populationFilterId;
        this.aeFilterId = aeFilterId;
        this.localPopulationFiltersModel = localPopulationFiltersModel;
        this.localAeFiltersModel = localAeFiltersModel;
    }
}
