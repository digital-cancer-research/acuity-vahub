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
import {Observable} from 'rxjs/Observable';
import {Subscription} from 'rxjs/Subscription';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Action, Store} from '@ngrx/store';
import {fromJS, List} from 'immutable';
import 'rxjs/add/operator/take';
import 'rxjs/add/operator/filter';
import {isEmpty} from 'lodash';

import {
    APPLY_ZOOM,
    CHANGE_DAY_ZERO,
    CHANGE_ECG_WARNINGS,
    CHANGE_ECG_YAXIS_VALUE,
    CHANGE_LABS_YAXIS_VALUE,
    CHANGE_PAGE,
    CHANGE_POPULATION_FILTER,
    CHANGE_SPIROMETRY_YAXIS_VALUE,
    CHANGE_TRACKS,
    CHANGE_VITALS_YAXIS_VALUE,
    COLLAPSE_TRACK,
    EXPAND_TRACK,
    INITIALISE_TIMELINE,
    RELOAD_DATA,
    SAVE_TRACK_INITIAL_OPENING_STATE
} from './TimelineAction';
import {
    AppStore,
    DayZero,
    EcgWarnings,
    EcgYAxisValue,
    ISubject,
    ITimelinePage,
    ITrack,
    IZoom,
    LabsYAxisValue,
    PageRecord,
    SpirometryYAxisValue, TimelineId,
    VitalsYAxisValue
} from './ITimeline';
import {TrackDataService} from './../http/TrackDataService';
import {ITrackExpansion} from '../menu/ITimelineContextMenuItem';
import {FilterEventService} from '../../../filters/event/FilterEventService';
import {DynamicAxis, FilterId} from '../../../common/trellising/store/ITrellising';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {AppStoreUtils} from './AppStoreUtils';
import {TimelineActionCreator} from './actions/TimelineActionCreator';
import {TimelineDispatcher} from './dispatcher/TimelineDispatcher';
import {TimelineObservables} from './observable/TimelineObservables';
import {ActionWithPayload} from '../../../common/trellising/store/actions/TrellisingActionCreator';
import {ApplicationState} from '../../../common/store/models/ApplicationState';

@Injectable()
export class Timeline<T extends TrackDataService> {
    private static DEFAULT_PAGINATION_LIMIT = 20;
    private static DEFAULT_PAGINATION_OFFSET = 0;
    loading: Observable<boolean>;
    showPagination: Observable<boolean>;
    subjects: Observable<List<ISubject>>;
    dayZero: Observable<DynamicAxis>;
    dayZeroOptions: Observable<DynamicAxis[]>;
    zoom: Observable<IZoom>;
    cursorXCoordinate = 0;
    page: Observable<ITimelinePage>;
    totalNumberOfSubjects: Observable<number>;
    pageSize = Timeline.DEFAULT_PAGINATION_LIMIT;
    tracks: Observable<List<ITrack>>;
    labsYAxisValue: Observable<LabsYAxisValue>;
    spirometryYAxisValue: Observable<SpirometryYAxisValue>;
    ecgYAxisValue: Observable<EcgYAxisValue>;
    ecgWarnings: Observable<EcgWarnings>;
    vitalsYAxisValue: Observable<VitalsYAxisValue>;
    plotBands: Observable<any[]>;
    private timelineActionCreator: TimelineActionCreator;
    private actions$ = new BehaviorSubject<ActionWithPayload<any>>({type: null, payload: null});
    private filterListeners: Array<Subscription> = [];
    private isSSV: boolean;

    constructor(private timelineId: string,
                private _store: Store<ApplicationState>,
                private dataService: T,
                private filterEventService: FilterEventService,
                private trellisingDispatcher: TrellisingDispatcher,
                private timelineDispatcher: TimelineDispatcher,
                private timelineObservables: TimelineObservables) {

        const timelineStore: Observable<AppStore> = _store.select('timelineReducer');
        this.timelineActionCreator = new TimelineActionCreator(timelineId);
        this.timelineDispatcher.setTimelineId(timelineId);
        this.timelineObservables.setTimelineId(timelineId);

        this.loading = timelineStore.map(data => data.getIn(['timelines', timelineId, 'loading']));
        this.showPagination = timelineStore.map(data => data.getIn(['timelines', timelineId, 'showPagination']));
        this.dayZero = timelineStore.map(data => data.getIn(['timelines', timelineId, 'dayZero']));
        this.dayZeroOptions = timelineStore.map(data => data.getIn(['timelines', timelineId, 'dayZeroOptions']));
        this.subjects = timelineStore.map(data => {
            return data.getIn(['timelines', timelineId, 'displayedSubjects'])
                .reduce((currentSubjects, subjectId) => {
                    currentSubjects = currentSubjects.push(data.getIn(['timelines', timelineId, 'subjects', subjectId]));
                    return currentSubjects;
                }, List<ISubject>());
        });
        this.zoom = timelineStore.map(data => data.getIn(['timelines', timelineId, 'zoom']));
        this.page = timelineStore.map(data => data.getIn(['timelines', timelineId, 'page']));
        this.totalNumberOfSubjects = timelineStore.map(data => data.getIn(['timelines', timelineId, 'subjects']).size);
        this.tracks = timelineStore.map(data => data.getIn(['timelines', timelineId, 'tracks']));
        this.labsYAxisValue = timelineStore.map(data => data.getIn(['timelines', timelineId, 'labsYAxisValue']));
        this.spirometryYAxisValue = timelineStore.map(data => data.getIn(['timelines', timelineId, 'spirometryYAxisValue']));
        this.ecgYAxisValue = timelineStore.map(data => data.getIn(['timelines', timelineId, 'ecgYAxisValue']));
        this.ecgWarnings = timelineStore.map(data => data.getIn(['timelines', timelineId, 'ecgWarnings']));
        this.vitalsYAxisValue = timelineStore.map(data => data.getIn(['timelines', timelineId, 'vitalsYAxisValue']));
        this.plotBands = timelineStore.map(data => data.getIn(['timelines', timelineId, 'plotBands']));
        this.isSSV = timelineId === TimelineId.SUBJECT_PROFILE;

        this.defineActions();
        this.listenToFilterChanges();
    }

    private defineActions(): void {
        const initTimeline = this.initTimelineAction();
        const updatePage = this.updatePageAction();
        const changeTracks = this.changeTracksAction();
        const expandOrCollapseTrack = this.expandOrCollapseTrackAction();
        const changePopulationFilter = this.populationChangeAction();
        const dayZeroChange = this.dayZeroChangeAction();
        const reloadPage = this.reloadPageAction();
        const updateYAxisValueAction = this.updateYAxisValueAction();

        Observable
            .merge(initTimeline,
                updatePage,
                changeTracks,
                expandOrCollapseTrack,
                changePopulationFilter,
                dayZeroChange,
                reloadPage,
                updateYAxisValueAction)
            .subscribe((action: Action) => {
                if (action) {
                    this._store.dispatch(action);
                }
            });
    }

    private updatePageAction(): Observable<Action> {

        return this.actions$
            .filter(action => action.type === CHANGE_PAGE)
            .do(action => this.timelineDispatcher.updateLoadingState(true))
            .do(action => this._store.dispatch(action))
            .switchMap(() => {
                return this.getTrackData().map((payload) => {
                    this.updateTracksData(payload);
                    return this.timelineActionCreator.makeLoadingAction(false);
                });
            });
    }

    /*take updated_track action and sort through if it needs to be hidden or shown here */

    private changeTracksAction(): Observable<Action> {

        return this.actions$
            .filter(action => action.type === CHANGE_TRACKS)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .do(action => this._store.dispatch(action))
            .switchMap((action) => {
                return this.getPossibleSubjects().map((subjectIds: string[]) => {
                    const currentPage = this.timelineObservables.getCurrentPage();
                    this.timelineDispatcher.updatePossibleSubjects(subjectIds);
                    this.updatePage(<ITimelinePage>new PageRecord({
                        limit: (currentPage && currentPage.limit) ? currentPage.limit : Timeline.DEFAULT_PAGINATION_LIMIT,
                        offset: Timeline.DEFAULT_PAGINATION_OFFSET
                    }));
                    return action;
                });
            })
            .switchMap((action) => {
                return this.getTrackData().map((payload: ISubject[]) => {
                    this.updateTracksData(payload);
                    return this.timelineActionCreator.makeLoadingAction(false);
                });
            });
    }

    private expandOrCollapseTrackAction(): Observable<Action> {

        return this.actions$
            .filter(action => action.type === COLLAPSE_TRACK || action.type === EXPAND_TRACK)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .do(action => this._store.dispatch(action))
            .switchMap(action => {
                let selectedSubjects: ISubject[] = [];
                if (action.payload.expansion.subjectId) {
                    const selectedSubject: ISubject = this.timelineObservables.getSubjectById(action.payload.expansion.subjectId);
                    selectedSubjects.push(selectedSubject);
                } else {
                    selectedSubjects = this.timelineObservables.getCurrentDisplayedSubjects();
                    // TODO mark expand/collapsed track with 'changed': true and other tracks with false
                }

                const dayZero: DayZero = this.timelineObservables.getCurrentDayZero();
                return this.dataService.getTrackData(selectedSubjects, dayZero)
                    .map((payload: ISubject[]) => {
                        this.updateTracksData(payload);
                        return this.timelineActionCreator.makeLoadingAction(false);
                    });
            });
    }

    private populationChangeAction(): Observable<Action> {

        return this.actions$
            .filter(action => action.type === CHANGE_POPULATION_FILTER)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .switchMap(action => {
                return this.getPossibleSubjects().map((subjectIds: string[]) => {
                    const currentPage = this.timelineObservables.getCurrentPage();
                    this.timelineDispatcher.updatePossibleSubjects(subjectIds);
                    this.updatePage(<ITimelinePage>new PageRecord({
                        limit: (currentPage && currentPage.limit) ? currentPage.limit : Timeline.DEFAULT_PAGINATION_LIMIT,
                        offset: Timeline.DEFAULT_PAGINATION_OFFSET
                    }));
                    return action;
                });
            })
            .switchMap(action => {
                return this.getTrackData().map((payload: ISubject[]) => {
                    this.updateTracksData(payload);
                    return this.timelineActionCreator.makeLoadingAction(false);
                });
            });
    }

    private dayZeroChangeAction(): Observable<Action> {

        return this.actions$
            .filter(action => action.type === CHANGE_DAY_ZERO)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .do(action => this._store.dispatch(action))
            .switchMap(action => {
                return this.getPossibleSubjects().map((subjectIds: string[]) => {
                    const currentPage = this.timelineObservables.getCurrentPage();
                    this.timelineDispatcher.updatePossibleSubjects(subjectIds);
                    this.updatePage(<ITimelinePage>new PageRecord({
                        limit: (currentPage && currentPage.limit) ? currentPage.limit : Timeline.DEFAULT_PAGINATION_LIMIT,
                        offset: Timeline.DEFAULT_PAGINATION_OFFSET
                    }));
                    return action;
                });
            })
            .switchMap(action => {
                return this.getTrackData().map((payload: ISubject[]) => {
                    this.updateTracksData(payload);
                    return this.timelineActionCreator.makeLoadingAction(false);
                });
            });
    }

    private reloadPageAction(): Observable<Action> {
        return this.actions$
            .filter(action => action.type === RELOAD_DATA)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .mergeMap(this.getPossibleSubjects.bind(this),
                (action, payload: ISubject[]): Action => {
                    this.updateTracksData(payload);
                    return this.timelineActionCreator.makeLoadingAction(false);
                });
    }

    private updateYAxisValueAction(): Observable<Action> {

        return this.actions$
            .filter(action =>
                action.type === CHANGE_LABS_YAXIS_VALUE
                || action.type === CHANGE_SPIROMETRY_YAXIS_VALUE
                || action.type === CHANGE_ECG_YAXIS_VALUE
                || action.type === CHANGE_VITALS_YAXIS_VALUE
                || action.type === CHANGE_ECG_WARNINGS)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .do(action => {
                this._store.dispatch(action);
                setTimeout(() => {
                    this._store.dispatch(this.timelineActionCreator.makeLoadingAction(false));
                }, 1000);
            });
    }

    private initTimelineAction(): Observable<Action> {
        return this.actions$
            .filter(action => action.type === INITIALISE_TIMELINE)
            .do(action => this._store.dispatch(this.timelineActionCreator.makeLoadingAction(true)))
            .switchMap(action => {
                return this.dataService.fetchDayZeroOptions().map((options: any) => {
                    this.timelineDispatcher.setDayZeroOptions(options);
                    return action;
                });
            })
            .switchMap(action => {
                return this.dataService.getPossibleTracks().map((tracks: ITrack[]) => {
                    this.timelineDispatcher.updatePossibleTracks(tracks);
                    // apply initial opening state
                    this.timelineDispatcher.applyInitialOpeningState();
                    return action;
                });
            })
            .switchMap(action => {
                return this.getPossibleSubjects().map((subjectIds: string[]) => {
                    this.timelineDispatcher.updatePossibleSubjects(subjectIds);
                    this.updatePage(<ITimelinePage>new PageRecord({
                        limit: Timeline.DEFAULT_PAGINATION_LIMIT,
                        offset: Timeline.DEFAULT_PAGINATION_OFFSET
                    }));
                    return action;
                });
            })
            .switchMap(action => {
                return this.getTrackData().map((payload: ISubject[]) => {
                    this.updateTracksData(payload);
                    this.timelineDispatcher.updateLocalIsInitialized(true);
                    return this.timelineActionCreator.makeLoadingAction(false);
                });
            });
    }

    init(): void {
        if (!this.timelineObservables.getIsInitialized()) {
            this.actions$.next({type: INITIALISE_TIMELINE, payload: {timelineId: this.timelineId}});
        }
    }

    destroy(): void {
        this.unsubscribeFromFilters();
        this._store.dispatch({type: SAVE_TRACK_INITIAL_OPENING_STATE, payload: {timelineId: this.timelineId}});
    }

    private updatePage(page: ITimelinePage): void {
        this._store.dispatch(this.timelineActionCreator.makeChangePageAction(page));
    }

    updatePageContent(page: ITimelinePage): void {
        if (this.haveChangedNumberOfSubectsPerPage(page)) {
            this.pageSize = page.limit;
        }
        this.actions$.next(this.timelineActionCreator.makeChangePageAction(page));
    }

    updateZoom(zoom?: IZoom): void {
        this._store.dispatch({type: APPLY_ZOOM, payload: {timelineId: this.timelineId, zoom: zoom}});
    }

    updateTrackSelection(allTracks: { [trackName: string]: { track: ITrack, selected: boolean, changed: boolean } }): void {
        this.actions$.next({
            type: CHANGE_TRACKS,
            payload: {timelineId: this.timelineId, tracks: allTracks}

        });
    }

    expandOrCollapseTrack(expansion: ITrackExpansion): void {
        if (expansion.expand) {
            this.actions$.next({
                type: EXPAND_TRACK,
                payload: {timelineId: this.timelineId, expansion: expansion}
            });
        } else {
            this.actions$.next({
                type: COLLAPSE_TRACK,
                payload: {timelineId: this.timelineId, expansion: expansion}
            });
        }
    }

    updatePopulation(): void {
        this.actions$.next({type: CHANGE_POPULATION_FILTER, payload: {}});
    }

    updateDayZero(dayZero: DayZero): void {
        this.actions$.next({
            type: CHANGE_DAY_ZERO,
            payload: {timelineId: this.timelineId, dayZero: dayZero}
        });
    }

    updateLabsYAxisValue(yAxisValue: LabsYAxisValue): void {
        this.actions$.next({
            type: CHANGE_LABS_YAXIS_VALUE,
            payload: {timelineId: this.timelineId, labsYAxisValue: yAxisValue}
        });
    }

    updateSpirometryYAxisValue(yAxisValue: SpirometryYAxisValue): void {
        this.actions$.next({
            type: CHANGE_SPIROMETRY_YAXIS_VALUE,
            payload: {timelineId: this.timelineId, spirometryYAxisValue: yAxisValue}
        });
    }

    updateEcgYAxisValue(yAxisValue: EcgYAxisValue): void {
        this.actions$.next({
            type: CHANGE_ECG_YAXIS_VALUE,
            payload: {timelineId: this.timelineId, ecgYAxisValue: yAxisValue}
        });
    }

    updateEcgWarnings(ecgWarnings: EcgWarnings): void {
        this.actions$.next({
            type: CHANGE_ECG_WARNINGS,
            payload: {timelineId: this.timelineId, ecgWarnings: ecgWarnings}
        });
    }

    updateAvailableEcgWarnings(payload: ISubject[]): void {
        const currentWarnings = this.timelineObservables.getCurrentEcgWarnings().toJS();
        const ecgWwarnings = AppStoreUtils.getAvailableEcgWarnings(payload, currentWarnings);
        this.updateEcgWarnings(fromJS(ecgWwarnings));
    }

    updateVitalsYAxisValue(yAxisValue: VitalsYAxisValue): void {
        this.actions$.next({
            type: CHANGE_VITALS_YAXIS_VALUE,
            payload: {timelineId: this.timelineId, vitalsYAxisValue: yAxisValue}
        });
    }

    private listenToFilterChanges(): void {
        const popFilter = this.filterEventService.populationFilter.subscribe((popChanged: any) => {
            if (popChanged) {
                this.timelineDispatcher.updateOthersIsInitialized(false);
                if (!this.timelineObservables.getPerformedJumpToTimelineFlag()) {
                    if (!this.isSSV) {
                        this.updatePopulation();
                    }
                } else {
                    this.timelineDispatcher.updatePerformedJumpToTimelineFlag(false);
                }
                this.trellisingDispatcher.localResetNotification(FilterId.POPULATION);
            }
        });
        const aesFilter = this.filterEventService.aesFilter.subscribe((aesChanged: any) => {
            this.updateAfterEventFilter(aesChanged, FilterId.AES);
        });
        const doseFilter = this.filterEventService.doseFilter.subscribe((doseChanged: any) => {
            this.updateAfterEventFilter(doseChanged, FilterId.DOSE);
        });
        const labsFilter = this.filterEventService.labsFilter.subscribe((labsChanged: any) => {
            this.updateAfterEventFilter(labsChanged, FilterId.LAB);
        });
        const spirometryFilter = this.filterEventService.lungFunctionFilter.subscribe((lungFunctionChanged: any) => {
            this.updateAfterEventFilter(lungFunctionChanged, FilterId.LUNG_FUNCTION);
        });
        const conmedsFilter = this.filterEventService.conmedsFilter.subscribe((conmedsChanged: any) => {
            this.updateAfterEventFilter(conmedsChanged, FilterId.CONMEDS);
        });
        const vitalsFilter = this.filterEventService.vitalsFilter.subscribe((vitalsChanged: any) => {
            this.updateAfterEventFilter(vitalsChanged, FilterId.VITALS);
        });
        const patientDataFilter = this.filterEventService.patientDataFilter.subscribe((vitalsChanged: any) => {
            this.updateAfterEventFilter(vitalsChanged, FilterId.PATIENT_REPORTED_DATA);
        });
        const cardiacFilter = this.filterEventService.cardiacFilter.subscribe((cardiacChanged: any) => {
            this.updateAfterEventFilter(cardiacChanged, FilterId.CARDIAC);
        });
        const exacerbationsFilter = this.filterEventService.exacerbationsFilter.subscribe((exacerbationsChanged: any) => {
            this.updateAfterEventFilter(exacerbationsChanged, FilterId.EXACERBATIONS);
        });

        this.filterListeners = [popFilter, aesFilter, doseFilter, labsFilter, conmedsFilter,
            vitalsFilter, cardiacFilter, spirometryFilter, exacerbationsFilter, patientDataFilter];
    }

    private updateAfterEventFilter(changed: any, filterId: FilterId): void {
        if (isEmpty(changed)) {
            return;
        }
        this.timelineDispatcher.updateOthersIsInitialized(false);
        this.updatePopulation();
        this.trellisingDispatcher.localResetNotification(filterId);

    }

    private unsubscribeFromFilters(): void {
        this.filterListeners.forEach((listener: Subscription) => {
            listener.unsubscribe();
        });
    }

    private haveChangedNumberOfSubectsPerPage(page: ITimelinePage): boolean {
        return page.offset === 0;
    }

    /**
     * Dispatches event with payload containing array of subject with tracks data
     * and recalculates ECG warnings based on data. Finally updates zoom based on data.
     * @param {ISubject[]} subjects - array of subjects with data
     */
    private updateTracksData(subjects: ISubject[]): void {
        this.timelineDispatcher.updateSelectedSubjects(subjects);
        this.updateAvailableEcgWarnings(subjects);
        this.updateZoom();
    }

    /**
     * Takes currently selected tracks and selected x axis value from store
     * and requests available subjects from server
     * @returns {Observable<string[]>} - array of available subjects
     */
    private getPossibleSubjects(): Observable<string[]> {
        const tracks: ITrack[] = this.timelineObservables.getCurrentDisplayedTracks();
        const dayZero: DayZero = this.timelineObservables.getCurrentDayZero();
        return this.dataService.getPossibleSubjects(dayZero, tracks);
    }

    /**
     * Takes currently selected subjects and selected x axis value from store
     * and requests track data from server
     * @returns {Observable<ISubject[]>} - array of initial subjects with updated tracks data
     */
    private getTrackData(): Observable<ISubject[]> {
        const currentDisplayedSubjects: ISubject[] = this.timelineObservables.getCurrentDisplayedSubjects();
        const dayZero: DayZero = this.timelineObservables.getCurrentDayZero();
        return this.dataService.getTrackData(currentDisplayedSubjects, dayZero);

    }
}
