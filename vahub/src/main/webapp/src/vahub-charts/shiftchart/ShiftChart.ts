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
import {splitAxisLabel, PLOT_COLORS, getSelectionRequest} from '../utils/utils';
import {Chart} from '../Chart';
import {ErrorBarData, Series, SeriesOptions, UserOptions, ZoneSelection} from '../types/interfaces';

export const XAXIS_LABEL_POSITIONING = 30;
export const TICK_GAP = 30;
export const XAXIS_TICK_GAP = 50;
export const HALF_ERRORBAR_WIDTH = 5;
export const Y_AXIS_LABELS_SHIFT = 11;

export class ShiftChart extends Chart {
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
        this.redrawSelectionZones(this.xScale, this.yScale);
    }

    protected initSeries(options: SeriesOptions<ErrorBarData>): Series[] {
        const newSeries = [];
        let dataMax = -Infinity, dataMin = Infinity;
        const data = [];
        const category = [];
        options.data.forEach(item => {
            data.push({
                ...item,
                category: item.x
            });
            dataMax = item.high > dataMax ? item.high : dataMax;
            dataMin = item.low < dataMin ? item.low : dataMin;
            category.push(item.x);
        });
        newSeries.push({
            data,
            points: data,
            categories: category,
            dataMax,
            dataMin
        });
        return newSeries;
    }

    public redraw(animate = false): void {
        const series: ErrorBarData[] = this.series.reduce((acc, val) => acc.concat(val.data), []);
        const that = this;
        let innerSpace, plotLinesContainer, barsContainer, selectedZonesContainer = null;

        this.yScale = d3.scaleLinear()
            .domain(this.limits.y)
            .range([this.height, 0]);

        this.xScale = d3.scaleLinear()
            .domain(this.limits.x)
            .range([0, this.width]);

        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickSize(-this.width)
            .ticks(Math.floor(this.height / TICK_GAP));

        this.xAxisScale = d3.axisBottom(this.xScale)
            .ticks(Math.floor(this.width / XAXIS_TICK_GAP));

        if (animate) {
            innerSpace = this.svg.select('g');

            innerSpace.select('.y-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.yAxisScale);
            innerSpace.select('.x-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.xAxisScale);
            plotLinesContainer = innerSpace.select('.plot-lines');
            barsContainer = innerSpace.select('#bars');
        } else {
            this.svg.selectAll('g').remove();

            innerSpace = this.svg.append('g')
                .attr('transform', 'translate(' + this.margins.left + ')');

            innerSpace.append('g')
                .attr('class', 'y-axis no-axis-line')
                .call(this.yAxisScale)
                .select('path')
                .attr('stroke-width', '0');

            innerSpace.selectAll('g.tick')
                .select('line')
                .attr('stroke', PLOT_COLORS.horizontalTicks);

            innerSpace.append('g')
                .attr('transform', `translate(0,${this.height})`)
                .attr('class', 'x-axis')
                .call(this.xAxisScale)
                .select('path')
                .attr('stroke', PLOT_COLORS.xAxis);

            const yAxisTitle = splitAxisLabel(this.yAxis[0].title, this.height);
            const yAxisCoordinate = -this.margins.left / 2 - Y_AXIS_LABELS_SHIFT
                * (yAxisTitle.length > 2 ? (yAxisTitle.length - 1) : yAxisTitle.length);

            innerSpace.append('text')
                .attr('class', 'axis-label')
                .attr('x', this.width / 2)
                .attr('y', this.height + XAXIS_LABEL_POSITIONING)
                .attr('text-anchor', 'middle')
                .attr('font-size', '11px')
                .attr('color', PLOT_COLORS.axisLabels)
                .text(this.xAxis[0].title);

            const label = innerSpace.append('g').append('text')
                .attr('class', 'axis-label')
                .attr('x', 0)
                .attr('y', yAxisCoordinate)
                .attr('text-anchor', 'middle')
                .attr('font-size', '11px')
                .attr('color', PLOT_COLORS.axisLabelColor)
                .attr('style', `transform: rotate(-90deg) translate(${-that.height / 2}px)`);

            yAxisTitle.forEach((word, i) => {
                label.append('tspan')
                    .attr('dy', i > 0 ? Y_AXIS_LABELS_SHIFT : 0)
                    .attr('x', 0)
                    .text(word);
            });

            this.addTooltip();
            this.tooltipContainer
                .style('transition', '.25s');

            this.addClipPath(innerSpace);

            innerSpace.append('g')
                .attr('class', 'brush')
                .attr('id', 'brush_' + this.id)
                .call(d3.brush()
                    .extent([[0, 0], [this.width, this.height]])
                    .filter(() => true)
                    .on('end', (event) => {
                        if (!event.selection) {
                            that.setSelectedZone(null);
                            that.redrawSelectionZones(that.xScale, that.yScale);
                            that.removeSelection();
                            return;
                        }
                        const xMin = this.xScale.invert(event.selection[0][0]);
                        const xMax = this.xScale.invert(event.selection[1][0]);
                        const yMin = this.yScale.invert(event.selection[0][1]);
                        const yMax = this.yScale.invert(event.selection[1][1]);
                        const chartX = event.selection[1][0] + that.margins.left;
                        const chartY = event.selection[1][1] + that.margins.top;
                        const selectionCords = {
                            xAxis: [
                                {
                                    min: Math.min(xMin, xMax),
                                    max: Math.max(xMin, xMax),
                                    axis: {
                                        categories: this.series[0].categories
                                    }
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

            plotLinesContainer = innerSpace
                .append('g')
                .attr('class', 'plot-lines');

            selectedZonesContainer = innerSpace.append('g')
                .attr('class', 'selection-zones')
                .attr('clip-path', this.clipPathURL);

            barsContainer = innerSpace.append('g')
                .attr('id', 'bars')
                .attr('clip-path', this.clipPathURL);
        }

        this.redrawSelectionZones(this.xScale, this.yScale, selectedZonesContainer, animate);
        this.drawPlotLines(plotLinesContainer, animate);

        this.drawBars(barsContainer, series, animate);
    }

    private drawBars (container: d3.Selection<SVGElement>, data, animate = false) {
        const that = this;
        const bars = container
            .selectAll('.shift-bar')
            .data(data);
        const newBars = bars.enter()
            .append('g')
            .attr('class', 'shift-bar')
            .on('mouseenter', (event, d) => {
                this.handleMouseOver(event, d);
                clearTimeout(that.tooltipTimeout);
            })
            .on('mouseleave', () => {
                that.tooltipTimeout = window.setTimeout(() => {
                    this.handleMouseOut();
                }, 1500);
            });
        newBars
            .append('line')
            .attr('class', 'error-stem')
            .attr('stroke-width', 1)
            .attr('stroke', PLOT_COLORS.baseLine);
        newBars
            .append('line')
            .attr('class', 'error-top')
            .attr('stroke-width', 1)
            .attr('stroke', PLOT_COLORS.whisker);
        newBars
            .append('line')
            .attr('class', 'error-bottom')
            .attr('stroke-width', 1)
            .attr('stroke', PLOT_COLORS.whisker);

        container.selectAll('.error-stem')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => this.xScale(d.x))
            .attr('x2', d => this.xScale(d.x))
            .attr('y1', d => this.yScale(d.low))
            .attr('y2', d => this.yScale(d.high));
        container.selectAll('.error-top')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => (this.xScale(d.x) - HALF_ERRORBAR_WIDTH))
            .attr('x2', d => (this.xScale(d.x) + HALF_ERRORBAR_WIDTH))
            .attr('y1', d => this.yScale(d.low))
            .attr('y2', d => this.yScale(d.low));
        container.selectAll('.error-bottom')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => (this.xScale(d.x) - HALF_ERRORBAR_WIDTH))
            .attr('x2', d => (this.xScale(d.x) + HALF_ERRORBAR_WIDTH))
            .attr('y1', d => this.yScale(d.high))
            .attr('y2', d => this.yScale(d.high));
    }
}
