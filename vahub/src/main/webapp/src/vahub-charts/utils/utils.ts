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
import {
    PlotLine,
    PlotData, PreformedPlotData
} from '../types/interfaces';
import {Chart} from '../Chart';
import {Axis} from '../axis';

export const MARGINS = {top: 20, right: 20, bottom: 25, bottomWithRotatedLabels: 98, left: 60};
export const MARGINS_INVERTED = {top: 20, right: 20, bottom: 20, bottomWithRotatedLabels: 80, left: 80};
export const MARGINS_TIMELINE = {top: 0, right: 0, bottom: 0, bottomWithRotatedLabels: 0, left: 50};
export const MARGINS_LINE_TIMELINE = {top: 10, right: 0, bottom: 10, bottomWithRotatedLabels: 10, left: 50};
export const MAX_TICK_VALUE = 40;
export const MAX_LABEL_LENGTH = 20;
export const SYMBOL_WIDTH = 6;
export const SHORT_SYMBOL_WIDTH = 3;
export const CAPITALIZED_SYMBOL_WIDTH = 7;
export const LABEL_MARGIN_IN_SYMBOLS = 1;
export const MAX_ROTATED_LABEL_LENGTH = 20;
export const HEAT_MAP_CHART_MARGINS = {top: 10, right: 20, bottom: 60, left: 90};
export const BAR_LINE_MARGINS = {left: 30, bottomReverse: 50, bottom: 25, right: 40};
export const DATA_TYPES = {
    arearange: 'arearange',
    scatter: 'scatter',
    point: 'point',
    boxplot: 'boxplot',
    simpleLine: 'simple-line',
    errorbar: 'errorbar',
    line: 'line',
    column: 'barline'
};

export const PLOT_COLORS = {
    lineColor: '#e4e4e4',
    axisLabelColor: '#666',
    selectionZoneColor: 'rgba(46, 204, 113, 0.3)',
    tooltipTransparentBackground: 'rgba(255,255,255,0.8)',
    selectedBarColor: '#cccccc',
    horizontalTicks: '#e6e6e6',
    xAxis: '#ccd6eb',
    baseLine: '#2c3e50',
    stem: '#95a5a6',
    whisker: '#000000',
    axisLabels: '#333333',
    tooltipBorderColor: '#1e90ff',
    brushSelection: 'grey'
};
export const LOG_SCALE_TICKS = [0.5, 0.2, 0.1];

export const selectionProperties = {
    selected: false,
    select: function(selected) {
        this.selected = selected;
    }
};

export const groupBy = key => array =>
    array.reduce((objectsByKeyValue = {}, obj) => {
        const value = obj[key];
        objectsByKeyValue[value] = (objectsByKeyValue[value] || []).concat(obj);
        return objectsByKeyValue;
    }, []);

export const getCategoricalAxisValues = (categories: string[], width: number, maxTickValue: number = MAX_TICK_VALUE) => {
    if (categories.length > maxTickValue) {
        const divider = Math.round(categories.length * 1000 / (maxTickValue * width));
        return categories.filter((d, i) => {
            return !(i % divider); });
    }
    return categories;
};

export const moveTicks = (ticks, bandSize) => {
    ticks.each(function(d, index) {
        const tick = d3.select(this);
        tick.attr('transform', `translate(${(index + 1) * bandSize}, 0)`);
        const text = tick.select('text');
        const transform = text.attr('transform');
        text.attr('transform', `translate(${- bandSize / 2}, 0) ${transform}`);
    });
};

export const getMaxNotRotatedTicks = (width) => Math.floor(width / (MAX_LABEL_LENGTH * SYMBOL_WIDTH));

export const rotatedLabelsMargin = (
    axis: Axis, scale: d3.scaleType, axisScale: d3.axisType, width?: number, areLabelsCapitalized = false
): number => {
    if (axis.isCategorical) {
        const texts = axisScale.tickValues();
        if (texts.length > 1) {
            let maxWidth = 0;
            const symbolWidth = areLabelsCapitalized ? CAPITALIZED_SYMBOL_WIDTH : SYMBOL_WIDTH;
            texts.forEach((text) => {
                text = String(text);
                const shortSymbolsNumber = (text.match(/[.,\-\s]/g) || []).length;
                const newWidth = (text.length - shortSymbolsNumber + LABEL_MARGIN_IN_SYMBOLS) * symbolWidth + shortSymbolsNumber * SHORT_SYMBOL_WIDTH;
                maxWidth = maxWidth < newWidth ? newWidth : maxWidth;
            });
            if (maxWidth > scale(texts[1]) - scale(texts[0])) {     // if the biggest label can't fit into ont tick
                if (width && texts.length <= getMaxNotRotatedTicks(width)) {    // if the labels should be wrapped but shouldn't be rotated
                    return 1;   // minimum truthy value for wrap to work without extra margin
                }
                const maximumLabelHeight = MAX_ROTATED_LABEL_LENGTH * symbolWidth;
                maxWidth = maxWidth > maximumLabelHeight ? maximumLabelHeight : maxWidth;
                return maxWidth * Math.sin(45 * Math.PI / 180);
            }
        }
    }
    return 0;
};

export const unwrap = (texts): void => {
    texts.each(function() {
        d3.select(this)
            .style('text-anchor', 'middle')
            .attr('dx', '0')
            .attr('transform', 'rotate(0)');
    });
};

export const wrap = (width: number, shift?: {x: number, y: number}, ticks?: string[]) => (texts) => {
    if (!width) {
        unwrap(texts);
        return;
    }
    const maxTicks = getMaxNotRotatedTicks(width);
    texts.each(function() {
        const text = d3.select(this),
            y = text.attr('y'),
            dy = parseFloat(text.attr('dy'));
        const allText = text.text();
        const maxLabelLength = texts._groups[0].length > maxTicks
            ? MAX_ROTATED_LABEL_LENGTH
            : MAX_LABEL_LENGTH;
        if (allText.length > maxLabelLength) {
            const ellips = allText.slice(0, maxLabelLength) + '...';
            text.text(null).append('tspan').attr('x', 0).attr('y', y).attr('dy', dy + 'em').text(ellips);
            text.append('title').attr('x', 0).attr('y', y).attr('dy', `${dy}em`).text(allText);
        } else {
            text.text(null).append('tspan').attr('x', 0).attr('y', y).attr('dy', dy + 'em').text(allText);
        }
        if ((ticks ? ticks.length : texts._groups[0].length) > maxTicks) {
            const transform = shift ? `rotate(-45) translate(${shift.x},${shift.y})` : 'rotate(-45)';
            text
                .style('text-anchor', 'end')
                .attr('dx', '2px')
                .style('cursor', 'default')
                .style('font-size', '11px')
                .attr('transform', transform);
        } else {
            text
                .style('text-anchor', 'middle')
                .attr('dx', '0')
                .attr('transform', 'rotate(0)')
                .style('font-size', '11px')
                .style('cursor', 'default');
        }
    });
};

export const splitLabels = (width) => (texts) => {
    const ticksMargin = 10;
    texts.each(function() {
        const text = d3.select(this),
            dy = parseFloat(text.attr('dy'));
        const allText = text.text().split(' ');
        text.text(null).append('tspan').attr('x', -width).attr('dy', dy + 'em').text(allText[0]).style('text-anchor', 'start');
        text.append('tspan').attr('x', -ticksMargin).attr('dy', '0').text(allText[1]);
    });
};

export const getShape = (shape) => {
    switch (shape) {
        case 'triangle':
            return d3.symbol().type(d3.symbolTriangle);
        case 'diamond':
            return d3.symbol().type(d3.symbolDiamond);
        case 'star':
            return d3.symbol().type(d3.symbolStar);
        case 'square':
            return d3.symbol().type(d3.symbolSquare);
        default:
            return d3.symbol().type(d3.symbolCircle);
    }
};

export const getTicksForColumnRange = (categories: string[], domain: [number, number],  height: number) => {
    const [min, max] = domain;
    const zoomedCategories = categories.slice(min, max + 1);
    const filteredCategories = getCategoricalAxisValues(zoomedCategories, height);
    const uniqueCategories = filteredCategories.filter((el, i, a) => i === a.indexOf(el));
    const showTicks = [];
    uniqueCategories.forEach((category) => {
        showTicks.push(categories.indexOf(category));
    });
    return showTicks;
};

export const splitAxisLabel = (label: string, chartHeight: number): string[] => {
    const maxLabelWidth = Math.floor(chartHeight / SYMBOL_WIDTH);
    if (label.length < maxLabelWidth) {
        return [label];
    }
    const labelWords = label.split(' ');
    const labelLines = [''];
    labelWords.forEach(word => {
        const i = labelLines.length - 1;
        if (labelLines[i].length + word.length < maxLabelWidth) {
            labelLines[i] += ` ${word}`;
        } else {
            labelLines.push(word);
        }
    });
    return labelLines;
};

export const getSelectionRequest = (event, selectionCords, chartX, chartY, originalEvent = null): d3.BrushEvent | MouseEvent => {
    return Object.assign(event, {
        ...selectionCords,
        chartX,
        chartY,
        originalEvent: originalEvent || event
    });
};

export const preformData = (options, categories = null): PreformedPlotData => {
    let dataMax = 0, dataMin = Infinity;
    const data: PlotData[] = [];
    switch (options.type) {
        case DATA_TYPES.line:
            options.data.forEach(d => {
                const point = {
                    ...d,
                    color: d.color || options.color,
                    point: d,
                    series: {
                        name: options.name
                    },
                    category: categories && categories.length ? categories[d.x] : d.x,
                    marker: {...options.marker},
                    isActive: false,
                    setIsActive: function (isActive) {
                        this.isActive = isActive;
                    },
                    ...selectionProperties
                };
                if (d.marker) {
                    point.marker.lineColor = d.marker.lineColor;
                }
                data.push(point);
                dataMax = d.y > dataMax ? d.y : dataMax;
                dataMin = d.y < dataMin ? d.y : dataMin;
            });
            break;
        case DATA_TYPES.arearange:
            options.data.forEach(d => {
                data.push({
                    x: d[0],
                    category: categories && categories.length ? categories[d[0]] : d[0],
                    yMin: d[1],
                    yMax: d[2]
                });
            });
            break;
        case DATA_TYPES.point:
            options.data.forEach(d => {
                data.push({
                    x: d[0],
                    y: d[1],
                    color: options.color,
                    ...selectionProperties
                });
                dataMax = d[1] > dataMax ? d[1] : dataMax;
                dataMin = d[1] < dataMin ? d[1] : dataMin;
            });
            break;
        case DATA_TYPES.simpleLine:
            options.data.forEach(d => {
                const point = {
                    ...d,
                    point: d,
                    series: {
                        name: options.name
                    },
                    isActive: false,
                    setIsActive: function (isActive) {
                        this.isActive = isActive;
                    },
                    ...selectionProperties
                };
                data.push(point);
                dataMax = d.y > dataMax ? d.y : dataMax;
                dataMin = d.y < dataMin ? d.y : dataMin;
            });
            break;
        case DATA_TYPES.scatter:
            options.data.forEach(d => {
                data.push({
                    color: options.color,
                    series: {
                        name: null
                    },
                    category: categories && categories.length ? categories[d.x] : d.x,
                    name: options.name,
                    xrank: d.x,
                    ...d,
                    ...selectionProperties
                });
                dataMax = d.y > dataMax ? d.y  : dataMax;
                dataMin = d.y  < dataMin ? d.y : dataMin;
            });
            break;
        case DATA_TYPES.boxplot:
            options.data.forEach(d => {
                data.push({
                    color: options.color,
                    series: {
                        name: null
                    },
                    category: d.x,
                    name: options.name,
                    ...d,
                    x: d.xrank,
                });
                dataMax = d.upperWhisker > dataMax ? d.upperWhisker : dataMax;
                dataMin = d.lowerWhisker < dataMin ? d.lowerWhisker : dataMin;
            });
            break;
        default:
            data.push(...options.data);
    }
    return { data, dataMin, dataMax, type: options.type };
};

export const getLogarithmicTopBorder = (border: number): number => {
    let order = Math.log10(border);
    if (order > 0 || order < -1) {
        order = Math.ceil(order);
    } else {
        order = 0;
    }
    let resultBorder = 10 ** order;
    LOG_SCALE_TICKS.forEach(tick => {
        if (border / (10 ** order) < tick) {
            resultBorder = tick * (10 ** order);
        }
    });
    return resultBorder;
};

export const formatLabels = (label: string, plotLines: PlotLine[] = null): string => {
    const labelValue = Math.round(parseFloat(label) * 1000) / 1000;
    if (plotLines) {
        const correspondingLine = plotLines.find(plotLine => labelValue === plotLine.value && Boolean(plotLine.formatLabel));
        if (correspondingLine) {
            return correspondingLine.formatLabel(labelValue);
        }
    }
    return labelValue.toString();
};

export const getReversedScaledValue = (axis: Axis, scale: d3.scaleType, coordinate: number, areScaleAndDomainReversed = false) => {
    if (axis.isCategorical) {
        const reversedScale = areScaleAndDomainReversed
            ? d3.scaleQuantize().domain(scale.range().reverse()).range(scale.domain().reverse())
            : d3.scaleQuantize().domain(scale.range()).range(scale.domain());
        return axis.categories.indexOf(reversedScale(coordinate));
    }
    return scale.invert(coordinate);    // linear or logarithmic if not categorical
};

export const getScaledCoordinate = (
    axis: Axis, scale: d3.scaleType, limits: number[], chartSize: number,
    coordinate: number, category: string = null, isLimitsReversed = false
): number => {
    if (axis.isCategorical) {
        if (category && !isNaN(scale(category))) {
            return scale(category) + scale.bandwidth() / 2;
        }
        if (coordinate >= limits[1]) {
            return isLimitsReversed
                ? (Math.floor(limits[1]) - coordinate) * scale.bandwidth()
                : chartSize + (coordinate - Math.floor(limits[1])) * scale.bandwidth();
        }
        if (coordinate <= limits[0]) {
            return isLimitsReversed
                ? chartSize + (-coordinate + Math.floor(limits[0])) * scale.bandwidth()
                : - (Math.floor(limits[0]) + coordinate) * scale.bandwidth();
        }
    }
    if (limits[0] === limits[1]) {
        const aboveLimits = chartSize + scale(Math.abs(coordinate - limits[1]));
        const belowLimits = - Math.abs(scale(Math.abs(coordinate - limits[0])));
        if (coordinate > limits[1]) {
            return isLimitsReversed ? belowLimits : aboveLimits;
        }
        if (coordinate < limits[0]) {
            return isLimitsReversed ? aboveLimits : belowLimits;
        }
    }
    return scale(coordinate);
};

export const preformPlotLineData = (lines: PlotLine[], chart: Chart): PlotLine[] => {
    return lines.map(line => {
        const { x1, x2, y1, y2, axis, value } = line;
        const preformedLine = {...line};
        const getXCoordinate = (x) => getScaledCoordinate(chart.xAxis[0], chart.xScale, chart.limits.x, chart.width, x);
        const getYCoordinate = (y) =>
            getScaledCoordinate(chart.yAxis[0], chart.yScale, chart.limits.y, chart.height, y, null, true);
        const getX = chart.isInverted ? getYCoordinate : getXCoordinate;
        const getY = chart.isInverted ? getXCoordinate : getYCoordinate;
        if (axis === 'x') {
            preformedLine.y1 = 0;
            preformedLine.y2 = chart.height;
            preformedLine.x1 = getX(value);
            preformedLine.x2 = getX(value);
        } else if (axis === 'y') {
            preformedLine.y1 = getY(value);
            preformedLine.y2 = getY(value);
            preformedLine.x1 = 0;
            preformedLine.x2 = chart.width;
        } else {
            preformedLine.y1 = getY(y1);
            preformedLine.y2 = getY(y2);
            preformedLine.x1 = getX(x1);
            preformedLine.x2 = getX(x2);
        }
        return preformedLine;
    });
};
