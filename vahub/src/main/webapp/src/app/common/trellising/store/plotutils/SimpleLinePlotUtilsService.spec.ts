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
    TabId,
    PlotType,
    IPlot,
    ILegend,
    LegendSymbol,
    TrellisDesign
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {SimpleLinePlotUtilsService} from './SimpleLinePlotUtilsService';

describe('GIVEN a SimpleLinePlotUtilsService class', () => {
    let utils: SimpleLinePlotUtilsService;
    beforeEach(() => {
        utils = new SimpleLinePlotUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: SimpleLinePlotUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([SimpleLinePlotUtilsService], (_simpleLinePlotUtilsService) => {
        utils = _simpleLinePlotUtilsService;
    }));

    const singlePlotsWithSingleSeries = fromJS([{
        plotType: PlotType.SIMPLE_LINEPLOT,
        trellising: null,
        series: null,
        data: [
            {
                color: '#000000',
                name: 'test_subject_1',
                series: [
                    {
                        x: 0.0,
                        y: 0.0,
                        name: {
                            subjectCycle: 'test_subject_1'
                        },
                        color: '#000000'
                    }, {
                        x: 4.0,
                        y: 51.0,
                        name: {
                            subjectCycle: 'test_subject_1'
                        },
                        color: '#000000'
                    }, {
                        x: 8.0,
                        y: 32.0,
                        name: {
                            subjectCycle: 'test_subject_1'
                        },
                        color: '#000000'
                    }
                ]
            }
        ]
    }]);

    const tlDimaetersPlotWithNoTrellising = fromJS([{
        plotType: PlotType.SIMPLE_LINEPLOT,
        trellising: null,
        series: null,
        data: [
            {
                color: '#000000',
                name: 'test_subject_1',
                series: [
                    {
                        x: 0.0,
                        y: 0.0,
                        name: 'No ASMT',
                        color: 'black'
                    }, {
                        x: 4.0,
                        y: 51.0,
                        name: 'No ASMT 2',
                        color: 'red'
                    }
                ],

            }
        ]
    }]);

    describe('WHEN the legend is requested', () => {
        describe('AND single plot is present', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = singlePlotsWithSingleSeries;
            });
            describe('AND no trellising is present', () => {
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.ANALYTE_CONCENTRATION, fromJS([]));
                    expect(legends).toEqual([<ILegend>{
                        title: undefined,
                        entries: [
                            {
                                label: 'test_subject_1',
                                symbol: LegendSymbol.CIRCLE,
                                color: '#000000'
                            }
                        ]
                    }]);
                });
            });
        });
    });

    describe('WHEN the legend is requested', () => {
        describe('AND single plot is present', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = tlDimaetersPlotWithNoTrellising;
            });
            describe('AND no trellising is present', () => {
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.TL_DIAMETERS_PLOT, fromJS([]));
                    expect(legends).toEqual([<ILegend>{
                        title: 'Overall visit response',
                        entries: [
                            {
                                label: 'No ASMT',
                                symbol: LegendSymbol.CIRCLE,
                                color: 'black'
                            },
                            {
                                label: 'No ASMT 2',
                                symbol: LegendSymbol.CIRCLE,
                                color: 'red'
                            }
                        ]
                    }]);
                });
            });
        });
    });

    describe('WHEN calculating zoom ranges', () => {
        describe('AND tabId is TL_DIAMETERS_PLOT', () => {
            it('THEN SHOULD call calculateTlDiametersZoomRanges', () => {
                spyOn(utils, 'calculateTlDiametersZoomRanges');
                utils.calculateZoomRanges(List(), TrellisDesign.CONTINUOUS_OVER_TIME, TabId.TL_DIAMETERS_PLOT);
                expect(utils.calculateTlDiametersZoomRanges).toHaveBeenCalled();
            });

            it('THEN SHOULD calculate zoom ranges', () => {
                const plots = fromJS([{
                    plotType: PlotType.SIMPLE_LINEPLOT,
                    trellising: null,
                    series: null,
                    data: [{
                        series: [{
                            x: {
                                start: -1,
                                end: -1
                            },
                            y: -1
                        }]
                    }, {
                        series: [{
                            x: {
                                start: 2,
                                end: 2
                            },
                            y: 2
                        }]
                    }, {
                        series: [{
                            x: {
                                start: 3,
                                end: 3
                            },
                            y: 3
                        }]
                    }]
                }]);

                const expectedRanges = {
                    x: {
                        min: -1,
                        max: 3
                    },
                    y: {
                        min: -1,
                        max: 3
                    }
                };

                expect(
                    utils.calculateZoomRanges(
                        plots as any,
                        TrellisDesign.CONTINUOUS_OVER_TIME,
                        TabId.TL_DIAMETERS_PLOT
                    )
                ).toEqual(expectedRanges);
            });
        });

        describe('AND tabId is CTDNA_PLOT', () => {
            it('THEN SHOULD call calculateCtDnaZoomRanges', () => {
                spyOn(utils, 'calculateCtDnaZoomRanges');
                utils.calculateZoomRanges(List(), TrellisDesign.CONTINUOUS_OVER_TIME, TabId.CTDNA_PLOT);
                expect(utils.calculateCtDnaZoomRanges).toHaveBeenCalled();
            });

            it('THEN SHOULD calculate zoom ranges', () => {
                const plots = fromJS([{
                    plotType: PlotType.SIMPLE_LINEPLOT,
                    trellising: null,
                    series: null,
                    data: [{
                        series: [{
                            x: -1,
                            y: 1
                        }]
                    }, {
                        series: [{
                            x: 2,
                            y: 2
                        }]
                    }, {
                        series: [{
                            x: 3,
                            y: 3
                        }]
                    }]
                }]);

                const expectedRanges = {
                    x: {
                        min: -1,
                        max: 3
                    },
                    y: {
                        min: 0.1,
                        max: 3
                    }
                };

                expect(
                    utils.calculateZoomRanges(
                        plots as any,
                        TrellisDesign.CONTINUOUS_OVER_TIME,
                        TabId.CTDNA_PLOT
                    )
                ).toEqual(expectedRanges);
            });
        });

        describe('AND tabId is not TL_DIAMETERS_PLOT and not CTDNA', () => {
            it('THEN SHOULD call defaultZoomRanges', () => {
                spyOn(utils, 'calculateTlDiametersZoomRanges');
                spyOn(utils, 'defaultZoomRanges');
                utils.calculateZoomRanges(List(), TrellisDesign.CONTINUOUS_OVER_TIME, TabId.ANALYTE_CONCENTRATION);
                expect(utils.calculateTlDiametersZoomRanges).not.toHaveBeenCalled();
                expect(utils.defaultZoomRanges).toHaveBeenCalled();
            });
        });
    });
});
