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
import { getScaledCoordinate, getSelectionRequest, PLOT_COLORS } from '../utils/utils';
import {Chart} from '../Chart';
import {UserOptions, ZoneSelection} from '../types/interfaces';

export const TICK_GAP = 50;
export const BAR_CLASS = 'bar';
export const DIALOG_MARGINS = {
    top: 40,
    left: 60
};

export abstract class BarChart extends Chart {
    public xScale: d3.ScaleBand<string>;
    public ticksOnEdges = false;
    public axis = {};

    public constructor(
        options: UserOptions,
    ) {
        super(options);
    }

    public getXCoordinate (d): number {
        return getScaledCoordinate(this.xAxis[0], this.xScale, this.limits.x, this.width, d.x, d.category) - this.xScale.bandwidth() / 2;
    }

    public update(): void {
        this.svg.selectAll(`.${BAR_CLASS}`)
            .attr('fill', d => (d.selected ? PLOT_COLORS.selectedBarColor : d.color));
    }

    public clearSeries(): void {
        super.clearSeries();
        this.axis = {};
    }

    protected drawInnerSpace(): d3.selection<SVGElement> {
        return this.svg.append('g')
            .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top + ')')
            .on('mouseout', () => {
                this.handleMouseOut();
            });
    }

    protected drawBrush(
        innerSpace: d3.Selection<SVGElement>,
        getSelectionZone: (selection: number[][]) => ZoneSelection,
        chartHeight: number = this.height
    ) {
        const that = this;
        innerSpace
            .append('g')
            .attr('class', 'brush')
            .call(d3.brush()
                .extent([[0, 0], [this.width, chartHeight]])
                .filter(() => true)
                .on('end', (event) => {
                    if (!event.selection) {
                        that.removeSelection();
                        this.update();
                        return;
                    }
                    const { xMin, xMax, yMin, yMax } = getSelectionZone(event.selection);
                    const chartX = event.selection[1][0] + DIALOG_MARGINS.left;
                    const chartY = event.selection[1][1] + DIALOG_MARGINS.top;
                    const selectionCords = {
                        xAxis: [
                            {
                                min: Math.min(xMin, xMax),
                                max: Math.max(xMin, xMax),
                                axis: {
                                    categories: that.xAxis[0].categories
                                }
                            }
                        ],
                        yAxis: [
                            {
                                min: Math.floor(Math.min(yMin, yMax)),
                                max: Math.ceil(Math.max(yMin, yMax)),
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
    }

    protected abstract drawBar(source: d3.Selection<SVGElement>, animate: boolean);

    protected enter(source: d3.Selection<SVGElement>, animate = false, series?, subjects?) {
        const that = this;
        source.enter()
            .append('rect')
            .attr('class', BAR_CLASS)
            .attr('pointer-events', 'all')
            .on('mouseover', function (event, d) {
                const tooltipText = that.tooltip.formatter.call({...d, x: that.xAxis[0].categories[d.x]});
                that.handleMouseOver(event, d, tooltipText);
                d3.select(this).style('opacity', .7);
            })
            .on('mouseout', function () {
                d3.select(this).style('opacity', 1);
                that.handleMouseOut();
            })
            .on('click', (event, d) => that.handleClickOnElement(event, d));
        this.drawBar(this.svg.select('#bars')
            .selectAll(`.${BAR_CLASS}`), animate);
    }
}
