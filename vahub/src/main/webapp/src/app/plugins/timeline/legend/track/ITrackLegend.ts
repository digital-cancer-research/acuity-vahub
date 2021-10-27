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

export interface TrackLegendConfig {
    title: string;
    items: TrackLegendItem[];
}

export interface TrackLegendItem {
    type: TrackLegendType;
    text: string;
    stroke?: string;
    strokeWidth?: string;
    color?: string;
    colorStart?: string;
    colorEnd?: string;
    height?: string;
    width?: string;
    rotate?: number;
    src?: string;
    yAxisOption?: any;
    warning?: boolean;
}

export enum TrackLegendType {
    ACUITY = 'ACUITY',
    CIRCLE = 'CIRCLE',
    DIAMOND = 'DIAMOND',
    TRIANGLE_RIGHT = 'TRIANGLE_RIGHT',
    LINE = 'LINE',
    DASH_LINE = 'DASH_LINE',
    LINE_AND_CIRCLE = 'LINE_AND_CIRCLE',
    CIRCLE_AND_LINE = 'CIRCLE_AND_LINE',
    IMAGE = 'IMAGE',
    GAP = 'GAP',
    GRADIENT = 'GRADIENT',
    STAR = 'STAR'
}
