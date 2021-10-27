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

import {
    ChangeDetectionStrategy,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    NgZone,
    OnChanges,
    OnDestroy,
    Output,
    SimpleChange
} from '@angular/core';
import {fromJS, List, Map, OrderedMap} from 'immutable';
import {cloneDeep, find, isEqual, orderBy, toPairs} from 'lodash';


import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {ChordDiagramConfigService} from './ChordDiagramConfigService';
import {
    IChartSelection,
    ITrellis,
    ITrellises,
    PlotSettings,
    TabId
} from '../../../../store';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {hexToRgba} from '../../../../../CommonChartUtils';
import {ChordDiagram} from '../../../../../../../vahub-charts/chord/ChordDiagram';
import {ChartMouseEvent, Series, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

export interface ChordData {
    start: string;
    end: string;
    width: number;
    contributors: Map<string, number>;
    startIndex?: number;
    endIndex?: number;
}

@Component({
    selector: 'chord-diagram',
    templateUrl: 'ChordDiagramComponent.html',
    styleUrls: ['ChordDiagramComponent.css'],
    providers: [ChordDiagramConfigService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChordDiagramComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {

    @Input() plotData: List<ChordData>;

    @Input() title: string;
    @Input() series: List<ITrellises>;
    @Input() height: number;
    @Input() tabId: TabId;
    @Input() selection: List<IChartSelection>;
    @Input() trellising: List<ITrellis>;
    @Input() plotSettings: PlotSettings;
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();

    chart: ChordDiagram;
    public percentageOfWidestChord: number;
    private chordData: List<Map<string, ChordData>>;
    private colorMap: Map<string, string>;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: ChordDiagramConfigService,
                sessionEventService: SessionEventService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData || changes['plotSettings'] && this.plotSettings
            && changes['plotSettings'].previousValue.get('trellisedBy') !== this.plotSettings.get('trellisedBy')) {
            this.updateChart();
        }
        if (changes['height'] && this.height) {
            this.updateHeight();
        }
        if (changes['selection'] && this.selection) {
            this.updateSelectionRendering();
        }

    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    updateZoomX(): void {
    }

    updateZoomY(): void {
    }

    protected createPlotConfig(): UserOptions {
        return this.chartPlotconfigService.createPlotConfig(
            this.title,
            this.height,
            this.plotSettings
        );
    }

    protected updateChart(): void {
        if (!this.chart) {
            this.createChart();
        } else {
            this.updateChartConfig();
        }
        if (this.chart && this.plotData) {
            const that = this;
            const trellisedBySetting = this.plotSettings.get('trellisedBy');
            this.chordData = this.plotData.getIn([0, trellisedBySetting, 'data']);
            if (this.chart.series.length > 0) {
                this.chart.clearSeries();
            }
            if (this.plotData.getIn([0, trellisedBySetting]).isEmpty() || this.chordData.isEmpty()) {
                // we have to deal with it here as we can have no data for one type of settings, but not for others
                this.chart.setNoDataMessage('No data to display');
                return;
            }
            const rawData: ChordData[] = this.chordData.toJS();
            this.colorMap = this.plotData.getIn([0, trellisedBySetting, 'colorBook']);
            const pieData = [];
            // TODO: refactoring and extracting to service
            let categories = OrderedMap<string, string[]>();
            rawData.forEach((line: ChordData) => {
                const startValue = categories.get(line.start) || [];
                startValue.push(`connects_${line.end}`);
                categories = categories.set(line.start, startValue);
            });
            rawData.forEach((line: ChordData) => {
                const endValue = categories.get(line.end) || [];
                endValue.push(`connects_${line.start}`);
                categories = categories.set(line.end, endValue);
            });
            const groupedCategories = [];
            let k = 0;
            categories.map((value, key) => {
                groupedCategories.push({name: key, categories: value});
                let lineWidthsSum = 0;
                rawData.forEach((line: ChordData) => {
                    if (line.start === key) {
                        line.startIndex = k;
                        k++;
                        lineWidthsSum++;
                    }
                });
                rawData.forEach((line: ChordData) => {
                    if (line.end === key) {
                        line.endIndex = k;
                        k++;
                        lineWidthsSum++;
                    }
                });
                pieData.push({
                    name: key,
                    y: lineWidthsSum,
                    color: this.colorMap.get(key)
                });
            });
            this.chart.xAxis[0].setCategories(groupedCategories);

            this.percentageOfWidestChord = this.plotSettings.getIn(['trellisOptions', 'percentageOfLinks']);

            rawData.forEach((line: ChordData) => {
                this.chart.addSeries({
                    name: {start: line.start, end: line.end},
                    color: hexToRgba(this.colorMap.get(line.start), '0.5'),
                    value: line.width,
                    subjects: orderBy(toPairs(line.contributors), ['1'], ['desc']),
                    events: {
                        click: function (event: ChartMouseEvent): boolean {
                            return that.clickWithDoubleClickEvent(event);
                        }
                    },
                    data: [{x: line.startIndex, y: 1}, {x: line.endIndex, y: 1}]
                });
            });
            this.chart.addSeries({
                type: 'pie',
                innerSize: '90%',
                size: '100%',
                data: pieData
            });
            this.chart.redraw();
        }
    }

    protected applySelection(): void {
        if (this.chart) {
            if (isEqual(this.selection, this.currentSelection)) {
                return;
            }
            const selectedColumns = this.selection.toJS().reduce((acc, val) => [...acc, ...val.columns], []);
            this.chart.series.forEach(serie => {
                const isSelected = selectedColumns.findIndex(sel => sel.start === serie.name.start && sel.end === serie.name.end) !== -1;
                serie.select(isSelected);
            });
            this.chart.update();
        }
    }

    protected removeSelection(): void {
        this.chart.series.forEach(serie => {
            serie.select(false);
        });
        this.chart.update();
    }

    protected handleClickEvent(event: ChartMouseEvent): boolean {
        const { chordData, mouseEvent } = event;
        if (event.chordData.selected) {
            this.displayMarkingDialogue.emit({x: mouseEvent.layerX, y: mouseEvent.layerY});
            return false;
        }
        let selection: List<IChartSelection>;
        const selectedSerie = find(this.chart.series, function (serie: Series) {
            return chordData.name.start === serie.name.start && chordData.name.end === serie.name.end;
        });
        if (!selectedSerie) {
            return false;
        }
        const selectedChord = {
            start: selectedSerie.name.start,
            end: selectedSerie.name.end,
        };
        let newSelectedChords;
        //Appending selection
        if (mouseEvent.ctrlKey) {
            newSelectedChords = this.currentSelection && this.currentSelection.get('columns')
                ? cloneDeep(this.currentSelection.get('columns'))
                : [];
            newSelectedChords.push(selectedChord);

            const selectionInPlot = fromJS({
                trellising: this.trellising,
                range: null,
                bars: null,
                columns: newSelectedChords
            });
            if (this.selection && this.selection.size > 0) {
                selection = this.selection.push(selectionInPlot);
            } else {
                selection = List.of(selectionInPlot);
            }
        } else {
            selection = List.of(fromJS({
                trellising: this.trellising,
                range: null,
                bars: null,
                columns: [selectedChord]
            }));
        }
        this.trellisingMiddleware.updateSelection(selection);
        this.displayMarkingDialogue.emit({x: mouseEvent.layerX, y: mouseEvent.layerY});

        return false;
    }

    protected handleDblclickEvent(event: ChartMouseEvent): boolean {
        const selectedChords = this.chordData.toJS().map(line => {
            return {
                start: line.start,
                end: line.end,
            };
        });
        const selection = List.of(fromJS({
            trellising: this.trellising,
            range: null,
            bars: null,
            columns: selectedChords
        }));
        this.trellisingMiddleware.updateSelection(selection);
        this.displayMarkingDialogue.emit({x: event.layerX, y: event.layerY});
        return false;
    }

}
