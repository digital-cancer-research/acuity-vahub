# vahub-charts

This directory contains all the chart classes that are used to plot the study data. They are built with [d3.js](https://github.com/d3/d3/wiki).

### Table of contents:
- [List of implemented chart types](#implemented-list)
- [Chart class properties](#chart-properties)
- [Chart class methods](#chart-methods)
- [Creating a new chart instance](#creating-chart)
- [Referenced interfaces and types](#interfaces)

## <a name="implemented-list"></a> List of implemented chart types

- **ScatterChart** - plotting scattered data across linear x and y axes (type: `scatter`)
- **LineChart** and **SimpleLineChart** - plotting groups of markers connected by lines across different axes (linear or category for x-axis and linear or logarithmic for y-axis) (type: `line` / `simple-line`)
- **RangeChart** - same as LineChart/SimpleLineChart but with ranges for deviations around the lines (type: `range`)
- **StackedBarChart** - plotting bars across category x and linear y axes. For one x category there may be multiple bars stacked on top of each other (type: `stacked-bar-plot`)
- **GroupedBarChart** - plotting bars across category x and linear y axes. For one category there may be multiple bars that will be displayed side-by-side, separated with a category sub-scale (type: `grouped-bar-plot`)
- **BarLineChart** - same as StackedBarChart, but adding another constant y-axis for displaying continuous (line-like) data (type: `barline`)
- **BoxPlot** - plotting statistical data with boxes across linear/category x-axis and linear y-axis (mean value, quartiles, whiskers) and dots (outliers) (type: `boxplot`)
- **ShiftPlot** - plotting range (minimum and maximum values) around the baseline across linear/category x-axis and linear y-axis (type: `errorbar`)
- **HeatMapChart** - plotting heat map between two categorical axes (type: `heatmap`)
- **ChordDiagram** - plotting chord diagram to show connections between entries (type: `chord`)
- **TimeLineBarChart** - plotting data as a continuous bar and specific markers across linear x-axis (type: `timeline-barchart`)
- **TimelineLineChart** - plotting data as a continuous line (with markers at points) across linear x and y axes (type: `timeline-linechart`)
- **TimelineAxis** - axis to be used with a group of TimelineBarCharts of TimelineLineCharts(type: `timeline-xaxis`)

## <a name="chart-properties"></a> Chart class properties

### Public properties

- `container: HTMLElement` - an HTML element in which the chart is drawn
- `series: Array<Series>` - an array of data series to be plotted
- `title: string` - chart title
- `height: number` - chart height
- `width: number` - chart width
- `xAxis: Array<Axis>` - an array of charts x-axis
- `yAxis: Array<Axis>` - an array of charts y-axis
- `svg: d3.Selection<SVGElement>` - svg element containing the chart
- `margins: Margins` - margins from the borders of a svg to the actual chart elements ([interface](#interfaces-margins))
- `isInverted: boolean` - are charts axes inverted (vertical x-axis and horizontal y-axis)
- `innerSpaceSelectorForExport: string` - selector of the element containing all the chart elements for export
- `xScale: d3.scaleType` - d3 scaling function for x-axis
- `yScale: d3.scaleType` - d3 scaling function for y-axis
- `limits: Object` - borders for x and y axes (for zooming)
    - `x: Array<number>` - first value (index 0) stands for lower limit, second (index 1) - for upper limit
    - `y: Array<number>`

### Protected properties
- `id: string` - chart ID
- `chartType: string` - chart type (should correspond to [chart types](#implemented-list))
- `animationTime: number` - scaling animation time in milliseconds (if set to 0 chart won't be animated)
- `tooltip: ChartTooltip` - configuration for chart tooltip ([interface](#interfaces-tooltip))
- `plotLines: Array<PlotLine>` - a set of pre-determined lines to be drawn (e.g., level of y=5) ([interface](#interfaces-plotline))
- `click: Function` - handling click or double-click on chart
- `displayMarkingDialogue: Function` - displaying dialog window on selection
- `removeSelection: () => void` - removing chart selection
- `selection: (event: ChartMouseEvent) => boolean` - adding data selection ([ChartMouseEvent type](#interfaces-chartmouseevent))
- `xAxisScale: d3.axisType` - d3 x-axis element to be drawn
- `yAxisScale: d3.axisType` - d3 y-axis element to be drawn
- `tooltipContainer: d3.Selection<SVGElement>` - selected tooltip container for this chart
- `tooltipTimeout: number` - timeout for changing tooltip state (position, content. displaying) in milliseconds
- `minHeight: number` - minimum chart height
- `resizeTimeout: number` - timeout for applying changes to charts appearence on window resize
- `selectionZones: Array<ZoneSelection>` - an array of selection zones that should be drawn ([interface](#interfaces-selectionzone))
- `noDataMessage: string` - a message to be displayed instead of the chart

## <a name="chart-methods"></a> Chart class methods

### Public methods

- `onZoomX: function(borders: Array<number>): void` - set new limits on x-axis and redraw the chart
- `onZoomY: function(borders: Array<number>): void` - set new limits on y-axis and redraw the chart
- `setSize: function(width: number, height: number): void` - set new size for the chart
- `addSeries: function(options: SeriesOptions<SeriesItemType>): void` - add new series to charts `series` property
- `clearSeries: function(): void` - remove all chart series (set `chart.series = []`)
- `addPlotLine: function(line: PlotLine): void` - add line to charts `plotLines` property
- `redraw: function(animate?: boolean): void` - redraw the chart (whether animated, without clearing contents of svg, or completely)
- `resize: function(): void` - redraw the chart after resize and set new height parameter value
- `getSVG: function(): string` - get serialised contents of charts svg for export
- `setTitle: function(title: string): void` - set new chart title
- `setNoDataMessage: function(message: string): void` - set a message to display instead of the chart
- `destroy: function(): void` - remove charts svg element and tooltip from the document

### Protected methods

- `abstract` `initSeries: function(options: SeriesOptions<SeriesItemType>): Array<Series>` - preform data from `options` to `series` property
- `addTooltip: function(): void` - add `tooltipContainer` for this chart
- `setAxis: function(): void` - set single x and y axes
- `animateOnZoom: get(): boolean` - get whether chart is animated (`animationTime !== 0`) of not
- `init: function(options: UserOptions): void` - initialization function that:
    - sets UserOptions values to chart properties
    - adds buttons for chart export (if not disabled)
    - adds an svg element to charts container
    - adds window resize event listener
- `drawPlotLines: function(): void` - draw plot lines added to `plotLines` property
- `handleMouseOver: function(event: ChartMouseEvent, d, tooltipText?: string, movementAnimationTime?: number): void` - handler for mouseenter over chart element (to show tooltip)
- `handleMouseOut: function(): void` - handler for mouseleave from chart element (to hide tooltip)
- `clipPathId: get(): string` - getter for this charts clip path id
- `clipPathURL: get(): string` - getter for this charts clip path URL
- `addClipPath: function(): void` - draw clip path for this chart
- `resizeClipPath: function(height: number): void` - resize clip path with a new height value
- `handleResize: function(): void` - handler for window resize event listener, resizes the chart
- `redrawSelectionZones: function(xScale: d3.ScaleType, yScale: d3.ScaleType, selectedZonesContainer?: d3.Selection<SVGElement>, animate?: boolean): void` - redraw already drawn selection zones
- `showNoData: function(): void` - draw message from `noDataMessage` instead of a chart
- `handleClickOnElement: function(event: ChartMouseEvent, d): void` - handle either click or double-click on chart element
- `handleSingleClickOnElement: function(event: ChartMouseEvent, d): void` - handle single click on chart element

## <a name="creating-chart"></a> Creating a new chart instance

A new chart instance is created in a `createChart` method of AbstractPlotComponent. It uses `chart` function from `index.ts`.  
A configuration object should be passed with properties as described below. It is created in a `createPlotConfig` method of a plot component. It uses this components config service.
`chart` function uses `chart.type` property of a configuration object to create and return an instance of the appropriate chart (as stated [here](#implemented-list)).

### UserOptions interface

- `chart: Object`
    - `renderTo: HTMLElement` - an HTML element in which chart will be drawn  
    - `id: string` - chart id
    - `height: number` - chart height
    - `type: string` - chart type (should correspond to [chart types](#implemented-list))
    - `events?: Object` - handlers for chart events
      - `displayMarkingDialogue: Function` - displaying dialog window on selection
      - `removeSelection: () => void` - removing chart selection
      - `selection: (event: ChartMouseEvent) => boolean` - adding data selection ([ChartMouseEvent type](#interfaces-chartmouseevent))
      - `click: (event: ChartMouseEvent) => boolean` - handling click or double-click on chart ([ChartMouseEvent type](#interfaces-chartmouseevent))
    - `animationTime?: number` - scaling animation time in milliseconds (if set to 0 chart won't be animated)
    - `disableExport?: boolean` - should exporting be unavailable for this chart
    - `margins?: Margins` - margins from the borders of a svg to the actual chart elements ([interface](#interfaces-margins))
- `plotOptions?: Object`  
    - `markerSymbol?: string` - type of marker to use (currently only available option is 'diamond' for SimpleLineChart)
- `plotLines?: Array<PlotLine>` - a set of pre-determined lines to be drawn (e.g., level of y=5) ([interface](#interfaces-plotline))
- `yAxis?: PlotAxisConfig` - y axis configuration ([interface](#interfaces-plotaxisconfig))
- `xAxis?: PlotAxisConfig` - same as `yAxis`
- `tooltip?: Object` - configuration for chart tooltip ([interface](#interfaces-tooltip))


## <a name="interfaces"></a> Referenced interfaces and types

#### <a name="interfaces-chartmouseevent"></a> ChartMouseEvent type
- `ChartMouseEvent: d3.BrushEvent | MouseEvent` - any mouse event on chart (either d3 or native)

#### <a name="interfaces-selectionzone"></a> ZoneSelection interface
- `xMin: number` - start x coordinate of selected zone
- `xMax: number` - end x coordinate of selected zone
- `yMin: number` - start y coordinate of selected zone
- `yMax: number` - end y coordinate of selected zone

#### <a name="interfaces-tooltip"></a> ChartTooltip interface
- `formatter: () => void` - function to parse data from chart element to tooltip content (HTML)
- `onclick?: (element: HTMLElement, event: any) => void` - any specific handler for click on tooltip

#### <a name="interfaces-margins"></a> Margins interface
- `left?: number`
- `right?: number`
- `top?: number`
- `bottom?: number`
- `bottomWithRotatedLabels?: number` - maximum bottom margin when lower axis labels are rotated

#### <a name="interfaces-plotline"></a> PlotLine interface
- `axis?: string` - name of axis this line should be parallel to ('x' or 'y')
- `width?: number` - total length of a parallel line
- `value?: number` - line level (e.g., if `axis='x'` if would stand for y-axis value)
- `color?: string` - line color (any css)
- `x1, x2, x3, x4?: number` - if the line is not parallel to any of the axes it can be set by its start and end coordinates
- `styles?: Object` - object with any css styles to be applied to the line (e.g., `stroke-dasharray: 1`)

#### <a name="interfaces-plotaxisconfig"></a> PlotAxisConfig interface
- `title?: Object` - axis title parameters
  - `text: string` - axis title text
- `type?: string` - [axis type](#interfaces-axistype)
- `categories?: Array<string>` - list of all axis categories
- `borders?: { min: number, max: number }` - initial axis limits  

#### <a name="interfaces-axistype"></a> Axis types
- `ScaleTypes`
    - `LINEAR_SCALE` = `linear`
    - `LOGARITHMIC_SCALE` = `logarithmic`
    - `DATETIME_SCALE` = `datetime`
    - `CATEGORY_SCALE` = `category`