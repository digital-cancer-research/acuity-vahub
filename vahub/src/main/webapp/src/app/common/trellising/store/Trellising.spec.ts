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

import {inject, TestBed} from '@angular/core/testing';
import {Store, StoreModule} from '@ngrx/store';

import {
    TabId, TabStore, FilterId
} from './ITrellising';
import {TimelineConfigService} from './services/TimelineConfigService';
import {
    FilterEventService,
} from '../../../filters/module';
import {
    MockFilterEventService, MockSessionEventService, MockDataService, MockFiltersUtils
} from '../../MockClasses';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {TabStoreUtils} from './utils/TabStoreUtils';
import {DataService} from '../data/DataService';
import {Trellising} from './Trellising';
import {trellisingReducer} from './reducer/TrellisingReducer';
import {
    TRELLIS_REFRESH_PLOTS, TRELLIS_RESET, TRELLIS_UPDATE_SELECTION, TRELLIS_OPTION_CHANGE_TRELLISING, TRELLIS_CHANGE_AXIS, TRELLIS_NO_DATA,
    TRELLIS_UPDATE_ZOOM, TRELLIS_CHANGE_PAGE
} from './actions/TrellisingActions';
import {TrellisingObservables} from './observable/TrellisingObservables';
import {TrellisingDispatcher} from './dispatcher/TrellisingDispatcher';
import {LabsFiltersModel} from '../../../filters/dataTypes/labs/LabsFiltersModel';
import {DatasetViews} from '../../../security/DatasetViews';
import {ApplicationState} from '../../store/models/ApplicationState';
import {FiltersUtils} from '../../../filters/utils/FiltersUtils';

xdescribe('GIVEN Trellising', () => {
    const tabId = TabId.VITALS_BOXPLOT;
    let initialState: TabStore;
    let store: Store<ApplicationState>;
    let trellising: Trellising;
    let trellisingObservables: TrellisingObservables;
    let trellisingDispatcher: TrellisingDispatcher;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                StoreModule.forRoot({trellisingReducer: trellisingReducer}),
            ],
            providers: [
                TabId,
                TabStoreUtils,
                {
                    provide: TimelineConfigService,
                    deps: [LabsFiltersModel, DatasetViews]
                },
                TrellisingObservables,
                TrellisingDispatcher,
                {provide: DataService, useClass: MockDataService},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: FiltersUtils, useClass: MockFiltersUtils},
                {
                    provide: Trellising,
                    useFactory: (tabStore: Store<ApplicationState>,
                                 dataService: DataService,
                                 sessionEventService: SessionEventService,
                                 trellisingObservables_: TrellisingObservables,
                                 trellisingDispatcher_: TrellisingDispatcher,
                                 filterUtils: FiltersUtils): Trellising => {
                        return new Trellising(tabStore, dataService, sessionEventService,
                            trellisingObservables_, trellisingDispatcher_, filterUtils);
                    },
                    deps: [Store, DataService, SessionEventService, TrellisingObservables, TrellisingDispatcher, FiltersUtils]
                }
            ]
        });
    });
    beforeEach(
        inject([Trellising, Store, TrellisingObservables, TrellisingDispatcher],
            (_trellising: Trellising, _store: Store<ApplicationState>,
             _trellisingObservables: TrellisingObservables, _trellisingDispatcher: TrellisingDispatcher) => {
            store = _store;
            trellising = _trellising;
            trellisingObservables = _trellisingObservables;
            trellisingDispatcher = _trellisingDispatcher;
            trellising.setTabId(tabId);
            initialState = TabStoreUtils.buildInitialStore();
            spyOn(store, 'dispatch');
            spyOn((<any>trellising).trellisingObservables, 'getState').and.callFake(() => {
                return initialState;
            });
        })
    );

    describe('WHEN init is called', () => {
        beforeEach(() => {
            spyOn((<any>trellising).actions$, 'next');
        });
        describe('AND trellising is initilized', () => {
            it('THEN TRELLIS_REFRESH_PLOTS action is emitted', () => {
                spyOn((<any>trellising).trellisingObservables, 'getCurrentIsInitialised').and.returnValue(true);
                trellising.init();
                const expectedPayload = {type: TRELLIS_REFRESH_PLOTS, payload: {}};
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });
        describe('AND trellising is not initilized', () => {
            it('THEN TRELLIS_RESET action is emitted', () => {
                spyOn((<any>trellising).trellisingObservables, 'getCurrentIsInitialised').and.returnValue(false);
                trellising.init();
                const expectedPayload = {type: TRELLIS_RESET, payload: {}};
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });
    });

    describe('WHEN reset is called', () => {
        beforeEach(() => {
            spyOn((<any>trellising).actions$, 'next');
        });
        it('THEN TRELLIS_RESET action is emitted', () => {
            trellising.reset();
            const expectedPayload = {type: TRELLIS_RESET, payload: {}};
            expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

        });
    });

    describe('WHEN setTabId is called', () => {
        beforeEach(() => {
            spyOn(trellisingObservables, 'setTabId');
            spyOn(trellisingDispatcher, 'setTabId');
        });
        it('THEN setTabId is called for observables and dispatcher', () => {
            trellising.setTabId(tabId);
            expect(trellisingObservables.setTabId).toHaveBeenCalledWith(tabId);
            expect(trellisingDispatcher.setTabId).toHaveBeenCalledWith(tabId);

        });
    });

    describe('WHEN selection is changed', () => {
        describe('AND updateSelection is called', () => {
            beforeEach(() => {
                spyOn((<any>trellising).actions$, 'next');
            });
            it('THEN TRELLIS_UPDATE_SELECTION dispatch is emitted', () => {
                const selection: any = 'new selection';
                trellising.updateSelection(selection);
                const expectedPayload = {
                    type: TRELLIS_UPDATE_SELECTION,
                    payload: {
                        tabId: tabId,
                        selection: selection
                    }
                };
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });
    });

    describe('WHEN updateTrellisingOptions is called', () => {
        describe('AND updateSelection is called', () => {
            beforeEach(() => {
                spyOn((<any>trellising).actions$, 'next');
            });
            it('THEN TRELLIS_UPDATE_SELECTION action is emitted', () => {
                const newTrellising: any = 'new trellising';
                trellising.updateTrellisingOptions(newTrellising);
                const expectedPayload = {
                    type: TRELLIS_OPTION_CHANGE_TRELLISING,
                    payload: {
                        tabId: tabId,
                        trellising: newTrellising
                    }
                };
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });
    });

    describe('WHEN axis option is changed', () => {
        describe('AND updateXAxisOption is called', () => {
            beforeEach(() => {
                spyOn((<any>trellising).actions$, 'next');
            });
            it('THEN TRELLIS_CHANGE_AXIS action is emitted', () => {
                const option: any = 'new option';
                trellising.updateXAxisOption(option);
                const expectedPayload = {
                    type: TRELLIS_CHANGE_AXIS,
                    payload: {tabId: tabId, xAxis: true, option: option}
                };
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });

        describe('AND updateYAxisOption is called', () => {
            beforeEach(() => {
                spyOn((<any>trellising).actions$, 'next');
            });
            it('THEN TRELLIS_CHANGE_AXIS action is emitted', () => {
                const option: any = 'new option';
                trellising.updateYAxisOption(option);
                const expectedPayload = {
                    type: TRELLIS_CHANGE_AXIS,
                    payload: {tabId: tabId, yAxis: true, option: option}
                };
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });
    });

    describe('WHEN setNoData is called', () => {
        beforeEach(() => {
            spyOn(TabStoreUtils, 'tabsRelatedToFilter').and.returnValue([TabId.VITALS_BOXPLOT]);
            spyOn((<any>trellising).actions$, 'next');
        });
        it('THEN TRELLIS_NO_DATA action is emitted', () => {
            trellising.setNoData(FilterId.VITALS);
            const expectedPayload = {type: TRELLIS_NO_DATA, payload: {tabId: TabId.VITALS_BOXPLOT, noData: true}};
            expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);
        });
    });

    describe('WHEN zoom option is changed', () => {
        beforeEach(() => {
            spyOn((<any>trellising).actions$, 'next');
        });
        describe('AND updateXAxisZoom is called', () => {
            it('THEN TRELLIS_UPDATE_ZOOM action is emitted with xAxis option true', () => {
                const zoom: any = 'new zoom';
                trellising.updateXAxisZoom(zoom, tabId);
                const expectedPayload = {type: TRELLIS_UPDATE_ZOOM, payload: {tabId: tabId, xAxis: true, zoom: zoom}};
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);

            });
        });
        describe('AND updateYAxisZoom is called', () => {
            it('THEN TRELLIS_UPDATE_ZOOM action is emitted with yAxis option true', () => {
                const zoom: any = 'new zoom';
                trellising.updateYAxisZoom(zoom, tabId);
                const expectedPayload = {type: TRELLIS_UPDATE_ZOOM, payload: {tabId: tabId, yAxis: true, zoom: zoom}};
                expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);
            });
        });
    });

    describe('WHEN updatePage is called', () => {
        beforeEach(() => {
            spyOn((<any>trellising).actions$, 'next');
        });
        it('THEN TRELLIS_CHANGE_PAGE action is emitted', () => {
            const pageNumber = 11;
            trellising.updatePage(pageNumber);
            const expectedPayload = {
                type: TRELLIS_CHANGE_PAGE,
                payload: {
                    tabId: tabId,
                    pageNumber: pageNumber
                }
            };
            expect((<any>trellising).actions$.next).toHaveBeenCalledWith(expectedPayload);
        });
    });
});
