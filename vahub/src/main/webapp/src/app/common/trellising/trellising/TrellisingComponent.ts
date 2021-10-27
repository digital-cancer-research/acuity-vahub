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

import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {Subject} from 'rxjs/Subject';
import {FilterEventService} from '../../../filters/module';
import {Trellising} from '../store/Trellising';
import {FilterId, TabId, TrellisDesign} from '../store';
import {TrellisingObservables} from '../store/observable/TrellisingObservables';
import {TrellisingDispatcher} from '../store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../../plugins/timeline/store/dispatcher/TimelineDispatcher';
import {TabStoreUtils} from '../store/utils/TabStoreUtils';
import {AppStoreUtils} from '../../../plugins/timeline/store/AppStoreUtils';
import {SingleSubjectModel} from '../../../plugins/refactored-singlesubject/SingleSubjectModel';
import {TimelineId} from '../../../plugins/timeline/store/ITimeline';
import {ConfigurationService} from '../../../configuration/ConfigurationService';
import {DatasetViews} from '../../../security/DatasetViews';
import {EXPLANATION_MAP} from '../../utils/ExplanationUtils';

@Component({
    selector: 'trellising-component',
    templateUrl: 'TrellisingComponent.html',
    styleUrls: ['./style.css']
})

export class TrellisingComponent implements OnInit, OnDestroy {
    private eventsCountSubscription: Subscription;
    private populationCountSubscription: Subscription;
    private populationFilterSubscription: Subscription;
    private singleSubjectChangeSubscription: Subscription;
    private fractionalHeight = 0.65;
    private lastFilter: FilterId;
    public staticHeight: number;
    public isBoxPlot: boolean;
    private subscriptions: Subscription[] = [];
    cBioLink: string;
    hasAxis: boolean;
    explanationMap = EXPLANATION_MAP;
    @Input() tabId: TabId;
    @Input() isSubPlot: boolean;

    constructor(protected filterEventService: FilterEventService,
                protected configurationService: ConfigurationService,
                protected singleSubjectModel: SingleSubjectModel,
                public trellising: Trellising,
                public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher,
                private timelineDispatcher: TimelineDispatcher,
                private datasetViews: DatasetViews) {
    }

    ngOnInit(): void {
        this.trellising.setTabId(this.tabId);
        this.trellisingObservables.setTabId(this.tabId);
        this.trellisingDispatcher.setTabId(this.tabId);
        this.setFractionalHeight();
        this.init();
        this.isBoxPlot = TabStoreUtils.isBoxPlotTab(this.tabId);
        // Selectors
        this.hasAxis = this.trellisingObservables.getTrellisDesign(this.tabId) !== TrellisDesign.NO_AXIS;

    }

    ngOnDestroy(): void {
        const parentPlotId = this.trellisingObservables.getParentPlotId(this.tabId);
        if (parentPlotId) {
            this.trellising.setTabId(parentPlotId);
        } else {
            this.subscriptions.forEach(x => x.unsubscribe());
            this.subscriptions = [];
        }
    }

    onResize(event: any): void {
        if (this.trellisingDispatcher) {
            this.trellisingDispatcher.updateHeight(this.calculateNewHeightByOldHeight(event.target.innerHeight));
        }
    }

    private isSingleSubjectView(): boolean {
        return window.location.hash.indexOf('singlesubject') !== -1;
    }

    private init(): void {
        if (!this.isSingleSubjectView()) {
            this.trellising.init();
        }
        this.trellisingDispatcher.updateHeight(this.calculateNewHeightByOldHeight(window.innerHeight));
        this.listenToEventCountChange();
        this.listenToOtherFilterChange();
        this.listenToHeight();
        this.listenToConfigurationService();
    }

    private listenToHeight(): void {
        const that = this;
        this.trellisingObservables.height.subscribe((height) => {
            that.staticHeight = height * that.fractionalHeight + 50;
        });
    }

    private listenToConfigurationService(): void {
        const integration = this.configurationService.cbioportalUrl;
        if (integration && this.datasetViews.checkCBioLinkEnabled()) {
            this.cBioLink = integration;
        } else {
            this.cBioLink = '';
        }
    }

    private listenToEventCountChange(): void {
        this.eventsCountSubscription = this.filterEventService.eventFilterEventCount.subscribe((eventCount: number) => {
            if (eventCount === 0) {
                this.trellising.setNoData(this.lastFilter);
            }
        });
        this.filterEventService.addSubscription(this.tabId.toString() + 'eventCounts', this.eventsCountSubscription);
        this.subscriptions.push(this.eventsCountSubscription);

        if (this.tabId === TabId.POPULATION_BARCHART) {
            this.populationCountSubscription = this.filterEventService.populationFilterSubjectCount.subscribe((populationCount: number) => {
                if (populationCount === 0) {
                    this.trellising.setNoData(this.lastFilter);
                }
            });
            this.filterEventService.addSubscription(this.tabId.toString() + 'populationCounts', this.populationCountSubscription);
            this.subscriptions.push(this.populationCountSubscription);
        }
    }

    private listenToOtherFilterChange(): void {
        this.populationFilterSubscription = this.filterEventService.populationFilter.subscribe((changed) => {
            if (this.tabId === TabId.POPULATION_BARCHART) {
                this.onFilter(FilterId.POPULATION, changed);
                this.timelineDispatcher.globalResetNotification();
                this.trellisingDispatcher.globalResetNotification();
            } else if (!this.isSubPlot) {
                // if there is no event filter on the tab then trigger update cause no other event would appear
                if (!this.trellisingObservables.getHasEventFilters()) {
                    this.trellisingDispatcher.clearSubPlot(this.tabId);
                    this.onFilter(FilterId.POPULATION, changed);
                }
                // in case filter has changed then reset all views, cause they all rely on pop filter
                if (changed) {
                    this.timelineDispatcher.localResetNotification(TimelineId.COMPARE_SUBJECTS);
                    this.trellisingDispatcher.globalResetNotification();
                }
            }
        });
        this.addSubscription(FilterId.POPULATION, this.populationFilterSubscription);
        this.subscriptions.push(this.populationFilterSubscription);
        if (this.isSingleSubjectView()) {
            this.listenToSubjectChanges();
        }
        [
            [this.filterEventService.aesFilter, FilterId.AES],
            [this.filterEventService.cerebrovascularFilter, FilterId.CEREBROVASCULAR],
            [this.filterEventService.cardiacFilter, FilterId.CARDIAC],
            [this.filterEventService.conmedsFilter, FilterId.CONMEDS],
            [this.filterEventService.labsFilter, FilterId.LAB],
            [this.filterEventService.liverFilter, FilterId.LIVER],
            [this.filterEventService.tumourResponseFilter, FilterId.TUMOUR_RESPONSE],
            [this.filterEventService.lungFunctionFilter, FilterId.LUNG_FUNCTION],
            [this.filterEventService.exacerbationsFilter, FilterId.EXACERBATIONS],
            [this.filterEventService.renalFilter, FilterId.RENAL],
            [this.filterEventService.vitalsFilter, FilterId.VITALS],
            [this.filterEventService.patientDataFilter, FilterId.PATIENT_REPORTED_DATA],
            [this.filterEventService.recistFilter, FilterId.RECIST],
            [this.filterEventService.cieventsFilter, FilterId.CIEVENTS],
            [this.filterEventService.cvotFilter, FilterId.CVOT],
            [this.filterEventService.biomarkersFilter, FilterId.BIOMARKERS],
            [this.filterEventService.exposureFilter, FilterId.EXPOSURE],
            [this.filterEventService.doseProportionalityFilter, FilterId.DOSE_PROPORTIONALITY],
            [this.filterEventService.pkResultOverallResponseFilter, FilterId.PK_RESULT_OVERALL_RESPONSE],
            [this.filterEventService.ctDnaFilter, FilterId.CTDNA]
        ].forEach((filter) => {
            this.subscriptions.push(this.filterSubscriptionFactory(<Subject<any>> filter[0], filter[1]));
        });

    }

    private filterSubscriptionFactory(filterSubject: Subject<any>, filterId: any): Subscription {
        const subscription = filterSubject.subscribe((filterChanged) => {
            this.onFilter(filterId, filterChanged);
            if (AppStoreUtils.isTimelineRelatedFilter(filterId)) {
                this.timelineDispatcher.globalResetNotification();
            }
        });
        this.filterEventService.addSubscription(filterId, subscription);
        return subscription;
    }

    private addSubscription(filterId: FilterId, subscription: Subscription): void {
        const key: string = this.tabId.toString() + filterId.toString();
        this.filterEventService.addSubscription(key, subscription);
        this.subscriptions.push(subscription);
    }

    private onFilter(filterId: FilterId, changed: any): void {
        if (changed || filterId === FilterId.SINGLE_SUBJECT) {
            this.lastFilter = filterId;
            this.trellisingDispatcher.localResetNotification(filterId);
            this.trellising.reset();
        }
    }

    private setFractionalHeight(): void {
        if (window.screen.availHeight >= 820) {
            this.fractionalHeight = 0.55;
        } else {
            if (window.screen.availHeight >= 800) {
                this.fractionalHeight = 0.50;
            } else {
                if (window.screen.availHeight >= 680) {
                    this.fractionalHeight = 0.45;
                } else {
                    this.fractionalHeight = 0.45;
                }
            }
        }
        if (this.tabId === TabId.SINGLE_SUBJECT_LIVER_HYSLAW) {
            this.fractionalHeight *= 0.9;
        }
        // may be it should be reduced for chord if dod table appears there
        if (this.tabId === TabId.BIOMARKERS_HEATMAP_PLOT
            || this.tabId === TabId.AES_CHORD_DIAGRAM) {
            if (window.screen.availHeight <= 1050) {
                this.fractionalHeight = 0.6;
            } else {
                this.fractionalHeight = 0.65;
            }
        }
    }

    private listenToSubjectChanges(): void {
        this.singleSubjectChangeSubscription = this.singleSubjectModel.chosenSubject.subscribe((changed) => {
            if (changed) {
                this.onFilter(FilterId.SINGLE_SUBJECT, changed);
            }
        });
        this.addSubscription(FilterId.SINGLE_SUBJECT, this.singleSubjectChangeSubscription);
        this.subscriptions.push(this.singleSubjectChangeSubscription);
    }

    private calculateNewHeightByOldHeight(oldHeight: number): number {
        const additionalHeight = window.location.hash.indexOf('population-summary') > -1 ? 50 : 0;
        return oldHeight * this.fractionalHeight + additionalHeight;
    }
}
