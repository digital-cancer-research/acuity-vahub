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
import * as  _ from 'lodash';

import {AbstractFiltersModel} from '../AbstractFiltersModel';
import {
    BaseFilterItemModel,
    CheckListFilterItemModel,
    CohortFilterItemModel,
    ListFilterItemModel,
    MapListFilterItemModel,
    MapRangeDateFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel
} from '../../components/module';
import {StudySpecificFilterModel} from '../../components/studySpecific/StudySpecificFilterModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {List} from 'immutable';
import {UpdateAvailableSubjects} from '../../../common/store/actions/SharedStateActions';
import {getServerPath} from '../../../common/utils/Utils';

@Injectable()
export class PopulationFiltersModel extends AbstractFiltersModel {

    static SUBJECT_IDS_KEY = 'subjectId';
    static SAFETY_POPULATION_IDS_KEY = 'safetyPopulation';
    static COHORT_EDITOR_KEY = 'cohortEditorSubjects';
    static NO_INTERSECT_OF_SUBJECTS = 'No subjects';

    // bug where PopulationFiltersModel is generated after study selection event.
    hasInitData = false;

    constructor(private filterHttpService: FilterHttpService,
                private filterEventService: FilterEventService,
                private _store: Store<ApplicationState>,
                protected datasetViews: DatasetViews) {
        super();

        this.itemsModels.push(new CheckListFilterItemModel(PopulationFiltersModel.SAFETY_POPULATION_IDS_KEY, 'Safety Population'));
        this.itemsModels.push(new CheckListFilterItemModel('studyIdentifier', 'Study ID'));
        this.itemsModels.push(new CheckListFilterItemModel('studyPart', 'Study Part ID'));
        this.itemsModels.push(new ListFilterItemModel(PopulationFiltersModel.SUBJECT_IDS_KEY, 'Subject ID', true));
        this.itemsModels.push(new ListFilterItemModel('attendedVisits', 'Attended Visit Numbers'));
        this.itemsModels.push(new ListFilterItemModel('studyPeriodsParticipated', 'Study Periods Participated'));
        this.itemsModels.push(new RangeFilterItemModel('durationOnStudy', 'Duration on Study', 1));
        this.itemsModels.push(new CheckListFilterItemModel('randomised', 'Randomised'));
        this.itemsModels.push(new RangeDateFilterItemModel('randomisationDate', 'Date of Randomisation'));
        this.itemsModels.push(new ListFilterItemModel('withdrawalCompletion', 'Withdrawal/Completion'));
        this.itemsModels.push(new RangeDateFilterItemModel('withdrawalCompletionDate', 'Date of Withdrawal/Completion'));
        this.itemsModels.push(new ListFilterItemModel('withdrawalCompletionReason', 'Reason for Withdrawal/Completion'));
        this.itemsModels.push(new CheckListFilterItemModel('plannedTreatmentArm', 'Planned treatment arm'));
        this.itemsModels.push(new CheckListFilterItemModel('actualTreatmentArm', 'Actual treatment arm'));
        this.itemsModels.push(new ListFilterItemModel('doseCohort', 'Cohort (Dose)'));
        this.itemsModels.push(new ListFilterItemModel('otherCohort', 'Cohort (Other)'));

        this.itemsModels.push(new StudySpecificFilterModel('studySpecificFilters', 'Study specific filters'));

        this.itemsModels.push(new MapListFilterItemModel('drugsDosed', 'Drugs dosed'));
        this.itemsModels.push(new MapListFilterItemModel('drugsMaxDoses', 'Drugs max doses'));
        this.itemsModels.push(new MapListFilterItemModel('drugsMaxFrequencies', 'Drugs max frequencies'));
        this.itemsModels.push(new MapListFilterItemModel('drugsDiscontinued', 'Drugs discontinued'));
        this.itemsModels.push(new MapRangeDateFilterItemModel('drugsDiscontinuationDate', 'Drugs discontinuation date'));
        this.itemsModels.push(new MapListFilterItemModel('drugsDiscontinuationReason', 'Drugs discontinuation reason'));
        // Remove days on treatment for current release
        // this.itemsModels.push(new MapRangeFilterItemModel('drugsTotalDurationInclBreaks', 'Drugs total duration incl breaks'));
        // this.itemsModels.push(new MapRangeFilterItemModel('drugsTotalDurationExclBreaks', 'Drugs total duration excl breaks'));
        //
        this.itemsModels.push(new RangeFilterItemModel('exposureInDays', 'Exposure (Days)', 1));
        this.itemsModels.push(new RangeFilterItemModel('actualExposureInDays', 'Actual Exposure (Days)', 1));

        this.itemsModels.push(new ListFilterItemModel('siteIDs', 'Site ID'));
        this.itemsModels.push(new ListFilterItemModel('centreNumbers', 'Centre numbers'));

        this.itemsModels.push(new ListFilterItemModel('country', 'Country', true));
        this.itemsModels.push(new ListFilterItemModel('regions', 'Regions'));
        this.itemsModels.push(new CheckListFilterItemModel('sex', 'Sex'));
        this.itemsModels.push(new CheckListFilterItemModel('race', 'Race'));
        this.itemsModels.push(new RangeFilterItemModel('age', 'Age', 1));
        this.itemsModels.push(new RangeDateFilterItemModel('firstTreatmentDate', 'First treatment date'));
        this.itemsModels.push(new RangeDateFilterItemModel('lastTreatmentDate', 'Last treatment date'));
        this.itemsModels.push(new CheckListFilterItemModel('phase', 'Phase'));
        this.itemsModels.push(new CheckListFilterItemModel('death', 'Death'));
        this.itemsModels.push(new RangeDateFilterItemModel('deathDate', 'Date of Death'));
        this.itemsModels.push(new ListFilterItemModel('medicalHistory', 'Medical histories', true, 4));
        this.itemsModels.push(new MapListFilterItemModel('biomarkerGroups', 'Biomarker groups'));
        this.itemsModels.push(new ListFilterItemModel('ethnicGroup', 'Ethnic Group'));

        this.itemsModels.push(new ListFilterItemModel('specifiedEthnicGroup', 'Specified ethnic groups'));
        this.isPopulationFilter = true;
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setPopulationFilter(serverModel);
    }

    getName(): string {
        return 'pop';
    }

    getDisplayName(): string {
        return 'Population';
    }

    getModulePath(): string {
        return 'population';
    }

    getSelectedSubjectIds(): Array<string> {
        return this.getSelectedValues(PopulationFiltersModel.SUBJECT_IDS_KEY);
    }

    setSafetyPopulationAsY(): void {
        (<CheckListFilterItemModel>this.getModelByKey(PopulationFiltersModel.SAFETY_POPULATION_IDS_KEY)).selectedValues = ['Y'];
    }

    setAsPopulation(subjectIds: string[]): void {
        const subjectEcodes = this.datasetViews.getSubjectsEcodesByIds(subjectIds);
        this.itemsModels.forEach((itemModel) => {
            if (itemModel.key === PopulationFiltersModel.SUBJECT_IDS_KEY) {
                const listItemModel: any = itemModel;
                listItemModel.appliedSelectedValues = subjectEcodes;
                listItemModel.selectedValues = subjectEcodes;
                listItemModel.numberOfSelectedFilters = subjectEcodes.length;
            }
        });
        this.filterEventService.setPopulationFilterSubjectCount(subjectEcodes.length);
        this.getFilters(true);
    }

    isVisible(): boolean {
        return true;
    }

    addCohortFilter(subjectIds: string[], cohortName: string): void {
        const key = PopulationFiltersModel.COHORT_EDITOR_KEY + '--' + cohortName;
        const alreadyHasCohort = _.some(this.itemsModels, {key: key});
        if (!alreadyHasCohort) {
            const cohortFilter = new CohortFilterItemModel(key, cohortName);
            cohortFilter.selectedValues = subjectIds;
            this.itemsModels.push(cohortFilter);
        }
    }

    removeCohortFilter(cohortNames: string[], clearSubjectFilter: boolean): void {
        _.each(cohortNames, (cohortName) => {
            _.remove(this.itemsModels, (filter: BaseFilterItemModel) => {
                return filter.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1 && filter.displayName === cohortName;
            });
        });
        if (clearSubjectFilter) {
            const subjectIdFilter = _.find(this.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
            subjectIdFilter.clear();
            this.toggleSubjectIdFilterVisibility(true);
        }
    }

    transformFiltersToServer(manuallyApplied = false): any {

        const serverFiltersModel: any = {};
        const isCohortFilter: boolean = window.location.hash.indexOf('cohort-editor') !== -1;

        this.itemsModels.forEach((model: BaseFilterItemModel) => {
            const serverObject = model.toServerObject(manuallyApplied, isCohortFilter);
            if (!_.isEmpty(serverObject)) {
                serverFiltersModel[model.key] = serverObject;
            }
        });

        this.setSubjectsToSubjectsInCohorts(serverFiltersModel);

        return serverFiltersModel;
    }

    getCohortEditorFilters(): CohortFilterItemModel[] {
        return <CohortFilterItemModel[]> _.filter(this.itemsModels,
            (item) => item.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1);
    }

    getIntersectOfCohortSubjects(): string[] {
        const subjectsInEachCohort = _.map(this.getCohortEditorFilters(), 'selectedValues');
        return _.reduce(subjectsInEachCohort, (r: string[], a: string[]) => _.intersection(r, a));
    }

    getSubjectIdFilter(): ListFilterItemModel {
        return <ListFilterItemModel> _.find(this.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
    }

    renameCohortIfApplied(oldName: string, newName: string): void {
        const appliedCohort: BaseFilterItemModel = _.find(this.itemsModels, {
            key: PopulationFiltersModel.COHORT_EDITOR_KEY + '--' + oldName
        });

        if (!_.isEmpty(appliedCohort)) {
            appliedCohort.key = PopulationFiltersModel.COHORT_EDITOR_KEY + '--' + newName;
            appliedCohort.displayName = newName;
        }
    }

    hasCohortSelected(cohortName: string): boolean {
        return _.some(this.getCohortEditorFilters(), (cohort) => cohort.displayName === cohortName);
    }

    toggleSubjectIdFilterVisibility(visible: boolean): void {
        this.getSubjectIdFilter().filterIsVisible = visible;
    }

    protected _getFiltersImpl(manuallyApplied = false): void {
        const that = this;

        this.loading = true;
        this.hasInitData = true;

        if (this.pendingRequest) {
            this.pendingRequest.unsubscribe();
        }

        console.log('Sending ' + this.getName() + ' filters request');
        if (that.firstEventEmitted) {
            this.emitEvent(this.transformFiltersToServer(manuallyApplied));
        } else {
            that.firstEventEmitted = true;
        }

        this.pendingRequest = this.filterHttpService.getPopulationFiltersObservable(
            getServerPath(this.getModulePath(), 'filters'),
            this.transformFiltersToServer(manuallyApplied)
        ).subscribe(res => {
            console.log('Got ' + this.getName() + ' filters request');
            that.transformFiltersFromServer(res);
            if (that.firstTimeLoaded) {
                that.hideEmptyFilters(that.datasetViews.getEmptyFilters(that.getName()));
                that.firstTimeLoaded = false;
            }
            this.matchedItemsCount = <number>res.matchedItemsCount;
            that.filterEventService.setPopulationFilterSubjectCount(<number>res.matchedItemsCount);
            //Temporary solution. TODO: remove it after implementation of filters using store.
            this._store.dispatch(new UpdateAvailableSubjects(List<string>(res.subjectId.values)));
            that.loading = false;
        });
    }

    private setSubjectsToSubjectsInCohorts(serverFiltersModel: any): void {
        const cohortEditorFilters = this.getCohortEditorFilters();
        const subjectsIdFilter = this.getSubjectIdFilter();
        if (this.hasSubjectsInCohortFilters(cohortEditorFilters)) {
            const subjectsInCohorts = _.map(cohortEditorFilters, 'selectedValues');
            const intersectOfSubjects = _.reduce(subjectsInCohorts, (r: string[], a: string[]) => _.intersection(r, a));
            if (intersectOfSubjects.length === 0) {
                serverFiltersModel[PopulationFiltersModel.SUBJECT_IDS_KEY] = {
                    values: [PopulationFiltersModel.NO_INTERSECT_OF_SUBJECTS]
                };
            } else {
                subjectsIdFilter.selectedValues = intersectOfSubjects;
                serverFiltersModel[PopulationFiltersModel.SUBJECT_IDS_KEY] = subjectsIdFilter.toServerObject(true);
            }
        }
    }

    private hasSubjectsInCohortFilters(filters: CohortFilterItemModel[]): boolean {
        return _.chain(filters).flatMap('selectedValues').filter((val) => !_.isUndefined(val)).value().length > 0;
    }
}
