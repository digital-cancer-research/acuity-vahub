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
import {Chart} from '../Chart';
import {BoxPlotBoxItem, Series, SeriesOptions, UserOptions, ZoneSelection} from '../types/interfaces';
import {
    PLOT_COLORS,
    DATA_TYPES,
    MARGINS,
    getSelectionRequest,
    preformData,
    getCategoricalAxisValues,
    splitAxisLabel,
    getReversedScaledValue,
    wrap,
    getScaledCoordinate,
    rotatedLabelsMargin
} from '../utils/utils';
import {ScaleTypes} from '../types/types';
import {Y_AXIS_LABELS_SHIFT} from '../linechart/RangeChart';

export const TICK_GAP = 60;
export const Y_TICK_GAP = 30;
export const DOT_RADIUS = 2;
export const BIG_DOT_RADIUS = 10;
export const X_AXIS_LABEL_POSITIONING = 30;
export const BOX_FILL_COLOR = 'white';
export const BOLD_STROKE_WIDTH = 2;
export const BOX_RATIO = 0.6;
export const X_AXIS_PADDING = 0.5;
export const MAX_TICK_VALUE = 30;

export class BoxPlot extends Chart {
    public axis = {};
    public margins = {...MARGINS, left: 80, bottomWithRotatedLabels: 30};

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    public setSelectedZone = (zone: ZoneSelection, isAdding: boolean = false): void => {
        if (!zone) {
            this.selectionZones = [];
        } else {
            if (!isAdding) {
                this.selectionZones = [];
            }
            this.selectionZones.push(zone);
        }
        this.redrawSelectionZones(this.getSelectedZoneX, this.getY, null, false, this.xAxisBandwidth);
    }

    protected initSeries(options: SeriesOptions<BoxPlotBoxItem>): Series[] {
        const newSeries = [...this.series];
        const {data, dataMin, dataMax, type} = preformData(options, this.xAxis[0].categories);
        newSeries.push({
            data,
            dataMin,
            dataMax,
            type,
            points: data
        });
        return newSeries;
    }

    public onZoomX = (borders: number[]) => {
        if (this.xAxis[0].isCategorical) {
            this.limits.x = borders;
        } else {
            this.limits.x = [borders[0] - X_AXIS_PADDING, borders[1] + X_AXIS_PADDING];
        }
        this.redraw(this.animateOnZoom);
    }

    public update = (): void => {
        this.svg.selectAll('.circle')
            .attr('fill', d => (d.selected ? 'gray' : d.color));
    }

    private getYScale = (chartHeight: number): d3.scaleType => {
        switch (this.yAxis[0].type) {
            case ScaleTypes.LOGARITHMIC_SCALE:
                return d3.scaleLog()
                    .domain(this.limits.y)
                    .range([chartHeight, 0]);
            case ScaleTypes.LINEAR_SCALE:
            default:
                return d3.scaleLinear()
                    .domain(this.limits.y)
                    .range([chartHeight, 0]);
        }
    }

    private setYAxis(chartHeight: number): void {
        this.yScale = this.getYScale(chartHeight);
        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickSize(-this.width)
            .ticks(Math.floor(this.height / Y_TICK_GAP))
            .tickFormat(d => d);
        if (this.limits.y[0] === this.limits.y[1]) {
            this.yAxisScale.tickFormat(d3.format('.3n'));
        }
    }

    private setXAxis(): void {
        if (this.xAxis[0].isCategorical) {
            this.xScale = d3.scaleBand()
                .domain(this.xAxis[0].getFilteredCategories(this.limits.x))
                .range([0, this.width]);

            this.xAxisScale = d3.axisBottom(this.xScale)
                .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width, MAX_TICK_VALUE));
        } else {
            this.xScale = d3.scaleLinear()
                .domain(this.limits.x)
                .range([0, this.width]);

            this.xAxisScale = d3.axisBottom(this.xScale)
                .ticks(Math.floor(this.width / TICK_GAP));
        }
    }

    private getX = (d): number => {
        let coordinate = d.x;
        if (this.xAxis[0].isCategorical) {
            coordinate = this.xAxis[0].categories.indexOf(d.category);
        }
        return getScaledCoordinate(this.xAxis[0], this.xScale, this.limits.x, this.width, coordinate, d.category);
    }

    private getY = (y: number): number => {
        return getScaledCoordinate(this.yAxis[0], this.yScale, this.limits.y, this.height, y, null, true);
    }

    private getSelectedZoneX = (d): number => {
        if (this.xAxis[0].isCategorical) {
            if (d < this.limits.x[0]) {
                return 0;
            } else if (d > this.limits.x[1]) {
                return this.width;
            }
            return this.xScale(this.xAxis[0].categories[d]);
        }
        return this.xScale(d);
    }

    private get xAxisBandwidth() {
        return (this.xAxis[0] && this.xAxis[0].isCategorical) ? this.xScale.bandwidth() : 0;
    }

    private drawAxisLabels(container: d3.Selection<SVGElement>): void {
        const yAxisTitle = splitAxisLabel(this.yAxis[0].title, this.height);
        const yAxisCoordinate = -this.margins.left / 2 - (yAxisTitle.length - 1) * Y_AXIS_LABELS_SHIFT;
        const label = container.append('g').append('text')
            .attr('class', 'axis-label')
            .attr('x', 0)
            .attr('y', yAxisCoordinate)
            .attr('text-anchor', 'middle')
            .attr('font-size', '12px')
            .attr('color', PLOT_COLORS.axisLabelColor)
            .attr('style', `transform: rotate(-90deg) translate(${-this.height / 2}px)`);

        yAxisTitle.forEach((word, i) => {
            label.append('tspan')
                .attr('dy', i > 0 ? Y_AXIS_LABELS_SHIFT : 0)
                .attr('x', 0)
                .text(word);
        });
        container.append('text')
            .attr('class', 'axis-label')
            .attr('x', this.width / 2)
            .attr('y', this.height + X_AXIS_LABEL_POSITIONING)
            .attr('text-anchor', 'middle')
            .attr('font-size', '12px')
            .attr('color', PLOT_COLORS.axisLabelColor)
            .text(this.xAxis[0].title);
    }

    public redraw(animate = false): void {
        if (this.noDataMessage) {
            this.showNoData();
            return;
        }
        const dotsData = this.series.filter(data => data.type === DATA_TYPES.scatter).reduce((acc, val) => acc.concat(val.data), []);
        const boxesData = this.series.filter(data => data.type === DATA_TYPES.boxplot).reduce((acc, val) => acc.concat(val.data), []);
        const that = this;
        let innerSpace, selectedZonesContainer = null;
        this.setXAxis();
        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale);
        const chartHeight = this.height - rotatedTicksMargin;
        this.setYAxis(chartHeight);

        if (animate) {
            innerSpace = this.svg.select('g');
            innerSpace.select('.y-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.yAxisScale);
            innerSpace.select('.x-axis')
                .transition()
                .duration(this.animationTime)
                .attr('transform', `translate(0,${chartHeight})`)
                .call(this.xAxisScale);
            this.resizeClipPath(chartHeight);
        } else {
            this.svg.selectAll('g').remove();

            innerSpace = this.svg.append('g')
                .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top / 2 + ')');

            this.addClipPath(innerSpace, chartHeight);

            innerSpace.append('g')
                .attr('class', 'y-axis no-axis-line')
                .call(this.yAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            innerSpace.select('.no-axis-line')
                .select('path')
                .attr('stroke-width', '0');

            innerSpace.append('g')
                .attr('class', 'x-axis')
                .attr('transform', `translate(0,${chartHeight})`)
                .call(this.xAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            this.addTooltip();

            innerSpace.append('g')      // Add the brush feature
                .attr('class', 'brush')
                .attr('id', 'brush_' + this.id)
                .call(d3.brush()
                    .extent([[0, 0], [this.width, chartHeight]])
                    .filter(() => true) //reset filter that filters out events with ctrl button by default
                    .on('end', (event) => {
                        if (!event.selection) {
                            that.removeSelection();
                            this.update();
                            return;
                        }
                        const xMin = getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[0][0]);
                        const xMax = getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[1][0]);
                        const yMin = getReversedScaledValue(this.yAxis[0], this.yScale, event.selection[0][1]);
                        const yMax = getReversedScaledValue(this.yAxis[0], this.yScale, event.selection[1][1]);
                        const chartX = event.selection[1][0] + that.margins.left;
                        const chartY = event.selection[1][1] + that.margins.top;

                        const selectionCords = {
                            xAxis: [
                                {
                                    min: Math.min(xMin, xMax),
                                    max: Math.max(xMin, xMax),
                                }
                            ],
                            yAxis: [
                                {
                                    min: Math.min(yMin, yMax),
                                    max: Math.max(yMin, yMax),
                                }
                            ]
                        };
                        const originalEvent = {
                            ...event,
                            offsetX: chartX,
                            offsetY: chartY,
                            ctrlKey: event.sourceEvent.ctrlKey,
                        };
                        that.selection(getSelectionRequest(event, selectionCords, chartX, chartY, originalEvent));
                        innerSpace.select('.brush').call(d3.brush().clear);
                        this.setSelectedZone({
                            xMin,
                            xMax,
                            yMin: yMax,
                            yMax: yMin
                        }, event.originalEvent.ctrlKey);
                    })
                );
            innerSpace.selectAll('path')      // repaint chart borders for export
                .attr('stroke', PLOT_COLORS.lineColor);

            const axisLabels = innerSpace.append('g');   // axis labels
            this.drawAxisLabels(axisLabels);

            selectedZonesContainer = innerSpace.append('g')
                .attr('class', 'selection-zones')
                .attr('clip-path', this.clipPathURL);

            innerSpace.append('g')
                .attr('id', 'dots')
                .attr('clip-path', this.clipPathURL);

            innerSpace.append('g')
                .attr('id', 'boxes')
                .attr('clip-path', this.clipPathURL);

            innerSpace.on('mouseout', () => {
                this.handleMouseOut();
            });

            innerSpace.append('g').attr('class', 'line-container');
        }

        innerSpace.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.width));

        this.redrawSelectionZones(this.getSelectedZoneX, this.getY, selectedZonesContainer, animate, this.xAxisBandwidth);
        this.drawPlotLines(innerSpace.select('.line-container'), animate);

        this.drawDots(dotsData, animate);
        this.drawBoxes(boxesData, animate);
    }

    protected drawDots(dotsData, animate: boolean) {
        this.svg.selectAll('.circle')
            .data(dotsData)
            .exit()
            .remove();
        const dots = this.svg.select('#dots')
            .selectAll('.circle')
            .data(dotsData);
        const that = this;
        dots.enter()
            .append('circle')
            .attr('class', 'circle')
            .attr('cx', d => this.getX(d))
            .attr('cy', d => this.getY(d.y))
            .attr('r', DOT_RADIUS)
            .attr('fill', d => (d.color))
            .attr('stroke', d => d.color)
            .attr('stroke-width', 0)
            .attr('stroke-opacity', 0.4)
            .on('mouseover', function (event, d) {
                that.handleMouseOver(event, d);
                d3.select(this).style('opacity', .7)
                    .style('stroke-width', BIG_DOT_RADIUS);
            })
            .on('mouseout', function () {
                d3.select(this).style('opacity', 1)
                    .style('stroke-width', 0);
                that.handleMouseOut();
            });

        dots
            .transition()
            .duration(animate && this.animationTime)
            .attr('cx', d => this.getX(d))
            .attr('cy', d => this.getY(d.y));
    }

    protected drawBoxes(boxesData, animate: boolean) {
        const width = this.xAxis[0].isCategorical
            ? this.xScale.bandwidth() * BOX_RATIO
            : (this.xScale(1) - this.xScale(0)) * BOX_RATIO;
        const halfWidth = width / 2;
        const quaterWidth = width / 4;

        this.svg.select('#boxes')
            .selectAll('.box')
            .data(boxesData)
            .enter()
            .append('g')
            .attr('class', 'box')
            .on('mouseover', (event, d) => this.handleMouseOver(event, d))
            .on('mouseout', this.handleMouseOut);
        const boxes = this.svg.selectAll('#boxes .box');
        boxes.exit().remove();

        this.drawWhisker('lowerWhisker', boxes, quaterWidth, animate);
        this.drawWhisker('upperWhisker', boxes, quaterWidth, animate);
        this.drawCenterLine(boxes, animate);
        this.drawRect(boxes, halfWidth, width, animate);
        this.drawMedian(boxes, halfWidth, animate);
    }

    private drawRect(box, halfWidth: number, width: number, animate = false) {
        if (!animate) {
            box.append('rect')
                .attr('class', 'rect')
                .style('fill', BOX_FILL_COLOR)
                .attr('stroke', d => d.color);
        }
        box.select('.rect')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => this.getX(d) - halfWidth)
            .attr('y', d => this.getY(d.upperQuartile))
            .attr('height', d => this.getY(d.lowerQuartile) - this.getY(d.upperQuartile))
            .attr('width', width);
    }

    private drawMedian(box, halfWidth: number, animate = false) {
        if (!animate) {
            box.append('line')
                .attr('class', 'median')
                .attr('stroke', d => d.color)
                .attr('stroke-width', BOLD_STROKE_WIDTH);
        }
        box.select('.median')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => this.getX(d) - halfWidth)
            .attr('x2', d => this.getX(d) + halfWidth)
            .attr('y1', d => this.getY(d.median))
            .attr('y2', d => this.getY(d.median));
    }

    private drawCenterLine(box, animate = false) {
        if (!animate) {
            box.append('line')
                .attr('class', 'center')
                .attr('stroke', d => d.color);
        }
        box.select('.center')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => this.getX(d))
            .attr('x2', d => this.getX(d))
            .attr('y1', d => this.getY(d.upperWhisker))
            .attr('y2', d => this.getY(d.lowerWhisker));
    }

    private drawWhisker(identifier: string, box, quarterWidth: number, animate = false) {
        if (!animate) {
            box.append('line')
                .attr('class', identifier)
                .attr('stroke', d => d.color)
                .attr('stroke-width', BOLD_STROKE_WIDTH);
        }
        box.select(`.${identifier}`)
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => this.getX(d) - quarterWidth)
            .attr('x2', d => this.getX(d) + quarterWidth)
            .attr('y1', d => this.getY(d[identifier]))
            .attr('y2', d => this.getY(d[identifier]));
    }
}
