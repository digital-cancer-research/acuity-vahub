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
