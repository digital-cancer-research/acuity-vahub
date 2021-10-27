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

import {ShiftChart} from './ShiftChart';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';
import {PLOT_COLORS} from '../utils/utils';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testData = {
    data: [
        {
            X: 5,
            low: 1,
            high: 10
        }
    ]
};
const testTwoBarData = {
    data: [
        {
            X: 5,
            low: 1,
            high: 10
        }, {
            X: 6,
            low: 1,
            high: 10
        }
    ]
};
const testDataBaseLine = {
    color: 'black',
    x1: 0,
    x2: 10,
    y1: 0,
    y2: 10,
    width: 1,
    styles: {
        fill: 'none',
        'stroke-dasharray': '4,3'
    }

};

describe('Shift chart', function () {
    let testChart;
    beforeEach( function () {
        const testOptions = getTestOptions('errorbar', renderTo, click, select);
        testChart = new ShiftChart(testOptions);
    });

    afterEach( function () {
        d3.selectAll('svg').remove();
    });

    describe('create chart', function () {

        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#svg_test_id');
            expect(svg).not.toBeNull();
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });

        it('should render title', function () {
            const title = renderTo.getElementsByClassName('chart-title');
            expect(title[0].innerHTML).toEqual('test title');
        });
    });

    describe('create line', function () {
        it('should render axis, bar and baseLine', function () {
            testChart.addSeries(testData);
            testChart.addPlotLine(testDataBaseLine);
            testChart.redraw();
            expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
        });
        it('should render axis, bar and baseLine', function () {
            testChart.addSeries(testData);
            testChart.addPlotLine(testDataBaseLine);
            testChart.redraw();
            expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
        });
        it('should render baseLine', function () {
            testChart.addSeries(testData);
            testChart.addPlotLine(testDataBaseLine);
            testChart.redraw();
            expect(getAllElementsBySelector('.plot-line', renderTo).length).toBe(1);
        });
        it('should render one bar', function () {
            testChart.addSeries(testData);
            testChart.addPlotLine(testDataBaseLine);
            testChart.redraw();
            expect(getAllElementsBySelector('.shift-bar', renderTo).length).toBe(1);
        });
        it('should render bar with expected color', function () {
            testChart.addSeries(testData);
            testChart.addPlotLine(testDataBaseLine);
            testChart.redraw();
            const barStemColor = d3.select(renderTo).select(`.shift-bar`).select('.error-stem').attr('stroke');
            expect(barStemColor).toEqual(`${PLOT_COLORS.baseLine}`);

            const barTopColor = d3.select(renderTo).select(`.shift-bar`).select('.error-top').attr('stroke');
            expect(barTopColor).toEqual(`${PLOT_COLORS.whisker}`);

            const barBottomColor = d3.select(renderTo).select(`.shift-bar`).select('.error-bottom').attr('stroke');
            expect(barBottomColor).toEqual(`${PLOT_COLORS.whisker}`);

        });

        it('should render two bar', function () {
            testChart.addSeries(testTwoBarData);
            testChart.addPlotLine(testDataBaseLine);
            testChart.redraw();
            expect(getAllElementsBySelector('.shift-bar', renderTo).length).toBe(2);
        });
    });
});
