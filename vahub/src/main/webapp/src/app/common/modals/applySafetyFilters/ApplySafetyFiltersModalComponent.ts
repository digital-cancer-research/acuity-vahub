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

import {Component, OnInit, OnDestroy} from '@angular/core';
import {ApplySafetyFiltersModalService} from './ApplySafetyFiltersModalService';
import {DatasetViews} from '../../../security/DatasetViews';
import {PopulationFiltersModel} from '../../../filters/dataTypes/population/PopulationFiltersModel';

/**
 * Modal window that offers to apply safety population filter
 *
 * @property {boolean} modalIsVisible
 */
@Component({
    selector: 'apply-safety-filters-modal',
    templateUrl: 'ApplySafetyFiltersModalComponent.html'
})
export class ApplySafetyFiltersModalComponent implements OnInit, OnDestroy {

    modalIsVisible = false;

    constructor(private applySafetyFiltersModalService: ApplySafetyFiltersModalService,
                private populationFiltersModel: PopulationFiltersModel,
                protected datasetViews: DatasetViews) {
    }

    /**
     * Sets modal window visible
     */
    ngOnInit(): void {
        if (this.datasetViews.hasSafetyAsNoInPopulation() && this.applySafetyFiltersModalService.isReShowMessage()) {
            this.modalIsVisible = true;
        }
    }

    ngOnDestroy(): void {
    }

    /**
     * Hides modal window on cancel
     */
    cancelModal(): void {
        this.modalIsVisible = false;
        this.applySafetyFiltersModalService.setReShowMessage(false);
    }

    /**
     * Applies safety filter on OK
     */
    okModal(): void {
        this.modalIsVisible = false;
        this.applySafetyFiltersModalService.setReShowMessage(false);
        this.populationFiltersModel.setSafetyPopulationAsY();
        this.populationFiltersModel.getFilters(true, true);
    }
}
