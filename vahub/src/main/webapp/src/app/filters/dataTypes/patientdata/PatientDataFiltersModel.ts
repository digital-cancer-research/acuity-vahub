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

import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {
    ListFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel,
    CheckListFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
//to apply this filters uncomment lines 43-49 in TimelineFilterComponent.html
@Injectable()
export class PatientDataFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('measurementName', 'Measurement name'));
        this.itemsModels.push(new RangeFilterItemModel('value', 'Value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('unit', 'Unit'));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementDate', 'Measurement date'));
        this.itemsModels.push(new RangeDateFilterItemModel('reportDate', 'Report date'));
        this.itemsModels.push(new ListFilterItemModel('sourceType', 'Source type'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setPatientDataFilter(serverModel);
    }

    getName(): string {
        return 'patientData';
    }

    getDisplayName(): string {
        return 'PatientData';
    }

    getModulePath(): string {
        return 'timeline/patientdata';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasPatientData() : false;
    }
}
