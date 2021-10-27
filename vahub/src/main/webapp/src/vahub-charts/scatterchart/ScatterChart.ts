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
import {
    ChartMouseEvent,
    ScatterPlotData,
    Series,
    SeriesOptions,
    UserOptions,
    ZoneSelection
} from '../types/interfaces';
import {PLOT_COLORS, getSelectionRequest, preformData, getScaledCoordinate} from '../utils/utils';

export const TICK_GAP = 60;
export const X_AXIS_LABEL_POSITIONING = 30;
export const Y_AXIS_LABELS_SHIFT = 30;

export class ScatterChart extends Chart {
    public axis = {};

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
        this.redrawSelectionZones(this.getXCoordinate, this.getYCoordinate);
    }

    protected initSeries(options: SeriesOptions<ScatterPlotData>): Series[] {
        const { data, dataMin, dataMax } = preformData(options);
        const newSeries = [...this.series];
        newSeries.push({
            data,
            points: data,
            dataMax,
            dataMin
        });
        return newSeries;
    }

    public update = (): void => {
        this.svg.selectAll('.circle')
            .attr('fill', d => (d.selected ? 'gray' : d.color));
    }

    public getXCoordinate = (x: number): number => {
        return getScaledCoordinate(this.xAxis[0], this.xScale, this.limits.x, this.width, x);
    }

    public getYCoordinate = (y: number): number => {
        return getScaledCoordinate(this.yAxis[0], this.yScale, this.limits.y, this.height, y, null, true);
    }

    private drawAxisLabels(container: d3.Selection<SVGElement>): void {
        container.append('text')
            .attr('class', 'axis-label')
            .attr('x', this.width / 2)
            .attr('y', this.height + X_AXIS_LABEL_POSITIONING)
            .attr('text-anchor', 'middle')
            .attr('font-size', '12px')
            .attr('color', PLOT_COLORS.axisLabelColor)
            .text(this.xAxis[0].title);

        container.append('text')
            .attr('class', 'axis-label')
            .attr('x', 0)
            .attr('y', -Y_AXIS_LABELS_SHIFT)
            .attr('text-anchor', 'middle')
            .attr('font-size', '12px')
            .attr('color', PLOT_COLORS.axisLabelColor)
            .attr('style', `transform: rotate(-90deg) translate(${- this.height / 2}px)`)
            .text(this.yAxis[0].title);
    }

    public redraw(animate = false): void {
        const series: ScatterPlotData[] = this.series.reduce((acc, val) => acc.concat(val.data), []);
        const that = this;
        let innerSpace, plotLinesContainer, selectedZonesContainer = null;

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
            .tickSize(-this.height)
            .ticks(Math.floor(this.width / TICK_GAP));
        d3.selectAll('.tick line')
            .attr('stroke', PLOT_COLORS.lineColor);

        if (animate) {
            innerSpace = this.svg.select('g');
            plotLinesContainer = innerSpace.select('.plot-lines');
            innerSpace.select('.y-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.yAxisScale);
            innerSpace.select('.x-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.xAxisScale);
        } else {
            this.svg.selectAll('g').remove();

            innerSpace = this.svg.append('g')
                .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top / 2 + ')');

            this.addClipPath(innerSpace);

            innerSpace.append('g')      // add yAxis
                .attr('class', 'y-axis')
                .call(this.yAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            innerSpace.append('g')      // add XAxis
                .attr('class', 'x-axis')
                .attr('transform', `translate(0,${this.height})`)
                .call(this.xAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            innerSpace.selectAll('path')      // repaint chart borders for export
                .attr('stroke', PLOT_COLORS.lineColor);

            plotLinesContainer = innerSpace.append('g')
                .attr('class', 'plot-lines')
                .attr('clip-path', this.clipPathURL);

            const axisLabels: d3.Selection<SVGElement> = innerSpace.append('g');   // axis labels
            this.drawAxisLabels(axisLabels);

        this.addTooltip();
        this.tooltipContainer.style('transition', 'opacity .5s');

            innerSpace.append('g')      // Add the brush feature
                .attr('class', 'brush')
                .attr('id', 'brush_' + this.id)
                .call(d3.brush()
                    .extent([[0, 0], [this.width, this.height]])
                    .filter(() => true) //reset filter that filters out events with ctrl button by default
                    .on('end', (event: ChartMouseEvent) => {
                        if (!event.selection) {
                            that.removeSelection();
                            this.update();
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
                                }
                            ],
                            yAxis: [
                                {
                                    min: Math.min(yMin, yMax),
                                    max: Math.max(yMin, yMax),
                                }
                            ]
                        };
                        const originalEvent: ChartMouseEvent = {
                            ...event,
                            offsetX: chartX,
                            offsetY: chartY,
                            ctrlKey: event.sourceEvent.ctrlKey,
                        };
                        that.selection(getSelectionRequest(event, selectionCords, chartX, chartY, originalEvent));
                        innerSpace.select('.brush').call(d3.brush().clear);
                        this.setSelectedZone({
                            xMin: selectionCords.xAxis[0].min,
                            xMax: selectionCords.xAxis[0].max,
                            yMin: selectionCords.yAxis[0].min,
                            yMax: selectionCords.yAxis[0].max
                        }, event.originalEvent.ctrlKey);
                    })
                );

            selectedZonesContainer = innerSpace.append('g')
                .attr('class', 'selection-zones')
                .attr('clip-path', this.clipPathURL);

            innerSpace.append('g')
                .attr('id', 'dots')
                .attr('clip-path', this.clipPathURL);
        }

        this.drawPlotLines(plotLinesContainer, animate);

        this.redrawSelectionZones(this.getXCoordinate, this.getYCoordinate, selectedZonesContainer, animate);

        this.drawDots(series, animate);
    }

    private drawDots (data: ScatterPlotData[], animate: boolean) {
        const dots = this.svg.select('#dots')
            .selectAll('.circle')
            .data(data);
        dots.exit().remove();
        const that = this;
        if (animate) {
            dots.transition()
                .duration(this.animationTime)
                .attr('cx', d => this.getXCoordinate(d.x))
                .attr('cy', d => this.getYCoordinate(d.y));
        }
        dots.enter()
            .append('circle')
            .attr('class', 'circle')
            .attr('cx', d => (this.getXCoordinate(d.x)))
            .attr('cy', d => (this.getYCoordinate(d.y)))
            .attr('r', 3)
            .attr('fill', d => (d.selected ? 'gray' : d.color))
            .attr('pointer-events', 'all')
            .attr('stroke', d => d.color)
            .attr('stroke-width', 0)
            .attr('stroke-opacity', 0.4)
            .on('mouseover', function (event, d) {
                d3.select(this).style('opacity', .7)
                    .style('stroke-width', 8);
                that.handleMouseOver(event, d);
                clearTimeout(that.tooltipTimeout);
            })
            .on('mouseout', function () {
                d3.select(this).style('opacity', 1)
                    .style('stroke-width', 0);
                that.tooltipTimeout = window.setTimeout(that.handleMouseOut, 1500);
            });
    }
    public clearSeries(): void {
        super.clearSeries();
        this.axis = {};
    }
}
