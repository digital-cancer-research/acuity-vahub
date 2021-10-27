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
    RangeDateFilterItemModel,
    MapListFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
import {AeDetailLevel} from '../../../common/trellising/store/ITrellising';

@Injectable()
export class AesFiltersModel extends AbstractEventFiltersModel {

    static AES_NUMBER_KEY = 'aeNumber';

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new ListFilterItemModel('pt', 'Preferred term', false, 4));
        this.itemsModels.push(new ListFilterItemModel('hlt', 'Higher level term', false, 4));
        this.itemsModels.push(new ListFilterItemModel('soc', 'System organ class', false, 4));
        this.itemsModels.push(new ListFilterItemModel('specialInterestGroup', 'Special interest group'));
        this.itemsModels.push(new CheckListFilterItemModel('severity', 'Max. severity'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));
        this.itemsModels.push(new RangeFilterItemModel('duration', 'Event duration', 1));
        this.itemsModels.push(new CheckListFilterItemModel('studyPeriods', 'Study period'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudyAtStart', 'Days on study at AE start', 1));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudyAtEnd', 'Days on study at AE end', 1));
        this.itemsModels.push(new RangeFilterItemModel('daysFromPrevDoseToStart', 'Days from previous dose to AE start', 1));
        this.itemsModels.push(new CheckListFilterItemModel('serious', 'Serious'));
        this.itemsModels.push(new ListFilterItemModel('actionTaken', 'Action taken'));
        this.itemsModels.push(new MapListFilterItemModel('drugsActionTaken', 'Action taken'));
        this.itemsModels.push(new CheckListFilterItemModel('requiresHospitalisation', 'Requires or prolongs hospitalisation'));
        this.itemsModels.push(new CheckListFilterItemModel('treatmentEmergent', 'Treatment emergent'));
        this.itemsModels.push(new CheckListFilterItemModel('causality', 'Causality'));
        this.itemsModels.push(new MapListFilterItemModel('drugsCausality', 'Causality'));
        this.itemsModels.push(new ListFilterItemModel('description', 'Description', false, 4));
        this.itemsModels.push(new ListFilterItemModel('comment', 'Comment', false, 4));

        this.itemsModels.push(new CheckListFilterItemModel('outcome', 'AE Outcome'));
        this.itemsModels.push(new CheckListFilterItemModel('requiredTreatment', 'AE Required treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('causedSubjectWithdrawal', 'AE Caused subject withdrawal'));
        this.itemsModels.push(new CheckListFilterItemModel('doseLimitingToxicity', 'Dose limiting toxicity'));
        this.itemsModels.push(new CheckListFilterItemModel('timePointDoseLimitingToxicity', 'Time point of dose limiting toxicity'));
        this.itemsModels.push(new CheckListFilterItemModel('immuneMediated', 'Immune mediated'));
        this.itemsModels.push(new CheckListFilterItemModel('infusionReaction', 'Infusion reaction'));
        this.itemsModels.push(new CheckListFilterItemModel('suspectedEndpoint', 'Suspected Endpoint'));
        this.itemsModels.push(new ListFilterItemModel('suspectedEndpointCategory', 'Suspected Endpoint Category'));
        this.itemsModels.push(new CheckListFilterItemModel('aeOfSpecialInterest', 'AE of Special Interest'));
        this.itemsModels.push(new ListFilterItemModel(AesFiltersModel.AES_NUMBER_KEY, 'Subject AE Number'));
        this.itemsModels.push(new CheckListFilterItemModel('aeStartPriorToRandomisation', 'AE start prior to randomisation'));
        this.itemsModels.push(new CheckListFilterItemModel('aeEndPriorToRandomisation', 'AE end prior to randomisation'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setAesFilter(serverModel);
    }

    getName(): string {
        return 'aes';
    }

    getDisplayName(): string {
        return 'Adverse Event';
    }

    getModulePath(): string {
        return 'aes';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasAesData() : false;
    }

    // Additional parameter required to display right colours
    transformFiltersToServer(manuallyApplied = false, reseted: boolean = false): any {
        const toServerObject: any = super.transformFiltersToServer(manuallyApplied, reseted);
        if (this.isTimeline()) {
            toServerObject.aeDetailLevel = AeDetailLevel.PER_SEVERITY_CHANGE;
        }
        return toServerObject;
    }

    protected _getFiltersImpl(manuallyApplied = false, reseted: boolean = false, triggeredByPopulation = false): void {
        this.loading = true;

        if (this.pendingRequest) {
            this.pendingRequest.unsubscribe();
        }

        // when we leave the view with event filters applied modal window appears
        // we want to emit this to notify deactivator that it needs to update event filter state
        if (reseted) {
            this.resetEventFilterEvent.next(null);
        }

        // emit the event to the rest of the app
        if (this.firstEventEmitted && !(this.isTimeline() && triggeredByPopulation)) {
            const serverObject = super.transformFiltersToServer(manuallyApplied, reseted);
            if (this.isTimeline()) {
                this.emitEvent(super.transformFiltersToServer(manuallyApplied, reseted));
            } else {
                this.emitEvent(this.transformFiltersToServer(manuallyApplied, reseted));
            }
        } else {
            this.firstEventEmitted = true;
        }

        this.makeFilterRequest(manuallyApplied);
    }
}
