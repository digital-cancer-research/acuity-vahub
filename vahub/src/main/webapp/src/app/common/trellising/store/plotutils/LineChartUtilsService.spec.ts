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
    ITrellises,
    TabId,
    IPlot,
    TrellisDesign
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {LineChartUtilsService} from './LineChartUtilsService';

describe('GIVEN a LineChartUtilsService class', () => {
    let utils: LineChartUtilsService;
    beforeEach(() => {
        utils = new LineChartUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: LineChartUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([LineChartUtilsService], (_lineChartUtilsService) => {
        utils = _lineChartUtilsService;
    }));

    describe('WHEN extractLegend is called', () => {
        describe('AND multiple range plots with 1 series are present', () => {
            let plots: List<IPlot>;
            describe('AND no trellising is present', () => {
                beforeEach(() => {
                    plots = fromJS([{
                        'plotType': 'LINECHART',
                        'trellising': [],
                        'data': [{
                            'name': ' ',
                            'color': '#CC6677',
                            'series': [
                                {'category': '-37', 'rank': 1, 'value': 1},
                                {'category': '-36', 'rank': 2, 'value': 0},
                                {'category': '-35', 'rank': 3, 'value': 0},
                                {'category': '-34', 'rank': 4, 'value': 11},
                                {'category': '-33', 'rank': 5, 'value': 1},
                                {'category': '-32', 'rank': 6, 'value': 0}
                            ]
                        }]
                    }]);
                });
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.EXACERBATIONS_COUNTS, fromJS([]));
                    expect(legends).toEqual([{
                        entries: [{label: 'All', color: '#CC6677', symbol: 'CIRCLE'}],
                        title: 'All'
                    }]);
                });
            });
            describe('AND no trellising is present', () => {
                let trellises: List<ITrellises>;
                beforeEach(() => {
                    plots = fromJS([{
                        'plotType': 'LINECHART',
                        'trellising': [],
                        'data': [{
                            'name': 'Placebo                                                                                                                                                                                                 ',
                            'color': '#CC6677',
                            'series': [
                                {'category': '-36', 'rank': 2, 'value': 0},
                                {'category': '-35', 'rank': 3, 'value': 0},
                                {'category': '-32', 'rank': 6, 'value': 0},
                                {'category': '-31', 'rank': 7, 'value': 0},
                                {'category': '-30', 'rank': 8, 'value': 1},
                                {'category': '-29', 'rank': 9, 'value': 0},
                                {'category': '-28', 'rank': 10, 'value': 1},
                                {'category': '-27', 'rank': 11, 'value': 1},
                                {'category': '-26', 'rank': 12, 'value': 0},
                                {'category': '-23', 'rank': 15, 'value': 0},
                                {'category': '-22', 'rank': 16, 'value': 1},
                                {'category': '-21', 'rank': 17, 'value': 1}]
                        }, {
                            'name': 'SuperDex 10 mg                                                                                                                                                                                          ',
                            'color': '#332288',
                            'series': [{'category': '-37', 'rank': 1, 'value': 1},
                                {'category': '-36', 'rank': 2, 'value': 0},
                                {'category': '-35', 'rank': 3, 'value': 0},
                                {'category': '-34', 'rank': 4, 'value': 1},
                                {'category': '-33', 'rank': 5, 'value': 1},
                                {'category': '-32', 'rank': 6, 'value': 0},
                                {'category': '-31', 'rank': 7, 'value': 0},
                                {'category': '-29', 'rank': 9, 'value': 0},
                                {'category': '-26', 'rank': 12, 'value': 0},
                                {'category': '-25', 'rank': 13, 'value': 1}]
                        }, {
                            'name': 'SuperDex 20 mg                                                                                                                                                                                          ',
                            'color': '#999933',
                            'series': [
                                {'category': '-36', 'rank': 2, 'value': 0},
                                {'category': '-35', 'rank': 3, 'value': 0},
                                {'category': '-32', 'rank': 6, 'value': 0},
                                {'category': '-31', 'rank': 7, 'value': 0},
                                {'category': '-29', 'rank': 9, 'value': 0},
                                {'category': '-26', 'rank': 12, 'value': 0},
                                {'category': '-23', 'rank': 15, 'value': 0},
                                {'category': '-20', 'rank': 18, 'value': 1}
                            ]
                        }]
                    }]);
                    trellises = fromJS([{
                        'trellisedBy': 'PLANNED_ARM',
                        'category': 'NON_MANDATORY_SERIES',
                        'trellisOptions': ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg']
                    }]);
                });
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.EXACERBATIONS_COUNTS, trellises);
                    expect(legends).toEqual([{
                        entries: [{
                            label: 'SuperDex 20 mg                                                                                                                                                                                          ',
                            color: '#999933',
                            symbol: 'CIRCLE'
                        }, {
                            label: 'SuperDex 10 mg                                                                                                                                                                                          ',
                            color: '#332288',
                            symbol: 'CIRCLE'
                        }, {
                            label: 'Placebo                                                                                                                                                                                                 ',
                            color: '#CC6677',
                            symbol: 'CIRCLE'
                        }], title: 'PLANNED_ARM'
                    }]);
                });
            });
        });
    });

    describe('WHEN the zoom is requested', () => {
        describe('AND axes are CATEGORICAL_COUNTS_AND_PERCENTAGES', () => {
            describe('AND 1 plot is present', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([{
                        'plotType': 'LINECHART',
                        'trellising': [],
                        'series': [],
                        'data': [{
                            'name': ' ',
                            'color': '#CC6677',
                            'series': [
                                {'category': '-37', 'rank': 1, 'value': 1},
                                {'category': '-36', 'rank': 2, 'value': 0},
                                {'category': '-35', 'rank': 3, 'value': 0},
                                {'category': '-34', 'rank': 4, 'value': 1},
                                {'category': '-33', 'rank': 5, 'value': 1},
                                {'category': '-32', 'rank': 6, 'value': 10}
                            ]
                        }]
                    }]);
                });
                it('THEN returns the maximum height', () => {
                    const expectedZoom = {x: {min: 0, max: 5}, y: {min: 0, max: 11}};
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.EXACERBATIONS_COUNTS)).toEqual(expectedZoom);
                });
            });
            describe('AND multiple plots are present', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([{
                        'plotType': 'LINECHART',
                        'trellising': [],
                        'series': [],
                        'data': [{
                            'name': ' ',
                            'color': '#CC6677',
                            'series': [
                                {'category': '-37', 'rank': 1, 'value': 1},
                                {'category': '-36', 'rank': 2, 'value': 0},
                                {'category': '-35', 'rank': 3, 'value': 0},
                                {'category': '-34', 'rank': 4, 'value': 1},
                                {'category': '-33', 'rank': 5, 'value': 1},
                                {'category': '-32', 'rank': 6, 'value': 11}
                            ]
                        }]
                    }, {
                        'plotType': 'LINECHART',
                        'trellising': [],
                        'series': [],
                        'data': [{
                            'name': ' ',
                            'color': '#CC6677',
                            'series': [
                                {'category': '-3', 'rank': 1, 'value': 11},
                                {'category': '-2', 'rank': 2, 'value': 0},
                                {'category': '-1', 'rank': 3, 'value': 0},
                                {'category': '0', 'rank': 4, 'value': 1},
                                {'category': '1', 'rank': 5, 'value': 1},
                                {'category': '2', 'rank': 6, 'value': 1},
                                {'category': '3', 'rank': 7, 'value': 20}
                            ]
                        }]
                    }]);
                });
                it('THEN returns the maximum height', () => {
                    const expectedZoom = {x: {min: 0, max: 6}, y: {min: 0, max: 22}};
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.EXACERBATIONS_COUNTS)).toEqual(expectedZoom);
                });
            });
        });
    });
});
