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

import {TestBed, inject} from '@angular/core/testing';
import {fromJS} from 'immutable';
import {Store, StoreModule} from '@ngrx/store';

import {TabId, TabStore, FilterId} from '../ITrellising';
import {TimelineConfigService} from '../services/TimelineConfigService';
import {
    MockSessionEventService,
    MockTimelineConfigService
} from '../../../MockClasses';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {TabStoreUtils} from '../utils/TabStoreUtils';
import {trellisingReducer} from '../reducer/TrellisingReducer';
import {
    TRELLIS_UPDATE_HEIGHT, TRELLIS_INITIALISED,
    TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
    TRELLIS_CLEAR_SELECTIONS, TRELLIS_CHANGE_AXIS,
    TRELLIS_GENERATE_EMPTY_PLOTS, TRELLIS_UPDATE_PLOTS, TRELLIS_NO_DATA,
    TRELLIS_CLEAR_SELECTION, TRELLIS_CHANGE_AXIS_OPTIONS,
    TRELLIS_UPDATE_ZOOM, TRELLIS_CHANGE_PAGE
} from '../actions/TrellisingActions';
import {TrellisingObservables} from '../observable/TrellisingObservables';
import {TrellisingDispatcher} from './TrellisingDispatcher';
import {TrellisingActionCreator} from '../actions/TrellisingActionCreator';
import {DatasetViews} from '../../../../security/DatasetViews';
import {PopulationFiltersModel} from '../../../../filters/dataTypes/population/PopulationFiltersModel';

describe('GIVEN TrellisingDispatcher', () => {
    const tabId = TabId.VITALS_BOXPLOT;
    let initialState: TabStore;
    let store: Store<TabStore>;
    let trellisingDispatcher: TrellisingDispatcher;
    let trellisingActionCreator: TrellisingActionCreator;
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                StoreModule.forRoot({trellisingReducer: trellisingReducer}),
            ],
            providers: [
                TabId,
                TabStoreUtils,
                DatasetViews,
                PopulationFiltersModel,
                TrellisingObservables,
                {
                    provide: TimelineConfigService,
                    useClass: MockTimelineConfigService

                },
                {
                    provide: TrellisingActionCreator,
                    useFactory: (): TrellisingActionCreator => {
                        return new TrellisingActionCreator(tabId);
                    }
                },
                {provide: SessionEventService, useClass: MockSessionEventService},
                {
                    provide: TrellisingDispatcher,
                    useFactory: (tabStore: Store<TabStore>,
                                 timelineConfigService: TimelineConfigService,
                                 sessionEventService: SessionEventService): TrellisingDispatcher => {
                        return new TrellisingDispatcher(tabStore, timelineConfigService, sessionEventService);
                    },
                    deps: [Store, TimelineConfigService, SessionEventService]
                }
            ]
        });
    });

    beforeEach(
        inject([TrellisingDispatcher, Store, TrellisingActionCreator],
            (_trellisingDispatcher: TrellisingDispatcher, _store: Store<TabStore>, _trellisingActionCreator: TrellisingActionCreator) => {
                store = _store;
                trellisingDispatcher = _trellisingDispatcher;
                trellisingDispatcher.setTabId(tabId);
                trellisingActionCreator = _trellisingActionCreator;
                initialState = TabStoreUtils.buildInitialStore();
                spyOn(store, 'dispatch');
            })
    );

    describe('WHEN updateHeight is called', () => {
        it('THEN TRELLIS_UPDATE_HEIGHT is dispatched', () => {
            const height = 100;
            trellisingDispatcher.updateHeight(height);
            const expectedPayload = {
                type: TRELLIS_UPDATE_HEIGHT,
                payload: {
                    height: height
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

        });
    });


    describe('WHEN localResetNotification is called', () => {
        beforeEach(() => {
            spyOn(TabStoreUtils, 'tabsRelatedToFilter').and.returnValue([TabId.VITALS_BOXPLOT]);

        });
        it('THEN TRELLIS_RESET is dispatched', () => {
            trellisingDispatcher.localResetNotification(FilterId.VITALS);
            const expectedPayload = {
                type: TRELLIS_INITIALISED,
                payload: {
                    tabId: TabId.VITALS_BOXPLOT,
                    isInitialised: false
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

        });
    });

    describe('WHEN details on demand data is updated', () => {
        describe('AND updateEventDetailsOnDemand is called', () => {
            it('THEN TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND is dispatched', () => {
                const someData = ['new DoD data'];
                trellisingDispatcher.updateEventDetailsOnDemand(someData);
                const expectedPayload = {
                    type: TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
                    payload: {
                        tabId: tabId,
                        detailsOnDemand: someData
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

            });
        });

        describe('AND updateSubjectDetailsOnDemand is called', () => {
            it('THEN TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND is dispatched', () => {
                const someData = ['new DoD data'];
                trellisingDispatcher.updateSubjectDetailsOnDemand(someData, tabId);
                const expectedPayload = {
                    type: TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
                    payload: {
                        tabId: tabId,
                        detailsOnDemand: someData
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

            });
        });
    });

    describe('WHEN selection is changed', () => {
        describe('AND clearSelections is called', () => {
            it('THEN TRELLIS_CLEAR_SELECTIONS is dispatched', () => {
                trellisingDispatcher.clearSelections();
                const expectedPayload = {
                    type: TRELLIS_CLEAR_SELECTIONS,
                    payload: {
                        tabId: tabId
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
            });
        });
        describe('AND clearSelection is called', () => {
            it('THEN TRELLIS_CLEAR_SELECTION is dispatched', () => {
                trellisingDispatcher.clearSelection();
                const expectedPayload = {
                    type: TRELLIS_CLEAR_SELECTION,
                    payload: {tabId: tabId}
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
            });
        });
    });

    describe('WHEN axis option is changed', () => {
        describe('AND updateXAxisOption is called', () => {
            it('THEN TRELLIS_CHANGE_AXIS is dispatched', () => {
                const option: any = 'new option';
                trellisingDispatcher.updateXAxisOption(option);
                const expectedPayload = {
                    type: TRELLIS_CHANGE_AXIS,
                    payload: {tabId: tabId, xAxis: true, option: option}
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

            });
        });
        describe('AND updateXAxisOptions is called', () => {
            it('THEN TRELLIS_CHANGE_AXIS_OPTIONS with xAxis = true is dispatched', () => {
                const options: any = [{
                    value: 'someValue',
                    intarg: 1,
                    stringarg: 'arg'
                }];
                trellisingDispatcher.updateXAxisOptions(options);
                const expectedPayload = {
                    type: TRELLIS_CHANGE_AXIS_OPTIONS,
                    payload: {
                        tabId: tabId,
                        xAxis: true,
                        options: fromJS(options)
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

            });
        });
        describe('AND updateYAxisOptions is called', () => {
            it('THEN TRELLIS_CHANGE_AXIS_OPTIONS with yAxis = true is dispatched', () => {
                const options: any = ['option1', 'option2'];
                trellisingDispatcher.updateYAxisOptions(options);
                const expectedPayload = {
                    type: TRELLIS_CHANGE_AXIS_OPTIONS,
                    payload: {
                        tabId: tabId,
                        yAxis: true,
                        options: fromJS(options)
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);

            });
        });
    });

    describe('WHEN makeInitialStateOfTimelineAction is called', () => {
        it('THEN action with initial timeline state is dispatched', () => {
            trellisingDispatcher.setInitialStateOfTimeline();
            const expectedPayload = {
                type: 'UPDATE_INITIAL_OPENING_STATE',
                payload: {
                    timelineId: 'compareSubjects',
                    tracks: ['sometrack'],
                    performedJumpToTimeline: true,
                    isInitialized: false
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
        });
    });

    describe('WHEN generateEmptyPlots is called', () => {
        it('THEN TRELLIS_GENERATE_EMPTY_PLOTS is dispatched', () => {
            trellisingDispatcher.generateEmptyPlots();
            const expectedPayload = {
                type: TRELLIS_GENERATE_EMPTY_PLOTS,
                payload: {
                    tabId: tabId
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
        });
    });

    describe('WHEN updatePlotData is called', () => {
        it('THEN TRELLIS_UPDATE_PLOTS is dispatched', () => {
            const data: any = 'some data';
            trellisingDispatcher.updatePlotData(data);
            const expectedPayload = {
                type: TRELLIS_UPDATE_PLOTS,
                payload: {
                    tabId: tabId,
                    plots: data
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
        });
    });

    describe('WHEN updateNoData is called', () => {
        describe('AND plot data is empty', () => {
            it('THEN TRELLIS_NO_DATA with noData = true is dispatched', () => {
                const data: any = fromJS([]);
                trellisingDispatcher.updateNoData(data);
                const expectedPayload = {
                    type: TRELLIS_NO_DATA,
                    payload: {
                        tabId: tabId,
                        noData: true
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
            });
        });
        describe('AND plot data is not empty', () => {
            it('THEN TRELLIS_NO_DATA with noData = false is dispatched', () => {
                const data: any = fromJS([1]);
                trellisingDispatcher.updateNoData(data);
                const expectedPayload = {
                    type: TRELLIS_NO_DATA,
                    payload: {
                        tabId: tabId,
                        noData: false
                    }
                };
                expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
            });
        });
    });

    describe('WHEN updatePlotData is called', () => {
        it('THEN TRELLIS_UPDATE_PLOTS is dispatched', () => {
            const data: any = 'some data';
            trellisingDispatcher.updatePlotData(data);
            const expectedPayload = {
                type: TRELLIS_UPDATE_PLOTS,
                payload: {
                    tabId: tabId,
                    plots: data
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
        });
    });

    describe('WHEN updateZoom is called', () => {
        it('THEN TRELLIS_UPDATE_ZOOM is dispatched', () => {
            trellisingDispatcher.updateZoom();
            const expectedPayload = {
                type: TRELLIS_UPDATE_ZOOM,
                payload: {
                    tabId: tabId,
                    resetToDefault: undefined
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
        });
    });

    describe('WHEN updatePageNumber is called', () => {
        it('THEN TRELLIS_UPDATE_ZOOM is dispatched', () => {
            const pageNumber = 3;
            trellisingDispatcher.updatePageNumber(pageNumber);
            const expectedPayload = {
                type: TRELLIS_CHANGE_PAGE,
                payload: {
                    tabId: tabId,
                    pageNumber: pageNumber
                }
            };
            expect(store.dispatch).toHaveBeenCalledWith(expectedPayload);
        });
    });
});
