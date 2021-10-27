import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';
import {BoxPlot} from './BoxPlot';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');
const testData = {
    type: 'boxplot',
    data: [{
        x: 'Summer',
        xrank: 2,
        outliers: [
            {x: 'Summer', xrank: 2, outlierValue: -24.66, subjectId: 'DummyData-2009589165'},
            {x: 'Summer', xrank: 2, outlierValue: 18.0, subjectId: 'DummyData-9525132641'}
        ],
        eventCount: null,
        lowerQuartile: 3,
        lowerWhisker: 3,
        median: 3,
        subjectCount: 3,
        upperQuartile: 3,
        upperWhisker: 3,
    }]
};

const testData2 = {
    name: 'Outlier',
    zIndex: 1,
    color: '#2c3e50',
    allowPointSelect: false,
    type: 'scatter',
    marker: {
        symbol: 'circle',
        radius: 2
    },
    stickyTracking: false,
    data: [
        {x: 1, y: 2, outlierValue: -24.66, subjectId: 'DummyData-2009589165'},
        {x: 3, y: 2, outlierValue: 18.0, subjectId: 'DummyData-9525132641'}
    ],
};

describe('BoxPlot chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = getTestOptions('boxplot', renderTo, click, select);
        testChart = new BoxPlot(testOptions);
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
    });

    describe('create axis', function () {
        it('should render xAxis', function () {
            testChart.addSeries(testData);
            testChart.redraw();
            const axis = d3.select(renderTo).select('.x-axis');
            expect(axis._groups[0].length).toEqual(1);
        });

        it('should render yAxis', function () {
            testChart.addSeries(testData);
            testChart.redraw();
            const axis = d3.select(renderTo).select('.y-axis');
            expect(axis._groups[0].length).toEqual(1);
        });
    });

    describe('render data', function () {
        it('should render dots', function () {
            testChart.addSeries(testData2);
            testChart.redraw();
            expect(getAllElementsBySelector('.circle', renderTo).length).toBe(2);
            const dotColor = d3.select(renderTo).select(`.circle`).attr('fill');
            expect(dotColor).toEqual('#2c3e50');
        });

        it('should render boxes', function () {
            testChart.addSeries(testData);
            testChart.redraw();
            expect(getAllElementsBySelector('.box', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.lowerWhisker', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.upperWhisker', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.rect', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.median', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.center', renderTo).length).toBe(1);
        });
    });
});
