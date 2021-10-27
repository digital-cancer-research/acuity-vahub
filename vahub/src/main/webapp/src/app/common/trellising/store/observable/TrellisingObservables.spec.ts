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
import {TabId, TabStore, DynamicAxisRecord, TrellisCategory} from '../ITrellising';

import {TabStoreUtils} from '../utils/TabStoreUtils';
import {Store, StoreModule} from '@ngrx/store';
import {trellisingReducer} from '../reducer/TrellisingReducer';
import {TrellisingObservables} from './TrellisingObservables';
import {List} from 'immutable';
import {ApplicationState} from '../../../store/models/ApplicationState';

describe('GIVEN TrellisingObservables', () => {
    const tabId = TabId.VITALS_BOXPLOT;
    let initialState: TabStore;
    let store: Store<ApplicationState>;
    let trellisingObservables: TrellisingObservables;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                StoreModule.forRoot({trellisingReducer: trellisingReducer}),
            ],
            providers: [
                TabId,
                TabStoreUtils,
                TrellisingObservables,
                {
                    provide: TrellisingObservables,
                    useFactory: (tabStore: Store<ApplicationState>): TrellisingObservables => {
                        return new TrellisingObservables(tabStore);
                    }, deps: [Store]
                }
            ]
        });
    });
    beforeEach(
        inject([TrellisingObservables, Store], (_trellisingObservables: TrellisingObservables, _store: Store<ApplicationState>) => {
            store = _store;
            trellisingObservables = _trellisingObservables;
            trellisingObservables.setTabId(tabId);
            initialState = TabStoreUtils.buildInitialStore();
            spyOn(store, 'dispatch');
            spyOn(trellisingObservables, 'getState').and.callFake(() => {
                return initialState;
            });
        })
    );

    //----------------------------
    //TEST STORE GETTERS
    //----------------------------
    describe('WHEN getCurrentIsInitialised is called', () => {
        it('THEN initialization flag is returned', () => {

            initialState = <TabStore>initialState.setIn(['tabs', tabId, 'isInitialised'], false);
            expect(trellisingObservables.getCurrentIsInitialised()).toBeFalsy();
            initialState = <TabStore>initialState.setIn(['tabs', tabId, 'isInitialised'], true);
            expect(trellisingObservables.getCurrentIsInitialised()).toBeTruthy();
        });
    });

    describe('WHEN axis option is requested', () => {
        describe('AND getCurrentXAxisOption is called', () => {
            it('THEN x axis option is returned', () => {
                const newOption = {
                    value: 'someValue',
                    intarg: 1,
                    stringarg: 'arg'
                };
                initialState = <TabStore>initialState.setIn(['tabs', tabId, 'xAxis', 'option'],
                    new DynamicAxisRecord(newOption));
                expect(trellisingObservables.getCurrentXAxisOption()).toEqual(newOption);
            });
        });
        describe('AND getCurrentYAxisOption is called', () => {
            it('THEN y axis option is returned', () => {
                const newOption = 'someValue';
                initialState = <TabStore>initialState.setIn(['tabs', tabId, 'yAxis', 'option'], newOption);
                expect(trellisingObservables.getCurrentYAxisOption()).toEqual(newOption);
            });
        });
    });

    describe('WHEN getCurrentTrellising is called', () => {
        it('THEN current trellising option is returned', () => {
            const newTrellising = List.of({
                category: TrellisCategory.MANDATORY_HIGHER_LEVEL,
                trellisOptions: ['option1', 'option2'],
                trellisedBy: 'color'
            });
            initialState = <TabStore>initialState.setIn(['tabs', tabId, 'trellising'], newTrellising);
            expect(trellisingObservables.getCurrentTrellising()).toEqual(newTrellising);
        });
    });

    describe('WHEN getAllTrellises is called', () => {
        it('THEN base trellising is returned', () => {
            const newTrellising = List.of({
                category: TrellisCategory.MANDATORY_HIGHER_LEVEL,
                trellisOptions: ['option1', 'option2'],
                trellisedBy: 'color'
            });
            initialState = <TabStore>initialState.setIn(['tabs', tabId, 'baseTrellising'], newTrellising);
            expect(trellisingObservables.getAllTrellises()).toEqual(newTrellising);
        });
    });
});
