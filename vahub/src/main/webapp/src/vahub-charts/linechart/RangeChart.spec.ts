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

import {RangeChart} from './RangeChart';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';
import {fakeAsync, tick} from '@angular/core/testing';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');
const testAreaData = {
    type: 'arearange',
    color: 'red',
    data: [
        [0, 0, 1],
        [1, 0, 2]
    ]
};
const testLineData = {
    type: 'line',
    color: 'red',
    data: [
        {
            x: 1,
            y: 1.0,
            dataPoints: 2,
            stdErr: 0.0,
            ranges: [0.5, 2.0],
            marker: undefined,
            name: 'some name',
            category: 1
        }
    ]
};
const testTwoDotsLineData = {
    type: 'line',
    color: 'red',
    data: [
        {
            x: 1,
            y: 1.0,
            dataPoints: 2,
            stdErr: 0.0,
            ranges: [0.5, 2.0],
            marker: undefined,
            name: 'some name',
            category: 1
        },
        {
            x: 2,
            y: 2.0,
            dataPoints: 6,
            stdErr: 4.0,
            ranges: [0.0, 5.0],
            marker: undefined,
            name: 'some name 2',
            category: 2
        }
    ]
};

describe('Range chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = getTestOptions('range', renderTo, click, select);
        testChart = new RangeChart(testOptions);
    });

    afterEach(function () {
        d3.selectAll('svg').remove();
    });

    describe('create chart', function () {
        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#svg_test_id');
            expect(svg._groups[0].length).toEqual(1);
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });

        it('should render title', function () {
            const title = renderTo.querySelector('.chart-title');
            expect(title).not.toBeNull();
            expect(title.innerHTML).toEqual('test title');
        });

        it('should render export button', function () {
            expect(getAllElementsBySelector('.custom-export', renderTo).length).toBe(1);
        });
    });

    describe('create axis', function () {
        it('should render xAxis', function () {
            testChart.addSeries(testLineData);
            testChart.addSeries(testAreaData);
            testChart.redraw();
            expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
        });

        it('should render yAxis', function () {
            testChart.addSeries(testLineData);
            testChart.addSeries(testAreaData);
            testChart.redraw();
            expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
        });
    });

    describe('create chart elements', function () {
        describe('create dots', function () {
            it('should render one dot', function () {
                testChart.addSeries(testLineData);
                testChart.addSeries(testAreaData);
                testChart.redraw();
                expect(getAllElementsBySelector('circle', renderTo).length).toBe(1);
            });

            it('should render two dots', function () {
                testChart.addSeries(testTwoDotsLineData);
                testChart.addSeries(testAreaData);
                testChart.redraw();
                expect(getAllElementsBySelector('circle', renderTo).length).toBe(2);
            });
        });

        describe('create lines', function () {
            it('should render line', function () {
                testChart.addSeries(testTwoDotsLineData);
                testChart.addSeries(testAreaData);
                testChart.redraw();
                expect(getAllElementsBySelector('.line-with-points', renderTo).length).toBe(1);
            });
        });

        describe('create area', function () {
            it('should render arearange', function () {

                testChart.addSeries(testTwoDotsLineData);
                testChart.addSeries(testAreaData);
                testChart.redraw();
                expect(getAllElementsBySelector('#arearange path', renderTo).length).toBe(1);
            });
        });
    });

    describe('dot selection', function () {
        it('should call select when bar is selected', fakeAsync(function () {
            testChart.addSeries(testLineData);
            testChart.addSeries(testAreaData);
            testChart.redraw();
            const dot = d3.select(renderTo).select('.marker');
            dot.node().dispatchEvent(new MouseEvent('click', {}));
            tick(500);
            expect(select).toHaveBeenCalledTimes(1);
        }));
    });
});
