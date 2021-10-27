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
import {IPlot, PlotType, TabId, TrellisDesign} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {ScatterPlotUtilsService} from './ScatterPlotUtilsService';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

describe('GIVEN a ScatterPlotUtilsService class', () => {
    let utils: ScatterPlotUtilsService;
    beforeEach(() => {
        utils = new ScatterPlotUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: ScatterPlotUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([ScatterPlotUtilsService], (_scatterPlotUtilsService) => {
        utils = _scatterPlotUtilsService;
    }));

    describe('WHEN zoom ranges are calculated', () => {
        describe('AND data is not null', () => {
            let plots: List<IPlot>;

            beforeEach(() => {
                plots = fromJS([
                    {
                        plotType: PlotType.SCATTERPLOT,
                        trellising: null,
                        data: {
                            data: <OutputScatterPlotEntry[]>[
                                {
                                    color: '#000',
                                    name: 'test1',
                                    x: 12,
                                    y: 20
                                }]
                        }
                    },
                    {
                        plotType: PlotType.SCATTERPLOT,
                        trellising: null,
                        series: null,
                        data: {
                            data: <OutputScatterPlotEntry[]>[
                                {
                                    color: '#111',
                                    name: 'test2',
                                    x: 1,
                                    y: 22
                                }
                            ]
                        }
                    },
                ]);
            });

            it('THEN returns max X = 12, max Y = 20', () => {
                const expectedZoom = {x: {min: 0, max: 12}, y: {min: 0, max: 22}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.VARIABLE_Y_CONST_X, TabId.LIVER_HYSLAW)).toEqual(expectedZoom);
            });
        });

        describe('AND data is null', () => {
            let plots: List<IPlot>;

            beforeEach(() => {
                plots = fromJS([
                    {
                        trellising: null,
                        series: null,
                        data: null

                    },
                    {
                        trellising: null,
                        series: null,
                        data: null
                    }
                ]);
            });

            it('THEN returns max X = 0, max Y = 0', () => {
                const expectedZoom = {x: {max: 0, min: 0}, y: {max: 0, min: 0}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.VARIABLE_Y_CONST_X, TabId.LIVER_HYSLAW)).toEqual(expectedZoom);
            });
        });
    });

});
