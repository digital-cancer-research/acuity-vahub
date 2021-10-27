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
    IPlot,
    TrellisDesign
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {BaseChartUtilsService} from './BaseChartUtilsService';

describe('GIVEN a BaseChartUtilsService class', () => {
    let utils: BaseChartUtilsService;
    beforeEach(() => {
        utils = new BaseChartUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: BaseChartUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([BaseChartUtilsService], (_baseChartUtilsService) => {
        utils = _baseChartUtilsService;
    }));

    describe('WHEN zoom is requested', () => {
        //no need to init plots value as in all cases spy covered usage of passed parameter
        const plots: List<IPlot> = undefined;
        describe('AND axes are CONTINUOUS_OVER_TIME', () => {
            beforeEach(() => {
                spyOn(utils, 'calculateZoomRangesContinuousX').and.returnValue({min: 11, max: 99});
            });
            it('THEN calculateZoomRangesContinuousX is called', () => {
                const expectedZoom = {
                    x: {min: 11, max: 99},
                    y: {min: 0, max: 100}
                };
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CONTINUOUS_OVER_TIME, null)).toEqual(expectedZoom);
                expect((<any>utils).calculateZoomRangesContinuousX).toHaveBeenCalled();
            });
        });
        describe('AND axes are CATEGORICAL_OVER_TIME', () => {
            it('THEN both zoom are set to 0-100', () => {
                const expectedZoom = {
                    x: {min: 0, max: 100},
                    y: {min: 0, max: 100}
                };
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_OVER_TIME, null)).toEqual(expectedZoom);
            });
        });
        describe('AND axes are CATEGORICAL_COUNTS_AND_PERCENTAGES', () => {
            beforeEach(() => {
                spyOn(utils, 'calculateZoomRangesCategoricalX').and.returnValue({min: 11, max: 99});
                spyOn(utils, 'calculateZoomRangesCategoricalY').and.returnValue({min: 12, max: 98});
            });
            it('THEN calculateZoomRangesCategoricalX and calculateZoomRangesCategoricalY is called', () => {
                const expectedZoom = {
                    x: {min: 11, max: 99},
                    y: {min: 12, max: 98}
                };
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, null)).toEqual(expectedZoom);
                expect((<any>utils).calculateZoomRangesCategoricalX).toHaveBeenCalled();
                expect((<any>utils).calculateZoomRangesCategoricalY).toHaveBeenCalled();
            });
        });
        describe('AND axes are VARIABLE_Y_CONST_X', () => {
            beforeEach(() => {
                spyOn(utils, 'calculateZoomRangesScatterX').and.returnValue({min: 11, max: 99});
                spyOn(utils, 'calculateZoomRangesScatterY').and.returnValue({min: 12, max: 98});
            });
            it('THEN calculateZoomRangesScatterX and calculateZoomRangesScatterY is called', () => {
                const expectedZoom = {
                    x: {min: 11, max: 99},
                    y: {min: 12, max: 98}
                };
                expect(utils.calculateZoomRanges(plots, TrellisDesign.VARIABLE_Y_CONST_X, null)).toEqual(expectedZoom);
                expect((<any>utils).calculateZoomRangesScatterX).toHaveBeenCalled();
                expect((<any>utils).calculateZoomRangesScatterY).toHaveBeenCalled();
            });
        });
        describe('AND axes are VARIABLE_Y_VARIABLE_X', () => {
            it('THEN both zoom are set to 0-100', () => {
                const expectedZoom = {
                    x: {min: 0, max: 100},
                    y: {min: 0, max: 100}
                };
                expect(utils.calculateZoomRanges(plots, TrellisDesign.VARIABLE_Y_VARIABLE_X, null)).toEqual(expectedZoom);
            });
        });
    });

    describe('WHEN plots are ordered with plot data', () => {
        const plots: any = List.of(new Map([
            ['plotType', null],
            ['trellising', List.of({'category': 'NON_MANDATORY_TRELLIS', 'trellisedBy': 'ARM', 'trellisOption': 'Placebo'}).toJS()],
            ['series', List.of(fromJS({
                'trellisedBy': 'MAX_SEVERITY_GRADE',
                'category': 'NON_MANDATORY_SERIES',
                'trellisOptions': ['GRADE 1', 'GRADE 2', 'GRADE 3']
            }))],
            ['data', null]
        ]), new Map([
            ['plotType', null],
            ['trellising', List.of({
                'category': 'NON_MANDATORY_TRELLIS',
                'trellisedBy': 'ARM',
                'trellisOption': 'SuperDex 20 mg'
            }).toJS()],
            ['series', List.of(fromJS({
                'trellisedBy': 'MAX_SEVERITY_GRADE',
                'category': 'NON_MANDATORY_SERIES',
                'trellisOptions': ['GRADE 1', 'GRADE 2', 'GRADE 3']
            }))],
            ['data', null]
    ]));
        const plotData: any = List.of(fromJS({
            'plotType': 'STACKED_BARCHART',
            'trellising': [{'trellisedBy': 'ARM', 'category': 'NON_MANDATORY_TRELLIS', 'trellisOption': 'Placebo'}],
            'data': [{
                'name': 'GRADE 3',
                'color': '#fe8c01',
                'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                'series': [{'category': 'abscess', 'rank': 2, 'value': 1, 'totalSubjects': null}]
            }, {
                'name': 'GRADE 2',
                'color': '#f7d533',
                'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                'series': [{'category': 'Gastroenteritis', 'rank': 1, 'value': 1, 'totalSubjects': null}]
            }]
        }), fromJS({
            'plotType': 'STACKED_BARCHART',
            'trellising': [{
                'trellisedBy': 'ARM',
                'category': 'NON_MANDATORY_TRELLIS',
                'trellisOption': 'SuperDex 20 mg'
            }],
            'data': [{
                'name': 'GRADE 3',
                'color': '#fe8c01',
                'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                'series': [{'category': 'Appendicitis', 'rank': 3, 'value': 1, 'totalSubjects': null}]
            }, {
                'name': 'GRADE 1',
                'color': '#b4da50',
                'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                'series': [{'category': 'Gastroenteritis', 'rank': 1, 'value': 1, 'totalSubjects': null}]
            }]
        }));

        it('THEN plot and plotdata are merged correctly', () => {
            const expectedResult = [new Map([
                ['plotType', 'STACKED_BARCHART'],
                ['trellising', List.of({category: 'NON_MANDATORY_TRELLIS', trellisedBy: 'ARM', trellisOption: 'Placebo'}).toJS()],
                ['series', [{
                    trellisedBy: 'MAX_SEVERITY_GRADE', category: 'NON_MANDATORY_SERIES',
                    trellisOptions: ['GRADE 1', 'GRADE 2', 'GRADE 3']
                }]],
                ['data', [{
                    'name': 'GRADE 3',
                    'color': '#fe8c01',
                    'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                    'series': [{'category': 'abscess', 'rank': 2, 'value': 1, 'totalSubjects': null}]
                }, {
                    'name': 'GRADE 2',
                    'color': '#f7d533',
                    'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                    'series': [{'category': 'Gastroenteritis', 'rank': 1, 'value': 1, 'totalSubjects': null}]
                }]]
            ]), new Map([
                ['plotType', 'STACKED_BARCHART'],
                ['trellising', List.of({
                    category: 'NON_MANDATORY_TRELLIS',
                    trellisedBy: 'ARM',
                    trellisOption: 'SuperDex 20 mg'
                }).toJS()],
                ['series', [{
                    trellisedBy: 'MAX_SEVERITY_GRADE', category: 'NON_MANDATORY_SERIES',
                    trellisOptions: ['GRADE 1', 'GRADE 2', 'GRADE 3']
                }]],
                ['data', [{
                    'name': 'GRADE 3',
                    'color': '#fe8c01',
                    'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                    'series': [{'category': 'Appendicitis', 'rank': 3, 'value': 1, 'totalSubjects': null}]
                }, {
                    'name': 'GRADE 1',
                    'color': '#b4da50',
                    'categories': ['Gastroenteritis', 'abscess', 'Appendicitis'],
                    'series': [{'category': 'Gastroenteritis', 'rank': 1, 'value': 1, 'totalSubjects': null}]
                }]]
            ])];
            expect(BaseChartUtilsService.orderPlots(plots, plotData).toJS()).toEqual(expectedResult);
        });
    });

});
