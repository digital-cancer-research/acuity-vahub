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
    CheckListFilterItemModel,
    RangeFilterItemModel,
    RangeDateFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class CardiacFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new CheckListFilterItemModel('measurementCategory', 'Measurement category'));
        this.itemsModels.push(new CheckListFilterItemModel('measurementName', 'Measurement name'));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementTimePoint', 'Measurement time point'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudy', 'Days on study', 1));
        this.itemsModels.push(new RangeFilterItemModel('visitNumber', 'Visit number', 0.001));
        this.itemsModels.push(new RangeFilterItemModel('resultValue', 'Result value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('resultUnit', 'Result unit', false, 4));
        this.itemsModels.push(new RangeFilterItemModel('baselineValue', 'Baseline value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('changeFromBaselineValue', 'Change from baseline', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('percentageChangeFromBaselineValue', 'Percent change from baseline', 0.01));
        this.itemsModels.push(new CheckListFilterItemModel('baselineFlag', 'Baseline flag'));
        this.itemsModels.push(new CheckListFilterItemModel('clinicallySignificant', 'Clinically significant'));

        this.itemsModels.push(new ListFilterItemModel('protocolScheduleTimepoint', 'Protocol Schedule Timepoint'));
        this.itemsModels.push(new RangeDateFilterItemModel('dateOfLastDose', 'Date Of Last Drug Dose'));
        this.itemsModels.push(new ListFilterItemModel('lastDoseAmount', 'Last Drug Dose Amount'));
        this.itemsModels.push(new ListFilterItemModel('method', 'Method'));
        this.itemsModels.push(new CheckListFilterItemModel('atrialFibrillation', 'Atrial Fibrillation'));
        this.itemsModels.push(new CheckListFilterItemModel('sinusRhythm', 'Sinus Rhythm'));
        this.itemsModels.push(new ListFilterItemModel('reasonNoSinusRhythm', 'Reason, No Sinus Rhythm'));
        this.itemsModels.push(new ListFilterItemModel('heartRhythm', 'Heart Rhythm'));
        this.itemsModels.push(new ListFilterItemModel('heartRhythmOther', 'Heart Rhythm, Other'));
        this.itemsModels.push(new CheckListFilterItemModel('extraSystoles', 'Extra Systoles'));
        this.itemsModels.push(new ListFilterItemModel('specifyExtraSystoles', 'Specify Extra Systoles'));
        this.itemsModels.push(new ListFilterItemModel('typeOfConduction', 'Type Of Conduction'));
        this.itemsModels.push(new CheckListFilterItemModel('conduction', 'Conduction'));
        this.itemsModels.push(new ListFilterItemModel('reasonAbnormalConduction', 'Reason, Abnormal Conduction'));
        this.itemsModels.push(new CheckListFilterItemModel('sttChanges', 'ST-T Changes'));
        this.itemsModels.push(new ListFilterItemModel('stSegment', 'ST Segment'));
        this.itemsModels.push(new ListFilterItemModel('wave', 'T-Wave'));
        this.itemsModels.push(new RangeFilterItemModel('beatGroupNumber', 'Beat Group Number', 1));
        this.itemsModels.push(new RangeFilterItemModel('beatNumberWithinBeatGroup', 'Beat Number Within Beat Group', 1));
        this.itemsModels.push(new RangeFilterItemModel('numberOfBeatsInAverageBeat', 'Number of Beats in Average Beat', 1));
        this.itemsModels.push(new RangeFilterItemModel('beatGroupLengthInSec', 'Beat Group Length (sec)', 0.01));
        this.itemsModels.push(new ListFilterItemModel('comment', 'Cardiologist Comment'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setCardiacFilter(serverModel);
    }

    getName(): string {
        return 'cardiac';
    }

    getDisplayName(): string {
        return 'Cardiac';
    }

    getModulePath(): string {
        return 'cardiac';
    }

    protected getFilterPath(): string {
        return 'new-filters';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasCardiacData() : false;
    }

    transformFiltersFromServer(filters: any): void {
        super.transformFiltersFromServer(filters);
    }
}
