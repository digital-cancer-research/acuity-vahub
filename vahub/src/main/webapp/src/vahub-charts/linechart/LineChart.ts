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
    getSelectionRequest,
    preformData,
    wrap,
    rotatedLabelsMargin,
    PLOT_COLORS
} from '../utils/utils';
import {AbstractLineChart, MICROANIMATION_TIME} from './AbstractLineChart';
import {Series, UserOptions} from '../types/interfaces';
import {Axis} from '../axis';

export const AXIS_PADDING_PRECENTAGE = 0.02;
export const LINE_WIDTH = 2;
export const MARKERS_SIZES = {
    dot: {
        normal: 1,
        selected: 2,
        active: 3
    },
    background: 10
};

export class LineChart extends AbstractLineChart {
    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.markerSymbol = options.plotOptions ? options.plotOptions.markerSymbol : null;
    }

    protected initSeries(options): Series[] {
        const { data, dataMin, dataMax } = preformData(options, this.xAxis[0].categories);
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

    private setXAxis(): void {
        const xCategories = this.xAxis[0].getFilteredCategories(this.limits.x);
        this.xScale = d3.scaleBand()
            .domain(xCategories)
            .range([0, this.width])
            .paddingOuter([AXIS_PADDING_PRECENTAGE * xCategories.length]);
        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width));
    }

    private setYAxis(chartHeight: number): void {
        const padding = AXIS_PADDING_PRECENTAGE * (this.limits.y[1] - this.limits.y[0]);
        this.yScale = d3.scaleLinear()
            .domain([this.limits.y[0] - padding, this.limits.y[1] + padding])
            .range([chartHeight, 0]);
        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickSize(-this.width);
    }

    private getReversedScaledValue = (axis: Axis, scale: d3.scaleType, coordinate: number) => {
        if (axis.isCategorical) {
            const reversedScale = d3.scaleQuantize().domain(scale.range()).range(scale.domain());
            return axis.categories.indexOf(reversedScale(coordinate));
        }
        return scale.invert(coordinate);
    }

    redraw(animate = false): void {
        const that = this;
        this.setXAxis();
        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale);
        const chartHeight = this.height - rotatedTicksMargin;
        this.setYAxis(chartHeight);
        let innerSpace, lines;
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
        } else {
            this.svg.selectAll('g').remove();

            innerSpace = this.svg.append('g')
                .attr('transform', `translate(${this.margins.left}, ${this.margins.top / 2})`)
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

            this.addTooltip();

            innerSpace.append('g')      // Add the brush feature
                .attr('class', 'brush')
                .attr('id', 'brush_' + this.id)
                .call(d3.brush()
                    .extent([[0, 0], [this.width, chartHeight]])
                    .filter(() => true) //reset filter that filters out events with ctrl button by default
                    .on('end', (event) => {
                        innerSpace.select('.brush').call(d3.brush().clear);
                        if (!event.selection) {
                            that.removeSelection();
                            this.update();
                            return;
                        }
                        const xMin = this.getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[0][0]);
                        const xMax = this.getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[1][0]);
                        const yMin = this.getReversedScaledValue(this.yAxis[0], this.yScale, event.selection[0][1]);
                        const yMax = this.getReversedScaledValue(this.yAxis[0], this.yScale, event.selection[1][1]);
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

            lines = innerSpace.append('g')
                .attr('id', 'lines')
                .attr('clip-path', this.clipPathURL);

            innerSpace.selectAll('path')      // repaint chart borders for export
                .attr('stroke', PLOT_COLORS.lineColor);
        }

        innerSpace.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.width));

        this.series.forEach((serie, i) => this.drawLineWithPoints(lines, serie, animate, i));
        innerSpace.selectAll('.line-with-points path').attr('stroke-width', LINE_WIDTH);
        innerSpace.selectAll('.tick line')
            .attr('stroke', PLOT_COLORS.lineColor);
    }

    private drawMarker(source: d3.Selection<SVGElement>, index = 0): d3.Selection<HTMLDivElement> {
        const dots = source.enter()
            .append('g')
            .attr('class', `marker markers-${index}`);
        dots.append('circle')
            .attr('class', 'background')
            .attr('r', MARKERS_SIZES.background)
            .attr('pointer-events', 'all')
            .attr('stroke', d => d.color)
            .attr('stroke-width', 1)
            .attr('stroke-opacity', '0')
            .attr('fill-opacity', '0');
        dots.append('circle')
            .attr('class', 'dot')
            .attr('r', MARKERS_SIZES.dot.normal)
            .attr('fill-opacity', 1)
            .attr('stroke-width', 0.2)
            .attr('stroke', 'none')
            .attr('pointer-events', 'none')
            .style('transition',
                `r ${MICROANIMATION_TIME}s, fill ${MICROANIMATION_TIME}s, stroke ${MICROANIMATION_TIME}s`);
        dots.selectAll('circle')
            .attr('cx', d => this.getXCoordinate(d))
            .attr('cy', d => (this.getYCoordinate(d.y)))
            .attr('fill', d => d.color);
        return dots;
    }

    public updateMarker = (serie: Series, index: number): void => {
        this.svg
            .selectAll(`.markers-${index}`)
            .data(serie.data)
            .selectAll(`circle`)
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
        this.svg.selectAll('.marker .background')
            .attr('fill-opacity', d => d.isActive ? 0.2 : 0);
        this.svg.selectAll('.marker .dot')
            .attr('r', d => d.selected ? MARKERS_SIZES.dot.selected : d.isActive ? MARKERS_SIZES.dot.active : MARKERS_SIZES.dot.normal)
            .attr('fill', d => d.selected ? 'black' : d.color)
            .attr('stroke', d => d.selected ? 'black' : d.isActive ? 'white' : 'none');
    }
}
