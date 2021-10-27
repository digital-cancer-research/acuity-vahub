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

