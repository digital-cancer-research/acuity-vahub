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
import {
    ILegend,
    IPlot,
    ITrellises,
    LegendSymbol,
    PlotType,
    TabId,
    TrellisCategory,
    TrellisDesign
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {StackedBarChartUtilsService} from './StackedBarChartUtilsService';
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;

describe('GIVEN a StackedBarChartUtilsService class', () => {
    const multiplePlotsWithSingleSeries = fromJS([
        {
            plotType: PlotType.STACKED_BARCHART,
            trellising: null,
            series: null,
            data: <ColoredOutputBarChartData[]>[{
                categories: ['A', 'B'],
                color: '#000000',
                name: '1',
                series: [
                    {category: 'A', rank: 1, value: 2},
                    {category: 'B', rank: 1, value: 3}
                ]
            }]
        },
        {
            plotType: PlotType.STACKED_BARCHART,
            trellising: null,
            series: null,
            data: <ColoredOutputBarChartData[]>[{
                categories: ['A', 'B'],
                color: '#000000',
                name: '1',
                series: [
                    {category: 'A', rank: 1, value: 2},
                    {category: 'B', rank: 1, value: 3}
                ]
            }]
        }]);
    const singlePlotWithSingleSeries = fromJS([{
        plotType: PlotType.STACKED_BARCHART,
        trellising: null,
        series: null,
        data: <ColoredOutputBarChartData[]>[{
            categories: ['A', 'B'],
            color: '#000000',
            name: '1',
            series: [
                {category: 'A', rank: 1, value: 2},
                {category: 'B', rank: 1, value: 3}
            ]
        }]
    }]);
    const multiplePlotsWithMultipleSeries = fromJS([{
        plotType: PlotType.STACKED_BARCHART,
        trellising: null,
        series: null,
        data: <ColoredOutputBarChartData[]>[
            {
                categories: ['A', 'B'],
                color: '#000000',
                name: '1',
                series: [
                    {category: 'A', rank: 1, value: 3},
                    {category: 'B', rank: 2, value: 2}
                ]
            },
            {
                categories: ['A', 'B'],
                color: '#000001',
                name: '2',
                series: [
                    {category: 'A', rank: 1, value: 4},
                    {category: 'B', rank: 2, value: 7}
                ]
            }
        ]
    },
        {
            plotType: PlotType.STACKED_BARCHART,
            trellising: null,
            series: null,
            data: <ColoredOutputBarChartData[]>[
                {
                    categories: ['A', 'B'],
                    color: '#000000',
                    name: '1',
                    series: [
                        {category: 'A', rank: 1, value: 3},
                        {category: 'B', rank: 2, value: 4}
                    ]
                },
                {
                    categories: ['A', 'B'],
                    color: '#000001',
                    name: '2',
                    series: [
                        {category: 'A', rank: 1, value: 4},
                        {category: 'B', rank: 2, value: 7}
                    ]
                }
            ]
        }]);
    const singlePlotWithMultipleSeries = fromJS([{
        plotType: PlotType.STACKED_BARCHART,
        trellising: null,
        series: null,
        data: <ColoredOutputBarChartData[]>[
            {
                categories: ['A', 'B'],
                color: '#000000',
                name: '1',
                series: [
                    {category: 'A', rank: 1, value: 3},
                    {category: 'B', rank: 2, value: 2}
                ]
            },
            {
                categories: ['A', 'B'],
                color: '#000001',
                name: '2',
                series: [
                    {category: 'A', rank: 1, value: 4},
                    {category: 'B', rank: 2, value: 7}
                ]
            }
        ]
    }]);
    let utils: StackedBarChartUtilsService;
    beforeEach(() => {
        utils = new StackedBarChartUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: StackedBarChartUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([StackedBarChartUtilsService], (_stackedBarChartUtilsService) => {
        utils = _stackedBarChartUtilsService;
    }));

    describe('WHEN the legend is requested', () => {
        describe('AND multiple plots are present', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = multiplePlotsWithSingleSeries;
            });
            describe('AND no trellising is present', () => {
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.POPULATION_BARCHART, fromJS([]));
                    expect(legends).toEqual([<ILegend>{
                        title: 'All',
                        entries: [
                            {
                                label: 'All',
                                symbol: LegendSymbol.CIRCLE,
                                color: '#000000'
                            }
                        ]
                    }]);
                });
            });
            describe('AND trellising is present', () => {
                it('THEN legend data is returned', () => {
                    const trellises = fromJS([
                        {
                            category: TrellisCategory.NON_MANDATORY_SERIES,
                            trellisOptions: [],
                            trellisedBy: 'Some label'
                        }
                    ]);
                    const legends = utils.extractLegend(plots, TabId.POPULATION_BARCHART, trellises);
                    expect(legends).toEqual([{
                        entries: [{label: '1', color: '#000000', symbol: 'CIRCLE'}],
                        title: 'Some label'
                    }]);
                });
            });
        });

        describe('AND a single stacked barchart has 1 series', () => {
            let plots: List<IPlot>;
            let trellises: List<ITrellises>;
            beforeEach(() => {
                plots = singlePlotWithSingleSeries;
            });

            describe('AND trellising is present', () => {
                it('THEN returns the legend data', () => {
                    trellises = fromJS([{
                        category: TrellisCategory.NON_MANDATORY_SERIES,
                        trellisedBy: 'Centre',
                        options: ['1', '2']
                    }]);
                    const legends = utils.extractLegend(plots, TabId.POPULATION_BARCHART, trellises);
                    expect(legends).toEqual([<ILegend>{
                        title: 'Centre',
                        entries: [
                            {
                                label: '1',
                                symbol: LegendSymbol.CIRCLE,
                                color: '#000000'
                            }
                        ]
                    }]);
                });
            });
            describe('AND no trellising is present', () => {

                it('THEN returns the legend data when no trellising', () => {
                    const legends = utils.extractLegend(plots, TabId.POPULATION_BARCHART, fromJS([]));
                    expect(legends).toEqual([<ILegend>{
                        title: 'All',
                        entries: [
                            {
                                label: 'All',
                                symbol: LegendSymbol.CIRCLE,
                                color: '#000000'
                            }
                        ]
                    }]);
                });
            });
        });

        describe('AND multiple stacked barchart have multiple series', () => {
            let plots: List<IPlot>;
            let trellises: List<ITrellises>;
            beforeEach(() => {
                plots = multiplePlotsWithMultipleSeries;
                trellises = fromJS([{
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'Centre',
                    options: ['1', '2']
                }]);
            });

            it('THEN returns the legend data', () => {
                const legends = utils.extractLegend(plots, TabId.POPULATION_BARCHART, trellises);
                expect(legends).toEqual([<ILegend>{
                    title: 'Centre',
                    entries: [
                        {
                            label: '1',
                            symbol: LegendSymbol.CIRCLE,
                            color: '#000000'
                        },
                        {
                            label: '2',
                            symbol: LegendSymbol.CIRCLE,
                            color: '#000001'
                        }
                    ]
                }]);
            });
        });

        describe('WHEN a single stacked barchart with multiple series', () => {
            let plots: List<IPlot>;
            let trellises: List<ITrellises>;
            beforeEach(() => {
                plots = singlePlotWithMultipleSeries;
                trellises = fromJS([{
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'Centre',
                    options: ['1', '2']
                }]);
            });

            it('THEN returns the legend data', () => {
                const legends = utils.extractLegend(plots, TabId.POPULATION_BARCHART, trellises);
                expect(legends).toEqual([<ILegend>{
                    entries: [
                        {
                            label: '1',
                            color: '#000000',
                            symbol: 'CIRCLE'
                        },
                        {
                            label: '2',
                            color: '#000001',
                            symbol: 'CIRCLE'
                        }], title: 'Centre'
                }]);
            });
        });
    });

    describe('WHEN the zoom is requested', () => {
        describe('AND a single stacked barchart has 1 series', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = singlePlotWithSingleSeries;
            });

            it('THEN returns the maximum height', () => {
                const expectedZoom = {x: {min: 0, max: 1}, y: {min: 0, max: 3}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.AES_COUNTS_BARCHART))
                    .toEqual(expectedZoom);
            });
        });

        describe('AND multiple stacked barchart have multiple series', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = multiplePlotsWithMultipleSeries;
            });

            it('THEN returns the maximum height', () => {
                const expectedZoom = {x: {min: 0, max: 1}, y: {min: 0, max: 11}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.AES_COUNTS_BARCHART))
                    .toEqual(expectedZoom);
            });
        });

        describe('AND a single stacked barchart has multiple series', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = singlePlotWithMultipleSeries;
            });
            it('THEN returns the maximum height', () => {
                const expectedZoom = {x: {min: 0, max: 1}, y: {min: 0, max: 9}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.AES_COUNTS_BARCHART))
                    .toEqual(expectedZoom);
            });
        });
    });
});
