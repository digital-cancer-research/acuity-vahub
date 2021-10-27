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

import {HeatMapChart} from './HeatMapChart';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');
const testData = {
    name: 'test_name',
    boostThreshold: 50,
    data: [
        {
            color: 'red',
            name: 'test_name',
            value: 'test_value',
            x: 0,
            y: 0
        }
    ]
};

describe('Heat map chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = getTestOptions('heat-map', renderTo, click, select);
        testChart = new HeatMapChart(testOptions);
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

    it('should render xAxis', function () {
        testChart.limits.x = [0, 1];
        testChart.limits.y = [0, 1];
        testChart.addSeries(testData);
        testChart.redraw();
        expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
    });

    it('should render yAxis', function () {
        testChart.limits.x = [0, 1];
        testChart.limits.y = [0, 1];
        testChart.addSeries(testData);
        testChart.redraw();
        expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
    });

    describe('create rects', function () {
        it('should render one rect', function () {
            testChart.addSeries(testData);
            testChart.limits.x = [0, 1];
            testChart.limits.y = [0, 1];
            testChart.redraw();
            expect(getAllElementsBySelector(`.rect`, renderTo).length).toBe(1);
            const rectColor = d3.select(renderTo).select(`.rect`).attr('fill');
            expect(rectColor).toEqual('red');
        });

        it('should render two rects', function () {
            const testTwoRectsData = {
                name: 'test_name',
                boostThreshold: 50,
                data: [
                    {
                        color: 'red',
                        name: 'test_name1',
                        value: 'test_value1',
                        x: 0,
                        y: 0
                    },
                    {
                        color: 'red',
                        name: 'test_name2',
                        value: 'test_value2',
                        x: 1,
                        y: 1
                    }
                ]
            };
            testChart.addSeries(testTwoRectsData);
            testChart.limits.x = [0, 2];
            testChart.limits.y = [0, 2];
            testChart.redraw();
            expect(getAllElementsBySelector(`.rect`, renderTo).length).toBe(2);
        });
    });
});

