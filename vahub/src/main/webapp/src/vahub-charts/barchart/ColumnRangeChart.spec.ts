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

import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {ColumnRangeChart} from './ColumnRangeChart';
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';
import {BAR_CLASS} from './BarChart';
import {fakeAsync, tick} from '@angular/core/testing';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testOneBarData = {
    color: 'red',
    name: 'data1',
    data: [
        {
            x: 0,
            y: 5,
        }
    ]
};

describe('ColumnRangeChart chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = getTestOptions('stacked-bar-chart', renderTo, click, select);
        testChart = new ColumnRangeChart(testOptions);
    });

    afterEach(function () {
        d3.selectAll('svg').remove();
    });

    describe('create chart', function () {

        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#svg_test_id');
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });

        it('should render title', function () {
            const title = renderTo.getElementsByClassName('chart-title');
            expect(title[0].innerHTML).toEqual('test title');
        });

        it('should render export button', function () {
            expect(getAllElementsBySelector('.custom-export', renderTo).length).toBe(1);
        });
    });

    describe('create bars', function () {
        it('should render one bar', function () {
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(0);
        });

        it('should render one bar and one marker', function () {
            const testData = {
                color: 'red',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        high: 5,
                        low: -5,
                    }
                ]
            };
            const testMarkerData = {
                type: 'scatter',
                color: 'red',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        y: 5,
                        name: 'marker',
                        marker: {
                            fillColor: 'red',
                            name: 'test',
                            symbol: 'triangle',
                        }
                    }
                ]
            };
            testChart.addSeries(testData);
            testChart.addSeries(testMarkerData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(1);
            const marker = d3.select(renderTo).select('.marker').select('path');
            expect(marker.attr('fill')).toEqual('red');
            expect(marker.attr('transform')).toEqual('rotate(90)');

        });

        it('should render two bars and 3 markers', function () {
            const testData = {
                color: 'red',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        high: 0,
                        low: -10,
                    },
                    {
                        x: 1,
                        high: 45,
                        low: 25,
                        noStartDate: true
                    }
                ]
            };
            const testMarkerData = {
                color: 'blue',
                type: 'scatter',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        y: 5,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    },
                    {
                        x: 7,
                        y: -3,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    },
                    {
                        x: 2,
                        y: 9,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    }
                ]
            };
            testChart.addSeries(testData);
            testChart.addSeries(testMarkerData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(2);
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(3);
            const marker = d3.select(renderTo).select('.marker').select('path');
            expect(marker.attr('fill')).toEqual('blue');
            expect(marker.attr('transform')).toEqual(null);
        });
    });

    it('should render xAxis', function () {
        testChart.addSeries(testOneBarData);
        testChart.redraw();
        expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
    });

    it('should render yAxis', function () {
        testChart.addSeries(testOneBarData);
        testChart.redraw();
        expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
    });

    describe('test transformation', function () {
        it('should call select when bar is selected', fakeAsync(function () {
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            const bar = d3.select(renderTo).select(`.${BAR_CLASS}`);
            bar.node().dispatchEvent(new MouseEvent('click', {}));
            tick(500);
            expect(select).toHaveBeenCalledTimes(1);
        }));
    });
});

