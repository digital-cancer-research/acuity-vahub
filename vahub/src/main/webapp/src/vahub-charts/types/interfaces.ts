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

export interface PlotAxisConfig {
    title?: {
        text: string;
    };
    type?: string;
    categories?: string[];
    borders?: {min: number, max: number};
}

export interface ChartTooltip {
    formatter: () => string;
    onclick?: (element: HTMLElement, event: Event) => void;
}

export type ChartMouseEvent = d3.BrushEvent | MouseEvent;

export interface ChartEvents {
    displayMarkingDialogue?: Function;
    removeSelection?: () => void;
    selection?: (event: ChartMouseEvent) => boolean;
    click?: (event: ChartMouseEvent) => boolean;
}

export interface CustomPlotConfig {
    chart?: {
        renderTo?: HTMLElement;
        id?: string,
        height?: number;
        type?: string;
        isInverted?: boolean;
        events?: ChartEvents;
        animationTime?: number;
        disableExport?: boolean;
        margins?: {
            left?: number;
            right?: number;
            top?: number;
            bottom?: number;
        },
        [x: string]: any,
    };
    plotOptions?: {
        markerSymbol?: string;
    };
    plotLines?: PlotLine[];
    yAxis?: PlotAxisConfig[];
    xAxis?: PlotAxisConfig[];
    tooltip?: ChartTooltip;
}

export interface UserOptions extends CustomPlotConfig {
    title?: {
        text: string,
    };
    exporting?: {
        buttons: {
            text: string,
            onclick: () => void
        }[]
    };
    [x: string]: any;
}

export interface Margins {
    top?: number;
    right?: number;
    bottom?: number;
    left?: number;
    bottomWithRotatedLabels?: number;
}

export interface Gradient {
    id?: string;
    patternUnits?: number;
    width?: number;
    height?: number;
    path?: {
        d: string;
        stroke: string;
        strokeWidth: number;
    };
}

export interface Background extends String {
    pattern: Gradient;
}

export interface SeriesOptions<Type> {
    color?: string | Gradient;
    name?: string | ChordDiagramChordName;
    type?: string;
    data?: Array<Type>;
    [x: string]: any;
    events?: object;
}

export interface SeriesItem {
    x?: number;
    y?: number;
    [x: string]: any;
}

export interface MarkerItem {
    x: number;
    y: number;
    name: string;
    marker: {
        fillColor: string,
        name: string,
        symbol: string,
    };
}
export interface TimelineMarkerItem {
    x: number;
    y: number;
    id: number;
    eventType: string;
    name: string;
    marker: {
        fillColor: string,
        name: string,
        symbol: string,
        height: number,
        width: number;
    };
}

export interface ColumnRangeBarItem {
    x: number;
    low: number;
    high: number;
    name: string;
    noStartDate: boolean;
    [x: string]: any;
}

export interface RangePlotPoint {
    x?: number;
    y: number;
    dataPoints: number;
    stdErr: number;
    ranges?: number[];
    marker?: {
        states?: {
            select?: {
                lineColor?: string;
            }
        };
        lineColor?: string;
        fillColor?: string;
        lineWidth?: number;
    };
    name?: string;
    category?: string;
    isActive: boolean;
    setIsActive: (isActive: boolean) => void;
}

export interface RangePlotData {
    ranges: number[][];
    averages: RangePlotPoint[];
    name: string;
    color: string;
}

export interface RangePlot {
    data: RangePlotData[];
    categories: string[];
}

export interface RangePlotArearangeData {
    x: number;
    yMin: number;
    yMax: number;
}

export interface RangePlotLineData extends RangePlotPoint {
    point: RangePlotPoint;
    series: {
        name: string;
    };
    selected: boolean;
    select: (selected: boolean) => void;
}

export interface ScatterPlotData {
    x: number;
    y: number;
    color: string;
    selected: boolean;
    select: (selected: boolean) => void;
}

export interface SimpleLinePlotData {
    x: number;
    y: number;
    category?: string;
    color: string;
    selected: boolean;
    select: (selected: boolean) => void;
    isActive: boolean;
    setIsActive: (isActive: boolean) => void;
}

export interface ErrorBarData {
    x: number;
    category?: string;
    low: number;
    high: number;
    color: string;
}

export interface BarLineData {
    x: number;
    category?: string;
    low: number;
    high: number;
    color: string;
}

export interface BoxPlotBoxItem {
    eventCount: number;
    lowerQuartile: number;
    lowerWhisker: number;
    median: number;
    outliers: any[];
    subjectCount: number;
    upperQuartile: number;
    upperWhisker: number;
    x: string;
    xrank: number;
}

export interface HeatMapData {
    categories: {
        x: string;
        y: string;
    };
    color: string | Gradient;
    name: string;
    value: string;
    x: number;
    y: number;
    seriesName: string;
    isSelected?: boolean;
    select?: (isSelected: boolean) => void;
}

export interface ChordDiagramChordItem {
    x: number;
    y: number;
}

export interface ChordDiagramPieItem {
    name: string;
    y: number;
    color: string;
}

export interface ChordDiagramChordName {
    start: string;
    end: string;
}

export interface ChordDiagramChord {
    color: string;
    data: ChordDiagramChordItem;
    name: ChordDiagramChordName;
    subjects: [string, number][];
    value: number;
    events: ChartEvents;
    isSelected: boolean;
    select: (isSelected: boolean) => void;
}

export interface ChordDiagramPie extends SeriesOptions<ChordDiagramPieItem> {
    size: string;
    innerSize: string;
}

export interface D3ChordData {
    source: {
        index: number;
        startAngle: number;
        endAngle: number;
        value: number;
    };
    target: {
        index: number;
        startAngle: number;
        endAngle: number;
        value: number;
    };
}
export interface D3ArcData {
    index: number;
    value: number;
    endAngle: number;
    startAngle: number;
}

export interface ExtendedChordData extends D3ChordData {
    chordData: ChordDiagramChord;
    groups: {
        color: string;
        name: string;
        y: number;
        value: number;
        endAngle: number;
        startAngle: number;
        type: string;
    };
}

export type ChordDiagramData = ChordDiagramChord & ChordDiagramPie;

export type PlotData = ScatterPlotData | RangePlotLineData | RangePlotArearangeData | SimpleLinePlotData | ErrorBarData | ChordDiagramData;

export interface PreformedPlotData {
    data: PlotData[];
    dataMin: number;
    dataMax: number;
    type?: string;
    name?: string;
}

export interface Series {
    [x: string]: any;
}

export interface PlotLine {
    axis?: string;
    width?: number;
    value?: number;
    color?: string;
    x1?: number;
    x2?: number;
    y1?: number;
    y2?: number;
    styles?: object;
    zIndex?: number;
    formatLabel?: (value: number) => string;
}

export interface PlotBand {
    from: number;
    to: number;
    id: string;
    color: string;
}

export type SeriesItemType = SeriesItem | ColumnRangeBarItem | BoxPlotBoxItem | number;
export interface ZoneSelection {
    xMin: number;
    xMax: number;
    yMin: number;
    yMax: number;
}
