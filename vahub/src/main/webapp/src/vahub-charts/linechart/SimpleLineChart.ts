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
    getCategoricalAxisValues,
    getSelectionRequest, PLOT_COLORS,
    preformData, wrap, formatLabels,
    DATA_TYPES, getLogarithmicTopBorder, getReversedScaledValue, rotatedLabelsMargin
} from '../utils/utils';
import {AbstractLineChart, CIRCLE_DIAMETERS, MICROANIMATION_TIME} from './AbstractLineChart';
import {ErrorBarData, Series, UserOptions} from '../types/interfaces';

export const AXIS_PADDING_PRECENTAGE = 0.05;
export const TICK_GAP = 50;
export const DIAMOND_SIZE = 40;

export class SimpleLineChart extends AbstractLineChart {
    public plotWidth: number;
    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.markerSymbol = options.plotOptions ? options.plotOptions.markerSymbol : null;
        this.plotWidth = this.width;
        if (this.plotLines && this.plotLines.length) {
            this.plotWidth = this.width - this.margins.leftWithAdditionalLabels;
            this.margins.left = this.margins.leftWithAdditionalLabels;
        }
    }

    protected initSeries(options): Series[] {
        const { data, dataMin, dataMax } = preformData(options);
        const newSeries = [...this.series];
        newSeries.push({
            data,
            type: options.type,
            color: options.color,
            points: data,
            name: options.name,
            dataMax,
            dataMin
        });
        return newSeries;
    }

    private getYScale = (chartHeight: number): d3.scaleType => {
        let scale, domain;
        if (this.yAxis[0].isLogarithmic) {
            scale = d3.scaleLog();
            this.limits.y[0] = this.limits.y[0] || 0.1;
            this.limits.y[1] = this.limits.y[1] || 0.1;
            const top = getLogarithmicTopBorder(this.limits.y[1]);
            domain = [this.limits.y[0], top];
        } else {    // linear if not logarithmic
            scale = d3.scaleLinear();
            const padding = AXIS_PADDING_PRECENTAGE * (this.limits.y[1] - this.limits.y[0]);
            domain = [this.limits.y[0] - padding, this.limits.y[1] + padding];
        }
        return scale.domain(domain).range([chartHeight, 0]);
    }

    private formatYAxisLabels = (numberOfTicks: number): void => {
        if (this.yAxis[0].isLogarithmic) {
            this.yAxisScale.ticks(numberOfTicks, (tick) => formatLabels(tick, this.plotLines));
            return;
        }
        this.yAxisScale.ticks(numberOfTicks);           // linear if not logarithmic
        if (this.plotLines && this.plotLines.length) {
            const ticks = this.yScale.ticks();
            if (this.limits.y[0] > 0) {
                ticks.splice(ticks.findIndex(tick => tick === 0), 1);
            }
            this.plotLines.forEach(plotLine => {
                if (plotLine.value > this.limits.y[0] && plotLine.value < this.limits.y[1]) {
                    ticks.push(plotLine.value);
                }
            });
            this.yAxisScale.tickValues(ticks);
        }
        this.yAxisScale.tickFormat((tick) => formatLabels(tick, this.plotLines));
    }

    public setXAxis(): void {
        if (this.xAxis[0].isCategorical) {
            this.xScale = d3.scaleBand()
                .domain(this.xAxis[0].getFilteredCategories(this.limits.x))
                .range([0, this.width])
                .padding([0]);
            this.xAxisScale = d3.axisBottom(this.xScale)
                .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width));
            return;
        }
        const padding = AXIS_PADDING_PRECENTAGE * (this.limits.x[1] - this.limits.x[0]);    // linear if not categorical
        const domain = [this.limits.x[0] - padding, this.limits.x[1] + padding];
        this.xScale = d3.scaleLinear()
            .domain(domain)
            .range([0, this.width]);
        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickSize(-this.height)
            .ticks(Math.floor(this.width / TICK_GAP));
    }

    private drawErrorBar (
        container: d3.Selection<HTMLDivElement>,
        point: ErrorBarData,
        index: number,
        animate = false
    ): d3.Selection<HTMLDivElement> {
        const x = this.getXCoordinate(point);
        const { low, high, color } = point;
        const barHalfWidth = this.xAxis[0].isCategorical
            ? this.xScale.bandwidth() / 4
            : (this.xScale(1) - this.xScale(0)) / 4;
        let top, bottom, mid, barContainer = null;
        if (animate) {
            const bar = this.svg.select(`.error-bar-${index}`);
            top = bar.select('.error-top')
                .transition().duration(this.animationTime);
            bottom = bar.select('.error-bottom')
                .transition().duration(this.animationTime);
            mid = bar.select('.error-mid')
                .transition().duration(this.animationTime);
        } else {
            barContainer = container
                .append('g')
                .attr('class', `error-bar-${index}`);
            top = barContainer.append('line')
                .attr('class', 'error-top')
                .attr('stroke', color);
            bottom = barContainer.append('line')
                .attr('class', 'error-bottom')
                .attr('stroke', color);
            mid = barContainer.append('line')
                .attr('class', 'error-mid')
                .attr('stroke', color);
        }
        top.attr('x1', () => x - barHalfWidth)
            .attr('x2', () => x + barHalfWidth)
            .attr('y1', () => this.getYCoordinate(high))
            .attr('y2', () => this.getYCoordinate(high));
        bottom.attr('x1', () => x - barHalfWidth)
            .attr('x2', () => x + barHalfWidth)
            .attr('y1', () => this.getYCoordinate(low))
            .attr('y2', () => this.getYCoordinate(low));
        mid.attr('x1', () => x)
            .attr('x2', () => x)
            .attr('y1', () => this.getYCoordinate(low))
            .attr('y2', () => this.getYCoordinate(high));
        return barContainer;
    }

    private drawErrorBars (container: d3.Selection<HTMLDivElement>, data: Series[], animate = false) {
        const barPoints = data.reduce((acc, val) => acc.concat(val.data), []);
        barPoints.forEach((barPoint, i) => this.drawErrorBar(container, barPoint, i, animate));
    }

    redraw(animate = false): void {
        const that = this;
        this.setXAxis();
        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale);
        const chartHeight = this.height - rotatedTicksMargin;
        this.yScale = this.getYScale(chartHeight);
        this.yAxisScale = d3.axisLeft(this.yScale).tickSize(-this.width);
        this.formatYAxisLabels(Math.floor(chartHeight / TICK_GAP));

        let innerSpace, lines, plotLines, errorBars;
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
            lines = d3.select('#lines');
            plotLines = d3.select('#plot-lines');
            errorBars = d3.select('#error-bars');
        } else {
            this.svg.selectAll('g').remove();

            innerSpace = this.svg.append('g')
                .attr('transform', `translate(${this.margins.left}, ${this.margins.top / 2})`)
                .on('mousemove', (e) => this.handleMouseMovement(e))
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

            this.addTooltip();

            innerSpace.append('g')      // Add the brush feature
                .attr('class', 'brush')
                .attr('id', 'brush_' + this.id)
                .call(d3.brush()
                    .extent([[0, 0], [this.width, chartHeight]])
                    .filter(() => true) //reset filter that filters out events with ctrl button by default
                    .on('end', (event) => {
                        innerSpace.select('.brush').call(d3.brush().clear);
                        if (!that.selection) {
                            return;
                        }
                        if (!event.selection) {
                            that.removeSelection();
                            this.update();
                            return;
                        }
                        const xMin = getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[0][0]);
                        const xMax = getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[1][0]);
                        const yMin = getReversedScaledValue(this.yAxis[0], this.yScale, event.selection[0][1]);
                        const yMax = getReversedScaledValue(this.yAxis[0], this.yScale, event.selection[1][1]);
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
                        that.selection(getSelectionRequest(event, selectionCords, chartX, chartY, originalEvent));
                    })
                );

            plotLines = innerSpace.append('g')
                .attr('id', 'plot-lines')
                .attr('clip-path', this.clipPathURL);
            lines = innerSpace.append('g')
                .attr('id', 'lines')
                .attr('clip-path', this.clipPathURL);
            errorBars = innerSpace.append('g')
                .attr('id', 'error-bars')
                .attr('clip-path', this.clipPathURL);

            innerSpace.selectAll('path')      // repaint chart borders for export
                .attr('stroke', PLOT_COLORS.lineColor);
        }

        innerSpace.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.plotWidth));

        this.drawPlotLines(plotLines, animate);
        let lineSeriesData = this.series.filter(serie => serie.type === DATA_TYPES.simpleLine);
        const errorBarData = this.series.filter(serie => serie.type === DATA_TYPES.errorbar);
        if (!this.series[0].color) {
            lineSeriesData = [];
            this.series.forEach((serie, lineNumber) => {
                serie.data.forEach((point, i) => {
                    if (i < serie.data.length - 1) {
                        lineSeriesData.push({
                            color: point.color,
                            data: [point, serie.data[i + 1]],
                            lineNumber
                        });
                    }
                });
            });
        }
        lineSeriesData.forEach((serie, i) => this.drawLineWithPoints(lines, serie, animate, i));
        innerSpace.selectAll('.line-with-points path').style('transition', 'stroke-width .25s');
        if (errorBarData.length) {
            this.drawErrorBars(errorBars, errorBarData, animate);
        }
        innerSpace.selectAll('.tick line')
            .attr('stroke', PLOT_COLORS.lineColor);
    }

    private drawMarker(source: d3.Selection<SVGElement>, index = 0): d3.Selection<HTMLDivElement> {
        const dots = source.enter()
            .append('g')
            .attr('class', `marker markers-${index}`);
        const circle = dots.append('circle')
            .attr('cx', d => this.getXCoordinate(d))
            .attr('cy', d => this.getYCoordinate(d.y))
            .attr('r', CIRCLE_DIAMETERS.inactive)
            .attr('fill', d => (d.selected ? 'white' : d.color))
            .attr('pointer-events', 'all')
            .attr('stroke', d => d.color)
            .attr('stroke-width', 2)
            .style('stroke-opacity', 0)
            .style('transition',
                `r ${MICROANIMATION_TIME}s,
                fill ${MICROANIMATION_TIME}s,
                fill-opacity ${MICROANIMATION_TIME}s,
                stroke ${MICROANIMATION_TIME}s,
                stroke-opacity ${MICROANIMATION_TIME}s`);
        if (this.markerSymbol === 'diamond') {
            circle.attr('stroke-width', '1')
                .style('fill-opacity', '0');
            dots.append('path')
                .attr('d', d3.symbol()
                    .type(d3.symbolDiamond)
                    .size(DIAMOND_SIZE)
                )
                .attr('transform', d => `translate(${this.getXCoordinate(d)}, ${this.getYCoordinate(d.y)})`)
                .attr('fill', d => d.color)
                .attr('fill-opacity', 1)
                .attr('stroke-width', 2)
                .attr('stroke', d => d.selected ? 'black' : 'none')
                .attr('pointer-events', 'none')
                .style('transition',
                    `fill ${MICROANIMATION_TIME}s,
                    fill-opacity ${MICROANIMATION_TIME}s,
                    stroke ${MICROANIMATION_TIME}s,
                    stroke-opacity ${MICROANIMATION_TIME}s`);
        }
        return dots;
    }

    public updateMarker = (serie: Series, index: number): void => {
        this.svg
            .selectAll(`.markers-${index} circle`)
            .data(serie.data);
        if (this.markerSymbol === 'diamond') {
            this.svg
                .selectAll(`.markers-${index} path`)
                .transition()
                .duration(this.animationTime)
                .attr('transform', d => `translate(${this.getXCoordinate(d)}, ${this.getYCoordinate(d.y)})`);
        }
        this.svg
            .selectAll(`.markers-${index} circle`)
            .transition()
            .duration(this.animationTime)
            .attr('cx', d => this.getXCoordinate(d))
            .attr('cy', d => this.getYCoordinate(d.y));
    }

    protected enter(source: d3.Selection<SVGElement>, index = 0): void {
        const that = this;
        this.drawMarker(source, index)
            .on('click', (event, d) => that.handleClickOnElement(event, d));
    }

    public update = (): void => {
        let targetLine;
        this.svg.selectAll('.marker').each(function(d) {
            if (d.isActive) {
                targetLine = d3.select(this.parentNode).attr('class');
            }
        });
        this.svg.selectAll('.line-with-points')
            .selectAll('path')
            .attr('stroke-width', function() { return d3.select(this.parentNode).attr('class') === targetLine ? 2 : 1; });

        if (this.markerSymbol === 'diamond') {
            this.svg.selectAll('.marker circle')
                .attr('r', 2 * CIRCLE_DIAMETERS.active)
                .style('fill-opacity', d => d.isActive ? 0.2 : 0);
            this.svg.selectAll('.marker path')
                .attr('fill', d => d.selected ? PLOT_COLORS.selectedBarColor : d.color)
                .attr('stroke', d => d.selected ? 'black' : 'none');
            return;
        }
        this.svg.selectAll('.marker circle')
            .attr('r', d => d.isActive ? CIRCLE_DIAMETERS.active : CIRCLE_DIAMETERS.inactive)
            .attr('fill', d => d.selected ? PLOT_COLORS.selectedBarColor : d.color)
            .attr('stroke', d => d.selected ? 'black' : d.color)
            .style('stroke-opacity', d => d.selected ? 1 : 0)
            .style('opacity', 1);
    }

    public redrawTitle(): void {
        d3.select(this.container)
            .select('.chart-title')
            .text(this.title);
    }
}
