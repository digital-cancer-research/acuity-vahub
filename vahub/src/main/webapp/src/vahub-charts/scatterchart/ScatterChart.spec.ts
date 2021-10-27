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

import {ScatterChart} from './ScatterChart';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testData = {
    color: 'red',
    type: 'point',
    data: [
        [1, 1]
    ]
};

describe('Scatter chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = getTestOptions('scatter', renderTo, click, select);
        testChart = new ScatterChart(testOptions);
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
    describe('create axis', function () {
        it('should render xAxis', function () {
            testChart.addSeries(testData);
            testChart.redraw();
            expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
        });

        it('should render yAxis', function () {
            testChart.addSeries(testData);
            testChart.redraw();
            expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
        });
    });

    describe('create dots', function () {
        it('should render one dot', function () {
            testChart.addSeries(testData);
            testChart.redraw();
            const dotColor = d3.select(renderTo).select('.circle').attr('fill');
            expect(dotColor).toEqual('red');
            expect(getAllElementsBySelector('.circle', renderTo).length).toBe(1);
        });

        it('should render two dots', function () {
            const testTwoDotsData = {
                color: 'red',
                type: 'point',
                data: [
                    [1, 1],
                    [2, 2]
                ]
            };
            testChart.addSeries(testTwoDotsData);
            testChart.redraw();
            expect(getAllElementsBySelector('.circle', renderTo).length).toBe(2);
        });
    });
});

