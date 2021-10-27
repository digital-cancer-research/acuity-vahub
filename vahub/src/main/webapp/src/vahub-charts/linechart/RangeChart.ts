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
    PLOT_COLORS,
    DATA_TYPES,
    getCategoricalAxisValues,
    splitAxisLabel,
    wrap,
    getSelectionRequest,
    preformData,
    rotatedLabelsMargin
} from '../utils/utils';
import {AbstractLineChart, CIRCLE_DIAMETERS} from './AbstractLineChart';
import {RangePlotPoint, Series, SeriesOptions} from '../types/interfaces';

export const TICK_GAP = 60;
export const Y_AXIS_LABELS_SHIFT = 15;
export const X_AXIS_PADDING = 0.025;
export const SELECTED_LINE_WIDTH_COEFFICIENT = 1.5;
export const MAX_TICK_VALUE = 30;

export class RangeChart extends AbstractLineChart {
    public onZoomX = (borders: number[]): void => {
        if (this.xAxis[0].isCategorical) {
            this.limits.x = [borders[0], borders[1] + 1];
        } else {    // linear if not categorical
            const padding = X_AXIS_PADDING * (borders[1] - borders[0]);
            this.limits.x = [borders[0] - padding, borders[1] + padding];
        }
        this.redraw(this.animateOnZoom);
    }

    protected initSeries(options: SeriesOptions<RangePlotPoint>): Series[] {
        const { data, dataMin, dataMax } = preformData(options, this.xAxis[0].categories);
        const newSeries = [...this.series];
        newSeries.push({
            type: options.type,
            color: options.color,
            fillOpacity: options.fillOpacity,
            data,
            points: data,
            dataMax,
            dataMin
        });
        return newSeries;
    }

    private setXAxis(): void {
        if (this.xAxis[0].isCategorical) {
            this.xScale = d3.scaleBand()
                .domain(this.xAxis[0].getFilteredCategories(this.limits.x))
                .range([0, this.width])
                .paddingOuter([0.25]);
            this.xAxisScale = d3.axisBottom(this.xScale)
                .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width, MAX_TICK_VALUE));
        } else {    // linear if not categorical
            this.xScale = d3.scaleLinear()
                .domain(this.limits.x)
                .range([0, this.width]);
            this.xAxisScale = d3.axisBottom(this.xScale)
                .tickSize(-this.height)
                .ticks(Math.floor(this.width / TICK_GAP));
        }
    }

    private drawInterval (innerSpace: d3.Selection<SVGElement>, data: Series[], animate = false) {
        const intervalPoints = data.reduce((acc, val) => acc.concat(val.data), [])
            .sort((a, b) => a.x - b.x);
        const that = this;
        if (!animate) {
            innerSpace.append('g')
                .attr('id', 'arearange')
                .attr('clip-path', this.clipPathURL)
                .append('path')
                .datum(intervalPoints)
                .attr('fill', data[0].color)
                .attr('opacity', data[0].fillOpacity)
                .attr('stroke', 'none');
        }
        innerSpace.select('#arearange path')
            .transition()
            .duration(animate && this.animationTime)
            .attr('d', d3.area()
                .x((d) => that.getXCoordinate(d))
                .y0((d) => that.getYCoordinate(d.yMin))
                .y1((d) => that.getYCoordinate(d.yMax))
            );
    }

    private reversedScale = (x: number) => {
        const reversedScale = d3.scaleQuantize().domain(this.xScale.range()).range(this.xScale.domain());
        return this.xAxis[0].getFilteredCategories(this.limits.x).indexOf(reversedScale(x));
    }

    public redraw(animate = false): void {
        const that = this;
        const areaSeries = this.series.filter(data => data.type === DATA_TYPES.arearange);
        const lineSeries = this.series.filter(data => data.type === DATA_TYPES.line);

        if (!lineSeries[0].data.length) {
            this.showNoData();
            return;
        }

        let innerSpace, lines, intervals, plotLines;

        this.setXAxis();

        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale);
        const chartHeight = this.height - rotatedTicksMargin;

        this.yScale = d3.scaleLinear()
            .domain(this.limits.y)
            .range([chartHeight, 0]);
        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickSize(-this.width)
            .ticks(Math.floor(chartHeight / TICK_GAP));

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

            lines = innerSpace.select('#lines');
            intervals = innerSpace.select('#intervals');
            plotLines = innerSpace.select('#plot-lines');
        } else {
            this.svg.selectAll('g').remove();
            const yAxisTitle = splitAxisLabel(this.yAxis[0].title, this.height);
            const yAxisCoordinate = - this.margins.left / 2 - (yAxisTitle.length - 1) * Y_AXIS_LABELS_SHIFT;

            innerSpace = this.svg.append('g')
                .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top / 2 + ')')
                .on('mousemove', (e) => this.handleMouseMovement(e, true))
                .on('mouseleave', () => this.clearActivePoint());

            this.addClipPath(innerSpace, chartHeight);

            innerSpace.append('g')      // add yAxis
                .attr('class', 'y-axis')
                .call(this.yAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            innerSpace.append('g')      // add XAxis
                .attr('class', 'x-axis')
                .attr('transform', `translate(0,${chartHeight})`)
                .call(this.xAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            const label = innerSpace.append('g').append('text')
                .attr('class', 'axis-label')
                .attr('x', 0)
                .attr('y', yAxisCoordinate)
                .attr('text-anchor', 'middle')
                .attr('font-size', '12px')
                .attr('color', PLOT_COLORS.axisLabelColor)
                .attr('style', `transform: rotate(-90deg) translate(${- that.height / 2}px)`);

            yAxisTitle.forEach((word, i) => {
                label.append('tspan')
                    .attr('dy', i > 0 ? Y_AXIS_LABELS_SHIFT : 0)
                    .attr('x', 0)
                    .text(word);
            });

            innerSpace.selectAll('path')      // repaint chart borders for export
                .attr('stroke', PLOT_COLORS.lineColor);

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
                        const xMin = this.xAxis[0].isCategorical
                            ? this.reversedScale(event.selection[0][0])
                            : this.xScale.invert(event.selection[0][0]);
                        const xMax = this.xAxis[0].isCategorical
                            ? this.reversedScale(event.selection[1][0])
                            : this.xScale.invert(event.selection[1][0]);
                        const yMin = this.yScale.invert(event.selection[0][1]);
                        const yMax = this.yScale.invert(event.selection[1][1]);
                        const chartX = event.selection[1][0];
                        const chartY = event.selection[1][1];
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
                        innerSpace.select('.brush').call(d3.brush().clear);
                        that.selection(getSelectionRequest(event, selectionCords, chartX, chartY, originalEvent));
                    })
                );

            this.addTooltip();
            plotLines = innerSpace.append('g')
                .attr('id', 'plot-lines');
            intervals = innerSpace.append('g')
                .attr('id', 'intervals')
                .attr('clip-path', this.clipPathURL);
            lines = innerSpace.append('g')
                .attr('id', 'lines')
                .attr('clip-path', this.clipPathURL);
        }

        this.drawPlotLines(plotLines, animate);
        this.drawInterval(intervals, areaSeries, animate);
        lineSeries.forEach((serie, i) => this.drawLineWithPoints(lines, serie, animate, i));

        innerSpace.selectAll('.tick line')      // repaint chart borders for export
            .attr('stroke', PLOT_COLORS.lineColor);

        this.svg.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.width));
    }

    protected enter(source: d3.Selection<HTMLDivElement>, index = 0): void {
        const that = this;
        source.enter()
            .append('circle')
            .attr('class', `marker markers-${index}`)
            .attr('cx', d => this.getXCoordinate(d))
            .attr('cy', d => (this.getYCoordinate(d.y)))
            .attr('r', CIRCLE_DIAMETERS.inactive)
            .attr('fill', d => (d.selected ? d.marker.states.select.fillColor : d.marker.fillColor))
            .attr('pointer-events', 'all')
            .attr('stroke', d => (d.selected ? d.marker.states.select.lineColor : d.marker.lineColor))
            .attr('stroke-width', d => d.marker.lineWidth)
            .on('click', (event, d) => that.handleClickOnElement(event, d));
    }

    public update = (): void => {
        this.svg.selectAll('.marker')
            .attr('r', d => d.isActive ? CIRCLE_DIAMETERS.active : CIRCLE_DIAMETERS.inactive)
            .attr('stroke', d => (d.selected ? d.marker.states.select.lineColor : d.marker.lineColor))
            .attr('stroke-width', d => (d.selected ? SELECTED_LINE_WIDTH_COEFFICIENT * d.marker.lineWidth : d.marker.lineWidth));
    }
}
