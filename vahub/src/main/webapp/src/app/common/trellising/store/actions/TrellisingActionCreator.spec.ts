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

import {TestBed} from '@angular/core/testing';

import {TrellisingActionCreator} from './TrellisingActionCreator';

import {TabId} from '../ITrellising';
import {
    TRELLIS_RESET, TRELLIS_UPDATE_HEIGHT, TRELLIS_REFRESH_PLOTS,
    TRELLIS_UPDATE_SELECTION_DETAIL, TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND, TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_SELECTION, TRELLIS_CLEAR_SELECTIONS, TRELLIS_CHANGE_AXIS
} from './TrellisingActions';

    describe('GIVEN a TrellisingActionCreator class', () => {
        const tabId = TabId.VITALS_BOXPLOT;
        const trellisingActionCreator: TrellisingActionCreator = new TrellisingActionCreator(tabId);

        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    TrellisingActionCreator
                ],
                declarations: []
            });
        });
        describe('WHEN makeResetAction is called', () => {
            it('THEN TRELLIS_RESET action is returned', () => {
                const expectedResult = {type: TRELLIS_RESET, payload: {}};
                expect(TrellisingActionCreator.makeResetAction()).toEqual(expectedResult);
            });
        });

        describe('WHEN makeRefreshAction is called', () => {
            it('THEN TRELLIS_REFRESH_PLOTS action is returned', () => {
                const expectedResult = {type: TRELLIS_REFRESH_PLOTS, payload: {}};
                expect(TrellisingActionCreator.makeRefreshAction()).toEqual(expectedResult);
            });
        });

        describe('WHEN makeUpdateHeightAction is called', () => {
            it('THEN TRELLIS_UPDATE_HEIGHT action is returned', () => {
                const height = 100;
                const expectedResult = {
                    type: TRELLIS_UPDATE_HEIGHT,
                    payload: {
                        height: height
                    }
                };
                expect(TrellisingActionCreator.makeUpdateHeightAction(height)).toEqual(expectedResult);
            });
        });

        describe('WHEN makeUpdateSelectionDetailAction is called', () => {
            it('THEN TRELLIS_UPDATE_SELECTION_DETAIL action is returned', () => {
                const selectionDetail: any = 'detail';
                const expectedResult = {
                    type: TRELLIS_UPDATE_SELECTION_DETAIL,
                    payload: {
                        tabId: tabId,
                        detail: selectionDetail
                    }
                };
                expect(trellisingActionCreator.makeUpdateSelectionDetailAction(selectionDetail)).toEqual(expectedResult);
            });
        });

        describe('WHEN makeUpdateEventDetailsOnDemandAction is called', () => {
            it('THEN TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND action is returned', () => {
                const tableData: any = 'table data';
                const expectedResult = {
                    type: TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
                    payload: {
                        tabId: tabId,
                        detailsOnDemand: tableData
                    }
                };
                expect(trellisingActionCreator.makeUpdateEventDetailsOnDemandAction(tableData)).toEqual(expectedResult);
            });
        });

        describe('WHEN makeUpdateSubjectDetailsOnDemandAction is called', () => {
            it('THEN TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND action is returned', () => {
                const tableData: any = 'table data';
                const expectedResult = {
                    type: TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
                    payload: {
                        tabId: tabId,
                        detailsOnDemand: tableData
                    }
                };
                expect(trellisingActionCreator.makeUpdateSubjectDetailsOnDemandAction(tableData, tabId)).toEqual(expectedResult);
            });
        });

        describe('WHEN makeUpdateSelectionAction is called', () => {
            it('THEN TRELLIS_UPDATE_SELECTION action is returned', () => {
                const selection: any = 'some selection';
                const expectedResult = {
                    type: TRELLIS_UPDATE_SELECTION,
                    payload: {
                        tabId: tabId,
                        selection: selection
                    }
                };
                expect(trellisingActionCreator.makeUpdateSelectionAction(selection)).toEqual(expectedResult);
            });
        });

        describe('WHEN makeClearSelectionsAction is called', () => {
            it('THEN TRELLIS_UPDATE_SELECTION action is returned', () => {
                const expectedResult = {type: TRELLIS_CLEAR_SELECTIONS, payload: {tabId: tabId}};
                expect(trellisingActionCreator.makeClearSelectionsAction()).toEqual(expectedResult);
            });
        });

        describe('WHEN makeUpdateXAxisOption is called', () => {
            it('THEN TRELLIS_CHANGE_AXIS action is returned', () => {
                const option: any = 'some option';
                const expectedResult = {type: TRELLIS_CHANGE_AXIS, payload: {tabId: tabId, xAxis: true, option: option}};
                expect(trellisingActionCreator.makeUpdateXAxisOption(option)).toEqual(expectedResult);
            });
        });
    });
