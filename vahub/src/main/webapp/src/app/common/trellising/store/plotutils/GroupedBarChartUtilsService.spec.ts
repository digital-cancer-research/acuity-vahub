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
import {
    EMPTY,
    TabId,
    TrellisDesign
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {GroupedBarChartUtilsService} from './GroupedBarChartUtilsService';

describe('GIVEN a GroupedBarChartUtilsService class', () => {
    let utils: GroupedBarChartUtilsService;
    beforeEach(() => {
        utils = new GroupedBarChartUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: GroupedBarChartUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([GroupedBarChartUtilsService], (_groupedBarChartUtilsService) => {
        utils = _groupedBarChartUtilsService;
    }));

    describe('WHEN the zoom is requested', () => {
        it('THEN zoom is calculated correctly', () => {
            const plots = fromJS([{
                'plotType': 'GROUPED_BARCHART',
                'trellising': [],
                'series': [{
                    'trellisedBy': 'EXACERBATION_SEVERITY',
                    'category': 'NON_MANDATORY_SERIES',
                    'trellisOptions': [EMPTY, 'Mild', 'Moderate', 'Severe']
                }],
                'data': [{
                    'name': 'Severe',
                    'color': '#882255',
                    'categories': ['SuperDex 10 mg', 'SuperDex 20 mg', 'Placebo'],
                    'series': [{
                        'category': 'SuperDex 10 mg',
                        'rank': 1,
                        'value': 23,
                        'totalSubjects': 23
                    }, {
                        'category': 'SuperDex 20 mg',
                        'rank': 2,
                        'value': 18,
                        'totalSubjects': 18
                    }, {'category': 'Placebo', 'rank': 3, 'value': 8, 'totalSubjects': 8}]
                }, {
                    'name': 'Moderate',
                    'color': '#999933',
                    'categories': ['SuperDex 10 mg', 'SuperDex 20 mg', 'Placebo'],
                    'series': [{
                        'category': 'SuperDex 10 mg',
                        'rank': 1,
                        'value': 30,
                        'totalSubjects': 30
                    }, {
                        'category': 'SuperDex 20 mg',
                        'rank': 2,
                        'value': 25,
                        'totalSubjects': 25
                    }, {'category': 'Placebo', 'rank': 3, 'value': 11, 'totalSubjects': 11}]
                }, {
                    'name': 'Mild',
                    'color': '#332288',
                    'categories': ['SuperDex 10 mg', 'SuperDex 20 mg', 'Placebo'],
                    'series': [{
                        'category': 'SuperDex 10 mg',
                        'rank': 1,
                        'value': 30,
                        'totalSubjects': 30
                    }, {
                        'category': 'SuperDex 20 mg',
                        'rank': 2,
                        'value': 19,
                        'totalSubjects': 19
                    }, {'category': 'Placebo', 'rank': 3, 'value': 16, 'totalSubjects': 16}]
                }, {
                    'name': EMPTY,
                    'color': '#CC6677',
                    'categories': ['SuperDex 10 mg', 'SuperDex 20 mg', 'Placebo'],
                    'series': [{
                        'category': 'SuperDex 10 mg',
                        'rank': 1,
                        'value': 10,
                        'totalSubjects': 10
                    }, {
                        'category': 'Placebo',
                        'rank': 3,
                        'value': 6,
                        'totalSubjects': 6
                    }, {'category': 'SuperDex 20 mg', 'rank': 2, 'value': 5, 'totalSubjects': 5}]
                }]
            }]);
            const expectedZoom = {x: {min: 0, max: 2}, y: {min: 0, max: 33}};
            expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.EXACERBATIONS_GROUPED_COUNTS)).toEqual(expectedZoom);
        });
    });
});
