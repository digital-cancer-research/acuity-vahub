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
    MARGINS_TIMELINE,
} from '../utils/utils';
import {
    PlotBand,
    PlotLine,
    UserOptions
} from '../types/interfaces';
import {Chart} from '../Chart';

export abstract class TimelineChart extends Chart {
    public margins = MARGINS_TIMELINE;
    public plotLines: PlotLine[] = [];
    public minHeight = 30;
    protected handleDblclick: (axisX: number) => void;
    protected subjectId;

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.subjectId = options.id;
        this.handleDblclick = options.handleDblclick;
    }

    public addPlotLines (lines, inverted = false) {
        const getX = (value) => inverted ? this.yScale(value) : this.xScale(value);
        this.svg.select('.line-container')
            .selectAll('.plot-line')
            .data(lines)
            .enter()
            .append('line')
            .attr('class', 'plot-line')
            .attr('stroke-width', (d) => (d.width + 'px'))
            .attr('stroke-dasharray', '4')
            .attr('fill', 'none')
            .attr('stroke', (d) =>  d.color)
            .attr('id', (d) => d.id)
            .attr('x1', (d) => getX(d.value))
            .attr('x2', (d) => getX(d.value))
            .attr('y1', 0)
            .attr('y2', this.height)
            .on('dblclick', (event) => {
                const coords = d3.pointer(event);
                this.handleDblclick(inverted ? this.yScale.invert(coords[0]) :  this.xScale.invert(coords[0]));
            });
    }

    public removePlotLine () {
        this.svg.select('.line-container')
            .selectAll('.plot-line')
            .remove();
    }

    public getPlotLine () {
        const line =  this.svg.select('.line-container')
            .select('.plot-line');
        return line.empty() ? null : line.data()[0].value;
    }

    public hideTooltip () {
        d3.selectAll('.chart-tooltip')
            .style('opacity', '0');
    }

    public highlightBackground (options: {plotBackgroundColor: string}) {
        this.svg.select('.background')
            .attr('fill', options.plotBackgroundColor);
    }

    public removePlotBand (id: string) {
        this.svg.select(`#${id}`).remove();
    }

    public addPlotBand (id: PlotBand, inverted = false) {
        const width =  inverted ? Math.abs(this.yScale(id.from) - this.yScale(id.to)) : Math.abs(this.xScale(id.from) - this.xScale(id.to));
            this.svg.select('.plot-bands-container')
                .selectAll(`#${id.id}`)
                .data([id])
                .enter()
                .append('rect')
                .attr('x', (d) => inverted ? this.yScale(d.from) : this.xScale(d.from))
                .attr('width', width)
                .attr('height', '100%')
                .attr('class', 'plot-band')
                .attr('fill', (d) => d.color)
                .attr('id', (d) => d.id);
    }

    public movePlotBands (inverted = false, animate = false) {
        this.svg.selectAll('.plot-band')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => inverted ? this.yScale(d.from) : this.xScale(d.from))
            .attr('width', d => inverted
                ? Math.abs(this.yScale(d.from) - this.yScale(d.to))
                : Math.abs(this.xScale(d.from) - this.xScale(d.to))
            );
    }

    public removeSeries () {
        this.series = [];
    }

    public destroy(): void {
        d3.select(`#${this.id}`).remove();
    }

    public abstract resize(): void;

}
