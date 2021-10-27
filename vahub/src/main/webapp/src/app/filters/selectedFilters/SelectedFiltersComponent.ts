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

import {Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Location} from '@angular/common';
import {NavigationEnd, Router} from '@angular/router';
import {Subject} from 'rxjs/Subject';

import {FilterEventService} from '../event/FilterEventService';
import {FilterId} from '../../common/trellising/store';
import {SelectedFiltersModel} from './SelectedFiltersModel';
import {TimelineTrackService} from '../../plugins/timeline/config/trackselection/TimelineTrackService';
import {TrackName} from '../../plugins/timeline/store/ITimeline';
import {StudySpecificFilterModel} from '../components/studySpecific/StudySpecificFilterModel';
import {DatasetViews} from '../../security/DatasetViews';
import {remove, isNull, find, isEmpty, map, reduce, every, intersection, isEqual, some} from 'lodash';
import {FiltersUtils} from '../utils/FiltersUtils';
import {BaseFilterItemModel} from '../components/BaseFilterItemModel';
import {FILTER_TYPE} from '../components/dtos';
import {AbstractFiltersModel} from '../dataTypes/AbstractFiltersModel';
import {PopulationFiltersModel} from '../dataTypes/population/PopulationFiltersModel';
import {Subscription} from 'rxjs/Subscription';
import {CohortFiltersModel} from '../dataTypes/cohort/CohortFiltersModel';

const COLLAPSED_HEIGHT = 48;

@Component({
    selector: 'selected-filters',
    templateUrl: 'SelectedFiltersComponent.html',
    styleUrls: ['./SelectedFiltersComponent.css']
})
export class SelectedFiltersComponent implements OnInit, OnDestroy {

    @Input()
    id: string;

    @ViewChild('populationWidgetsContainer') populationWidgetsContainer: ElementRef;
    @ViewChild('eventWidgetsContainer') eventWidgetsContainer: ElementRef;
    populationFiltersCollapsed: boolean;
    populationWidgetHeight: number;
    populationExpandCollapseEnabled: boolean;

    eventFiltersCollapsed: boolean;
    eventWidgetHeight: number;
    eventExpandCollapseEnabled: boolean;
    showNumberOfEvents: boolean;
    showEventFilterWidget: boolean;

    saveModalVisible: boolean;
    clearModalVisible: boolean;
    filterDetailsVisible: boolean;

    detailsOffset: any;
    eventType: string;

    currentSidePanelTab: string;

    private eventFilterSubscriptions: Subscription[] = [];

    private populationFilterSubscription: Subscription;
    private populationFilterCountSubscription: Subscription;
    private eventFilterCountSubscription: Subscription;

    private pageChangeListener: Subscription;
    private sidePanelTabListener: Subscription;
    private updateSelectedTrackskListener: Subscription;
    private subKey = 'SelectedFiltersComponent';

    constructor(private router: Router,
                private filterEventService: FilterEventService,
                public selectedFiltersModel: SelectedFiltersModel,
                private timelineTrackService: TimelineTrackService,
                private datasetViews: DatasetViews,
                protected location: Location,
                private filtersUtils: FiltersUtils) {
    }

    ngOnInit(): void {
        this.populationFiltersCollapsed = true;
        this.populationWidgetHeight = COLLAPSED_HEIGHT;
        this.populationExpandCollapseEnabled = false;

        this.eventFiltersCollapsed = true;
        this.eventWidgetHeight = COLLAPSED_HEIGHT;
        this.eventExpandCollapseEnabled = false;
        this.showNumberOfEvents = true;
        this.showEventFilterWidget = true;

        this.saveModalVisible = false;
        this.clearModalVisible = false;
        this.filterDetailsVisible = false;
        this.detailsOffset = {top: 0, left: 0};
        this.getTotalPopulationSize();
        this.listenToPageChanges();
        this.setEventFilterWidgetVisibility();
    }

    ngOnDestroy(): void {
        const that = this;
        this.unsubscribe(this.populationFilterCountSubscription);
        this.unsubscribe(this.eventFilterCountSubscription);

        this.unsubscribe(this.populationFilterSubscription);
        this.eventFilterSubscriptions.forEach((s) => {
            that.unsubscribe(s);
        });

        this.unsubscribe(this.pageChangeListener);
        this.unsubscribe(this.sidePanelTabListener);
        this.unsubscribe(this.updateSelectedTrackskListener);

        this.selectedFiltersModel.clearSelectedFilters();
    }

    getTotalPopulationSize(): void {
        this.selectedFiltersModel.totalNumberOfSubjects = this.datasetViews.getItemsCount('population', '');
        this.listenToPopulationFilterChanges();
    }

    setEventFilterWidgetVisibility(): void {
        const [eventType, tab] = this.getEventTypeFromUrl();

        if (this.isSpotfire()) {
            this.showNumberOfEvents = false;
            this.showEventFilterWidget = false;
        } else if (this.isOnCohortEditorPage(eventType)) {
            this.showNumberOfEvents = false; // Hide, because the cohort editor page can have lots of events types in the selected filter
            this.showEventFilterWidget = true;
            this.selectedFiltersModel.eventWidgetName = 'Events';
        } else if (tab
            && ((this.isNotOnTimelinePage(eventType) && this.isNotOnSingleSubjectPage(eventType) ) || this.isOnLungFunctionPage(tab))) {
            this.showNumberOfEvents = true;
            this.showEventFilterWidget = this.isNotOnPopulationPage(eventType)
                && this.isNotOnCohortEditorPage(eventType)
                && this.isNotOnExposureSpotfirePage(eventType, tab)
                && this.isNotOnTLDPage(tab)
                && this.isNotOnQtProlongationTab(tab);
            this.selectedFiltersModel.totalNumberOfEvents = this.datasetViews.getItemsCount(eventType, tab);
        } else {
            this.showNumberOfEvents = false;
            this.showEventFilterWidget = !tab || this.isNotOnSingleSubjectSummaryPage(tab);
            this.listenToTimelineFilterChanges();
        }
        this.listenToEventFilterChanges();
    }

    toggleFilterDetails(currentFilter, event, isEventFilter: boolean): void {
        this.filterDetailsVisible = true;
        this.selectedFiltersModel.consideredFilter = currentFilter;
        this.selectedFiltersModel.consideredFilter.isEventFilter = isEventFilter;

        const clientRect = event.currentTarget.getBoundingClientRect();
        this.detailsOffset.top = clientRect.top + clientRect.height;
        this.detailsOffset.left = clientRect.left;
    }

    closeDetails(): void {
        this.filterDetailsVisible = false;
    }

    removeFilterItem(filterToRemove: any, isPopulationWidgetClicked: boolean): void {
        const filtersModel: AbstractFiltersModel = this.filtersUtils.getFilterModelById(filterToRemove.filterId);
        if (isPopulationWidgetClicked) {
            remove(this.selectedFiltersModel.selectedPopulationFilters, (filter: any) => {
                return filter.key === filterToRemove.key && filterToRemove.displayName === filter.displayName;
            });
        } else {
            remove(this.selectedFiltersModel.selectedEventFilters, (filter: any) => {
                return filter.key === filterToRemove.key && filterToRemove.displayName === filter.displayName;
            });
        }
        if (!isNull(this.selectedFiltersModel.consideredFilter) && this.selectedFiltersModel.consideredFilter.key === filterToRemove.key) {
            this.filterDetailsVisible = false;
        }
        const filterItem: BaseFilterItemModel = find(filtersModel.itemsModels, (filter: BaseFilterItemModel) => {
            return filter.key === filterToRemove.key;
        });
        if (filterItem.type === FILTER_TYPE.STUDY_SPECIFIC_FILTERS) {
            filterItem.clear(filterToRemove.displayName);
            (<StudySpecificFilterModel>filterItem).updateNumberOfSelectedFilters();
        } else if (filterItem.type === FILTER_TYPE.COHORT_EDITOR) {
            (<PopulationFiltersModel> filtersModel).removeCohortFilter([filterToRemove.displayName], true);
            (<CohortFiltersModel> this.filtersUtils.getFilterModelById(FilterId.COHORT)).removeCohort(filterToRemove.displayName);
            if (!this.hasCohortFilters(filtersModel)) {
                const subjectIdFilter = find(filtersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
                subjectIdFilter.resetNumberOfSelectedFilters();
                (<any>subjectIdFilter).filterIsVisible = true;
            }
        } else {
            filterItem.clear();
            filterItem.resetNumberOfSelectedFilters();
        }
        filtersModel.getFilters(false, false, true);
        this.maybeUpdateWidgetState();
    }

    isSpotfire(): boolean {
        return this.location.path(true).indexOf('spotfire') !== -1;
    }

    togglePopulationFilters(): void {
        if (this.populationExpandCollapseEnabled) {
            if (this.populationFiltersCollapsed) {
                this.populationWidgetHeight = this.getHeight(this.populationWidgetsContainer, COLLAPSED_HEIGHT);
            } else {
                this.populationWidgetHeight = COLLAPSED_HEIGHT;
            }
            this.populationFiltersCollapsed = !this.populationFiltersCollapsed;
        }
    }

    toggleEventFilters(): void {
        if (this.eventExpandCollapseEnabled) {
            if (this.eventFiltersCollapsed) {
                this.eventWidgetHeight = this.getHeight(this.eventWidgetsContainer, COLLAPSED_HEIGHT);
            } else {
                this.eventWidgetHeight = COLLAPSED_HEIGHT;
            }
            this.eventFiltersCollapsed = !this.eventFiltersCollapsed;
        }
    }

    isRangeFilter(filterType: FILTER_TYPE): boolean {
        return filterType === FILTER_TYPE.RANGE || filterType === FILTER_TYPE.RANGE_DATE;
    }

    isListFilter(filterType: FILTER_TYPE): boolean {
        return filterType === FILTER_TYPE.LIST || filterType === FILTER_TYPE.CHECK_LIST || filterType === FILTER_TYPE.UNSELECTED_CHECK_LIST;
    }

    isStudySpecificFilter(filterType: FILTER_TYPE): boolean {
        return filterType === FILTER_TYPE.STUDY_SPECIFIC_FILTERS;
    }

    isCohortFilter(filterType: FILTER_TYPE): boolean {
        return filterType === FILTER_TYPE.COHORT_EDITOR;
    }

    displayValuesEqual(displayValues: any): boolean {
        if (displayValues.to && displayValues.from) {
            return isEqual(displayValues.from, displayValues.to);
        }
        return false;
    }

    private unsubscribe(subscription: Subscription): void {
        if (subscription) {
            subscription.unsubscribe();
        }
    }

    private getEventTypeFromUrl(): string[] {
        return this.location.path(true).split('plugins/')[1].split('/');
    }

    private getHeight(elementRef: ElementRef, fallback: number): number {
        try {
            return elementRef.nativeElement.clientHeight;
        } catch (e) {
            return fallback;
        }
    }

    private maybeUpdateWidgetState(): void {
        let populationFiltersCollapsed = this.populationFiltersCollapsed;
        let populationWidgetHeight = this.populationWidgetHeight;
        let populationExpandCollapseEnabled = this.populationExpandCollapseEnabled;

        if (this.populationWidgetsContainer) {
            const popWidgetsActualHeight = this.getHeight(this.populationWidgetsContainer, COLLAPSED_HEIGHT);
            if (COLLAPSED_HEIGHT >= popWidgetsActualHeight) {
                populationExpandCollapseEnabled = false;
                populationFiltersCollapsed = true;
                populationWidgetHeight = COLLAPSED_HEIGHT;
            } else {
                populationExpandCollapseEnabled = true;
                populationFiltersCollapsed = this.populationWidgetHeight <= COLLAPSED_HEIGHT;
                if (!populationFiltersCollapsed) {
                    populationWidgetHeight = popWidgetsActualHeight;
                }
            }
        }

        let eventFiltersCollapsed = this.eventFiltersCollapsed;
        let eventWidgetHeight = this.eventWidgetHeight;
        let eventExpandCollapseEnabled = this.eventExpandCollapseEnabled;
        if (this.eventWidgetsContainer) {
            const eventWidgetsActualHeight = this.getHeight(this.eventWidgetsContainer, COLLAPSED_HEIGHT);
            if (COLLAPSED_HEIGHT >= eventWidgetsActualHeight) {
                eventExpandCollapseEnabled = false;
                eventFiltersCollapsed = true;
                eventWidgetHeight = COLLAPSED_HEIGHT;
            } else {
                eventExpandCollapseEnabled = true;
                eventFiltersCollapsed = this.eventWidgetHeight <= COLLAPSED_HEIGHT;
                if (!eventFiltersCollapsed) {
                    eventWidgetHeight = eventWidgetsActualHeight;
                }
            }
        }

        if (populationFiltersCollapsed !== this.populationFiltersCollapsed ||
            populationWidgetHeight !== this.populationWidgetHeight ||
            populationExpandCollapseEnabled !== this.populationExpandCollapseEnabled ||
            eventFiltersCollapsed !== this.eventFiltersCollapsed ||
            eventWidgetHeight !== this.eventWidgetHeight ||
            eventExpandCollapseEnabled !== this.eventExpandCollapseEnabled) {
            this.populationFiltersCollapsed = populationFiltersCollapsed;
            this.populationWidgetHeight = populationWidgetHeight;
            this.populationExpandCollapseEnabled = populationExpandCollapseEnabled;

            this.eventFiltersCollapsed = eventFiltersCollapsed;
            this.eventWidgetHeight = eventWidgetHeight;
            this.eventExpandCollapseEnabled = eventExpandCollapseEnabled;
        }
    }

    private isNotOnTimelinePage(eventType: string): boolean {
        return eventType.indexOf('timeline') === -1;
    }

    private isOnTimelineOrSingleSubjectTimelinePage(eventType: string, tab: string): boolean {
        return !this.isNotOnTimelinePage(eventType) || (tab && tab.indexOf('timeline') !== -1);
    }

    private isNotOnSingleSubjectPage(eventType: string): boolean {
        return eventType.indexOf('singlesubject') === -1;
    }

    private isNotOnQtProlongationTab(tab: string): boolean {
        return tab && tab.indexOf('qt-prolongation') === -1;
    }

    private isOnLungFunctionPage(tab: string): boolean {
        return tab && tab.indexOf('lungfunction-tab') !== -1;
    }

    private isNotOnPopulationPage(eventType: string): boolean {
        return eventType.indexOf('population') === -1;
    }

    private isNotOnSingleSubjectSummaryPage(tab: string): boolean {
        return tab.indexOf('summary-tab') === -1;
    }

    private isOnCohortEditorPage(eventType: string): boolean {
        return !this.isNotOnCohortEditorPage(eventType);
    }

    private isNotOnCohortEditorPage(eventType: string): boolean {
        return eventType.indexOf('cohort-editor') === -1;
    }

    private isNotOnExposureSpotfirePage(eventType: string, tab: string): boolean {
        return eventType.indexOf('exposure') === -1 || tab.indexOf('spotfire') === -1;
    }

    private isNotOnTLDPage(tab: string): boolean {
        return tab.indexOf('target-lesion-diameters') === -1;
    }

    private listenToPageChanges(): void {
        this.pageChangeListener = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationEnd) {
                this.selectedFiltersModel.updateEventWidgetName();
                this.setEventFilterWidgetVisibility();
                this.closeDetails();
                // This is a hack to get the Number of Subjects working when navigating from the Cohort Editor page
                this.setNumberOfSubjectsToCohortSizes();
                this.maybeUpdateWidgetState();
            }
        });
        this.sidePanelTabListener = this.filterEventService.sidePanelTab.subscribe((tabName: any) => {
            this.currentSidePanelTab = tabName;
        });
    }

    private setNumberOfSubjectsToCohortSizes(): void {
        const selectedPopulationFilters = this.selectedFiltersModel.selectedPopulationFilters;
        const hasOnlyCohortFilters = !isEmpty(selectedPopulationFilters) && every(selectedPopulationFilters, (f: BaseFilterItemModel) => {
            return f.key.split('--')[0] === PopulationFiltersModel.COHORT_EDITOR_KEY;
        });
        if (hasOnlyCohortFilters) {
            const allSubjects = map(selectedPopulationFilters, 'displayValues');
            const numberOfSubjects = reduce(allSubjects, (r: string[], a: string[]) => intersection(r, a)).length;
            this.selectedFiltersModel.numberOfSubjects = numberOfSubjects + ' of ' + this.selectedFiltersModel.totalNumberOfSubjects;
        }
    }

    private listenToPopulationFilterChanges(): void {
        const that = this;

        this.populationFilterCountSubscription = this.filterEventService.populationFilterSubjectCount.subscribe((numberOfSubjects: any) => {
            const numberOfSubjectsString = numberOfSubjects + ' of ' + that.selectedFiltersModel.totalNumberOfSubjects;
            if (this.isOpeningCohortEditorPage(numberOfSubjects)) {
                // This is a hack to get the Number of Subjects working when navigating back to the Cohort Editor page
                that.selectedFiltersModel.numberOfSubjects = '(All) ' + that.selectedFiltersModel.totalNumberOfSubjects
                    + ' of ' + that.selectedFiltersModel.totalNumberOfSubjects;
            } else if (String(that.selectedFiltersModel.totalNumberOfSubjects) === String(numberOfSubjects)) {
                that.selectedFiltersModel.numberOfSubjects = '(All) ' + numberOfSubjectsString;
            } else {
                that.selectedFiltersModel.numberOfSubjects = numberOfSubjectsString;
            }
        });
        this.populationFilterSubscription = this.filterEventService.populationFilter.subscribe((filterChanged: any) => {
            if (!isNull(filterChanged)) {
                this.selectedFiltersModel.transformFilters(FilterId.POPULATION);
            }
            this.maybeUpdateWidgetState();
        });
        this.filterEventService.addSubscription(this.id + 'Population', this.populationFilterSubscription);
        this.filterEventService.addSubscription(this.id + 'PopulationCount', this.populationFilterCountSubscription);
    }

    private isOpeningCohortEditorPage(numberOfSubjects: number): boolean {
        // This is a hack to get the Number of Subjects working when navigating back to the Cohort Editor page
        const model = this.selectedFiltersModel;
        return isEmpty(model.selectedPopulationFilters) && String(model.totalNumberOfSubjects) !== String(numberOfSubjects);
    }

    private listenToEventFilterChanges(): void {
        this.eventFilterCountSubscription = this.filterEventService.eventFilterEventCount.subscribe((numberOfEvents: any) => {
            const numberOfEventsString = numberOfEvents + ' of ' + this.selectedFiltersModel.totalNumberOfEvents;
            if (String(this.selectedFiltersModel.totalNumberOfEvents) === String(numberOfEvents)) {
                this.selectedFiltersModel.numberOfEvents = '(All) ' + numberOfEventsString;
            } else {
                this.selectedFiltersModel.numberOfEvents = numberOfEventsString;
            }
        });
        this.filterEventService.addSubscription(this.subKey + 'Events', this.eventFilterCountSubscription);

        [
            [this.filterEventService.aesFilter, FilterId.AES],
            [this.filterEventService.seriousAesFilter, FilterId.SAE],
            [this.filterEventService.cardiacFilter, FilterId.CARDIAC],
            [this.filterEventService.conmedsFilter, FilterId.CONMEDS],
            [this.filterEventService.doseFilter, FilterId.DOSE],
            [this.filterEventService.labsFilter, FilterId.LAB],
            [this.filterEventService.liverFilter, FilterId.LIVER],
            [this.filterEventService.tumourResponseFilter, FilterId.TUMOUR_RESPONSE],
            [this.filterEventService.lungFunctionFilter, FilterId.LUNG_FUNCTION],
            [this.filterEventService.exacerbationsFilter, FilterId.EXACERBATIONS],
            [this.filterEventService.renalFilter, FilterId.RENAL],
            [this.filterEventService.vitalsFilter, FilterId.VITALS],
            [this.filterEventService.patientDataFilter, FilterId.PATIENT_REPORTED_DATA],
            [this.filterEventService.deathFilter, FilterId.DEATH],
            [this.filterEventService.doseDiscontinuationFilter, FilterId.DOSE_DISCONTINUATION],
            [this.filterEventService.medicalHistoryFilter, FilterId.MEDICAL_HISTORY],
            [this.filterEventService.liverDiagnosticInvestigationFilter, FilterId.LIVER_DIAGNOSTIC_INVESTIGATION],
            [this.filterEventService.liverRiskFactorsFilter, FilterId.LIVER_RISK_FACTORS],
            [this.filterEventService.alcoholFilter, FilterId.ALCOHOL],
            [this.filterEventService.nicotineFilter, FilterId.NICOTINE],
            [this.filterEventService.surgicalHistoryFilter, FilterId.SURGICAL_HISTORY],
            [this.filterEventService.cerebrovascularFilter, FilterId.CEREBROVASCULAR],
            [this.filterEventService.cieventsFilter, FilterId.CIEVENTS],
            [this.filterEventService.biomarkersFilter, FilterId.BIOMARKERS],
            [this.filterEventService.cvotFilter, FilterId.CVOT],
            [this.filterEventService.exposureFilter, FilterId.EXPOSURE],
            [this.filterEventService.doseProportionalityFilter, FilterId.DOSE_PROPORTIONALITY],
            [this.filterEventService.pkResultOverallResponseFilter, FilterId.PK_RESULT_OVERALL_RESPONSE],
            [this.filterEventService.ctDnaFilter, FilterId.CTDNA],
            [this.filterEventService.recistFilter, FilterId.RECIST]
        ].forEach((filter) => {
            this.eventFilterSubscriptions.push(this.filterSubscriptionFactory(<Subject<any>> filter[0], filter[1]));
        });
    }

    private filterSubscriptionFactory(filterSubject: Subject<any>, filterId: FilterId): Subscription {
        const subscription = filterSubject.subscribe((filterChanged) => {
            this.selectedFiltersModel.transformFilters(filterId);
            this.maybeUpdateWidgetState();
        });
        this.filterEventService.addSubscription(this.subKey + filterId, subscription);
        return subscription;
    }

    private listenToTimelineFilterChanges(): void {
        const that = this;
        const [eventType, tab] = this.getEventTypeFromUrl();
        if (this.isOnTimelineOrSingleSubjectTimelinePage(eventType, tab)) {
            this.updateSelectedTrackskListener = this.timelineTrackService.updatedSelectedTracks.subscribe((tracks: TrackName[]) => {
                if (!isEqual(that.selectedFiltersModel.timelineSelectedTracks, tracks)) {
                    that.selectedFiltersModel.timelineSelectedTracks = tracks;
                    this.selectedFiltersModel.transformFilters(null);
                }
                this.maybeUpdateWidgetState();
            });
            this.timelineTrackService.addSubscription(this.subKey + 'TrackNames', this.updateSelectedTrackskListener);
        }
    }

    private hasCohortFilters(filtersModel: AbstractFiltersModel): boolean {
        return some(filtersModel.itemsModels, (item) => item.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1);
    }
}
