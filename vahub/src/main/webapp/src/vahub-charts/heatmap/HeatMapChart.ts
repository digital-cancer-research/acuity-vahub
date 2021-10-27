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
    splitLabels,
    getCategoricalAxisValues,
    HEAT_MAP_CHART_MARGINS,
    getSelectionRequest,
    getScaledCoordinate,
    getReversedScaledValue,
    selectionProperties
} from '../utils/utils';
import {Chart} from '../Chart';
import {Background, HeatMapData, Series, UserOptions} from '../types/interfaces';

export const DEFAULT_FILLING_SERIES = 'DEFAULT_FILLING_SERIES';
export const X_AXIS_LABELS_TRANSFORM = 'rotate(-50deg) translate(-35px, -5px)';
export const BRUSH_SELECTION_COLORS = {
    fill: 'rgba(0, 0, 255, .25)',
    stroke: 'rgba(0, 0, 255, 1)'
};
export const MAX_TICKS = 50;
export const MAX_NORMAL_FONT_TICKS = 40;

export class HeatMapChart extends Chart {
    private svgDefs: d3.Selection<SVGElement>;
    public plotWidth: number;
    private initialYAxisBandwidth: number;
    public margins = HEAT_MAP_CHART_MARGINS;
    public limits = { x: null, y: null };

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(options): Series[] {
        let dataMax = 0, dataMin = 0;
        const data: HeatMapData[] = [];
        options.data.forEach(d => {
            data.push({
                ...d,
                point: d,
                categories: {
                    x: this.xAxis[0].categories[d.x],
                    y: this.yAxis[0].categories[d.y]
                },
                seriesName: options.name,
                ...selectionProperties
            });
            dataMax = d.y > dataMax ? d.y : dataMax;
            dataMin = d.y < dataMin ? d.y : dataMin;
        });
        const newSeries = [...this.series];
        newSeries.push({
            name: options.name || '',
            data,
            dataMax,
            dataMin
        });
        return newSeries;
    }

    protected drawYAxis(innerSpace: d3.Selection<SVGElement>, animate = false) {
        const yCategories = this.limits.y ? this.yAxis[0].getFilteredCategories(this.limits.y) : this.yAxis[0].categories;
        this.yScale = d3.scaleBand()
            .domain(yCategories)
            .range([this.height, 0])
            .paddingInner([0.1])
            .paddingOuter([0.5]);

        this.initialYAxisBandwidth = this.initialYAxisBandwidth || this.yScale.bandwidth();

        const yAxisTickValues = getCategoricalAxisValues(this.yScale.domain(), this.height, MAX_TICKS);
        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickValues(yAxisTickValues)
            .tickSizeOuter([0]);

        if (!animate) {
            innerSpace.append('g')
                .attr('class', 'y-axis');
        }
        innerSpace.select('.y-axis')
            .call(this.yAxisScale)
            .selectAll('line')
            .style('transform', `translateY(${this.yScale.bandwidth() / 2}px)`);

        const tickTexts = innerSpace.selectAll('.y-axis .tick text')
            .call(splitLabels(this.margins.left));

        if (yAxisTickValues.length > MAX_NORMAL_FONT_TICKS) {
            tickTexts.style('font-size', '8px');
        } else {
            tickTexts.style('font-size', '10px');
        }
    }

    protected drawXAxis(innerSpace: d3.Selection<SVGElement>, animate = false) {
        const xCategories = this.limits.x ? this.xAxis[0].getFilteredCategories(this.limits.x) : this.xAxis[0].categories;
        const width = this.initialYAxisBandwidth * xCategories.length + this.margins.left;
        this.plotWidth = width < this.width ? width : this.width;

        this.xScale = d3.scaleBand()
            .domain(xCategories)
            .range([0, this.plotWidth])
            .paddingInner([0.1])
            .paddingOuter([0.5]);

        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.plotWidth))
            .tickSizeOuter([0]);

        if (!animate) {
            innerSpace.append('g')
                .attr('class', 'x-axis')
                .attr('transform', `translate(0,${this.height})`);
        }
        innerSpace.select('.x-axis')
            .transition()
            .duration(animate && this.animationTime)
            .call(this.xAxisScale);
        innerSpace.selectAll('.x-axis text')
            .style('transform', X_AXIS_LABELS_TRANSFORM);
        innerSpace.selectAll('.x-axis line')
            .style('transform', `translateX(-${this.xScale.bandwidth() / 2}px)`)
            .style('stroke', 'black');
    }

    public update = (): void => {
        this.svg.selectAll('.rect')
            .attr('stroke-width', d => (d.selected ? 2 : 0));
    }

    private invertXScale = (x: number) => {
        return getReversedScaledValue(this.xAxis[0], this.xScale, x);
    }

    private invertYScale = (y: number) => {
        return getReversedScaledValue(this.yAxis[0], this.yScale, y, true);
    }

    private getXCoordinate (d: HeatMapData): number {
        const halfBandwidth = this.xScale.bandwidth() / 2;
        const chartSize = this.plotWidth + halfBandwidth;
        return getScaledCoordinate(this.xAxis[0], this.xScale, this.limits.x, chartSize, d.x, d.categories.x) - halfBandwidth;
    }

    private getYCoordinate (d: HeatMapData): number {
        const halfBandwidth = this.yScale.bandwidth() / 2;
        const chartSize = this.height + halfBandwidth;
        return getScaledCoordinate(this.yAxis[0], this.yScale, this.limits.y, chartSize, d.y, d.categories.y, true) - halfBandwidth;
    }

    redraw(animate = false): void {
        const series: HeatMapData[] = this.series.reduce((acc, val) => acc.concat(val.data), []);
        const that = this;
        const isAnimated = animate && this.animateOnZoom;
        let innerSpace, rectContainer;
        const bandwidth = {
            x: this.xScale ? this.xScale.bandwidth() : 0,
            y: this.yScale ? this.yScale.bandwidth() : 0
        };

        if (isAnimated) {
            innerSpace = this.svg.select('g');
            rectContainer = innerSpace.select('#rects');
        } else {
            this.svg.selectAll('g').remove();
            innerSpace = this.svg.append('g')
                .attr('transform', `translate(${this.margins.left}, ${this.margins.top / 2})`);

            this.addClipPath(innerSpace, this.height - bandwidth.y / 2, this.plotWidth - bandwidth.x / 2);

            this.addTooltip();

            innerSpace.append('g')      // Add the brush feature
                .attr('class', 'brush')
                .attr('id', 'brush_' + this.id)
                .attr('clip-path', this.clipPathURL)
                .call(d3.brush()
                    .extent([[0, 0], [this.plotWidth, this.height]])
                    .filter(() => true) //reset filter that filters out events with ctrl button by default
                    .on('end', (event) => {
                        if (!event.selection) {
                            that.removeSelection();
                            this.update();
                            return;
                        }
                        const xMin = that.invertXScale(event.selection[0][0]);
                        const xMax = that.invertXScale(event.selection[1][0]);
                        const yMin = that.invertYScale(event.selection[0][1]);
                        const yMax = that.invertYScale(event.selection[1][1]);
                        const chartX = event.selection[1][0] + this.margins.left;
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
                        innerSpace.select('.brush').call(d3.brush().clear);
                    })
                );
            innerSpace.select('.brush .selection')
                .attr('fill', BRUSH_SELECTION_COLORS.fill)
                .attr('stroke', BRUSH_SELECTION_COLORS.stroke)
                .attr('id', 'brushSelection');
            rectContainer = innerSpace.append('g')
                .attr('id', 'rects')
                .attr('clip-path', this.clipPathURL);
            innerSpace.append('use').attr('xlink:href', '#brushSelection');
        }

        this.drawYAxis(innerSpace, isAnimated);
        this.drawXAxis(innerSpace, isAnimated);

        innerSpace.select(`#${this.clipPathId} rect`)
            .transition()
            .duration(this.animationTime)
            .attr('width', this.plotWidth - bandwidth.x / 2)
            .attr('height', this.height - bandwidth.y / 2);

        d3.select('.chart-title').style('width', `${this.plotWidth + this.margins.left}px`);

        this.drawRects(rectContainer, series, isAnimated);
    }

    protected handleSingleClickOnElement(event, d: HeatMapData) {
        const selectionCords = {
            xAxis: [
                {
                    value: d.x,
                    min: d.x,
                    max: d.x,
                    axis: {
                        categories: this.xAxis[0].categories
                    }
                }
            ],
            yAxis: [
                {
                    value: d.y,
                    min: d.y,
                    max: d.y,
                    axis: {
                        categories: this.yAxis[0].categories
                    }
                }
            ]
        };
        this.selection(getSelectionRequest(event, selectionCords, event.offsetX, event.offsetY));
    }

    private drawRects (container: d3.Selection<SVGElement>, data: HeatMapData[], animate: boolean) {
        const that = this;
        const rects = container.selectAll('.rect').data(data);
        rects.exit().remove();
        if (animate) {
            rects
                .transition()
                .duration(this.animationTime)
                .attr('x', d => this.getXCoordinate(d))
                .attr('y', d => this.getYCoordinate(d))
                .attr('width', () => this.xScale.bandwidth())
                .attr('height', () => this.yScale.bandwidth());
        }
        rects.enter()
            .append('rect')
            .attr('class', 'rect')
            .attr('x', d => this.getXCoordinate(d))
            .attr('y', d => this.getYCoordinate(d))
            .attr('width', () => this.xScale.bandwidth())
            .attr('height', () => this.yScale.bandwidth())
            .attr('fill', d => that.getBackground(d.color))
            .attr('stroke', 'black')
            .attr('stroke-width', d => d.selected ? 2 : 0)
            .attr('pointer-events', d => (d.seriesName === DEFAULT_FILLING_SERIES) ? 'none' : 'auto')
            .style('cursor', 'crosshair')
            .style('opacity', '1')
            .on('mouseover', function (event, d) {
                d3.select(this).style('opacity', .3);
                that.handleMouseOver(event, d);
                clearTimeout(that.tooltipTimeout);
            })
            .on('mouseout', function () {
                d3.select(this).style('opacity', 1);
                that.tooltipTimeout = window.setTimeout(that.handleMouseOut, 1000);
            })
            .on('click', (event, d) => that.handleClickOnElement(event, d));
    }

    private getBackground (background: Background): string {
        if (typeof background === 'string') {
            return background;
        }
        const { id, patternUnits, width, height, path } = background.pattern;
        if (!this.svgDefs) {
            this.svgDefs = this.svg.append('defs')
                .append('pattern')
                .attr('id', id)
                .attr('patternUnits', patternUnits)
                .attr('width', width)
                .attr('height', height)
                .attr('x', '0')
                .attr('y', '0')
                .append('path')
                .attr('d', path.d)
                .attr('stroke', path.stroke)
                .attr('stroke-width', path.strokeWidth);
        }
        return `url(#${id})`;
    }

}
