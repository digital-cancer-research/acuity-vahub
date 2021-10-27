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

import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {List} from 'immutable';
import {Store} from '@ngrx/store';

import {
    DynamicAxis,
    IMultiSelectionDetail,
    IPlot,
    ISelectionDetail,
    IZoom,
    PlotType,
    TabId,
    TrellisDesign
} from '../../index';
import {AxisLabelService} from './services/AxisLabelService';
import {DialogueLocation} from './DialogueLocation';
import {MarkingDialogue} from './markingdialogue';
import {TrellisingObservables} from '../../store/observable/TrellisingObservables';
import {TrellisingDispatcher} from '../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../store/Trellising';
import {IChartSelection, PlotSettings, ScaleTypes, TrellisCategory} from '../../store';
import {PlotService} from './PlotService';
import {GroupBySetting} from '../../store/actions/TrellisingActionCreator';
import {getSelectedOption} from '../../axislabel/newxaxis/XAxisLabelService';
import {ApplicationState} from '../../../store/models/ApplicationState';
import {UpdateActiveTabId} from '../../../store/actions/SharedStateActions';

@Component({
    selector: 'trellising-plot',
    templateUrl: 'PlotComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./PlotComponent.css']
})
export class PlotComponent implements AfterViewInit, OnDestroy {
    plotType = PlotType;
    markingDialogue: MarkingDialogue;
    pageSubscription: Subscription;
    currentPageNum = 1;

    @Input() trellisDesign: TrellisDesign;
    @Input() xAxisOption: DynamicAxis;
    @Input() yAxisOption: string;
    @Input() newXAxisOption: GroupBySetting;
    @Input() newYAxisOption: GroupBySetting;
    @Input() xAxisZoom: IZoom;
    @Input() yAxisZoom: IZoom;
    @Input() yRange: { min: number, max: number };
    @Input() xRange: { min: number, max: number };
    @Input() plot: IPlot;
    @Input() height: number;
    @Input() tabId: TabId;
    @Input() parentTabId: TabId;
    @Input() selection: List<IChartSelection>;
    @Input() selectionDetail: ISelectionDetail;
    @Input() multiSelectionDetail: IMultiSelectionDetail;
    @Input() scaleType: ScaleTypes;
    @Input() preserveTitle: boolean;
    @Input() plotSettings: PlotSettings;
    @Input() logarithmicScaleRequired: boolean;
    @Input() settingLabelRequired: boolean;

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellising: Trellising,
                private axisLabelService: AxisLabelService,
                private plotService: PlotService,
                private _store: Store<ApplicationState>) {

    }

    ngAfterViewInit(): void {
        this.pageSubscription = this.listenToPageChanged();
        if (this.parentTabId) {
            this._store.dispatch(new UpdateActiveTabId(this.parentTabId));
        }
    }

    ngOnDestroy(): void {
        if (this.pageSubscription) {
            this.pageSubscription.unsubscribe();
        }
    }

    /**
     * Checks if current axis is deterministic or continuous
     * @returns {boolean}
     */
    isCategorical(): boolean {
        switch (this.trellisDesign) {
            case TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES:
            case TrellisDesign.CATEGORICAL_OVER_TIME:
                return true;
            default:
                return false;
        }
    }

    /**
     * Get height of container for no data message
     * @returns {string}
     */
    lineHeight(): string {
        return this.height ? this.height.toString() + 'px' : '0px';
    }

    /**
     * Get top offset for no data message container
     * @returns {string}
     */
    lineTop(): string {
        return this.height ? (this.height / 2.0).toString() + 'px' : '0px';
    }

    /**
     * Get plot title containing axes labels
     * @returns {string}
     */
    title(): string {
        if (this.trellisingObservables.isNewApproachTab(this.tabId)) {
            return this.plotService.getNewApproachPlotTitle(this.plot, this.trellisDesign, this.newXAxisOption,
                this.newYAxisOption, this.tabId, this.scaleType, this.preserveTitle, this.plotSettings,
                this.logarithmicScaleRequired, this.settingLabelRequired);
        } else {
            return this.plotService.getPlotTitle(this.plot, this.trellisDesign, this.xAxisOption, this.yAxisOption, this.tabId);
        }
    }

    /**
     * Pointless method
     * @returns {string}
     */
    xAxisLabel(): string {
        return;
    }

    /**
     * Get x axis label for the whole tab
     * @returns {string}
     */
    globalXAxisLabel(): string {
        if (this.trellisDesign) {
            if (this.xAxisOption && !this.trellisingObservables.isNewApproachTab(this.tabId)) {
                const xAxisOption = this.xAxisOption.get('stringarg', false)
                    ? this.xAxisOption.get('value') + this.xAxisOption.get('stringarg')
                    : this.xAxisOption.get('value');
                return this.axisLabelService.generateGlobalPlotLabel(xAxisOption, this.tabId);
            }
            if (this.newXAxisOption && this.trellisingObservables.isNewApproachTab(this.tabId)) {
                const selectedXAxis = getSelectedOption(this.newXAxisOption);
                const xAxisOption = selectedXAxis.params && selectedXAxis.params.DRUG_NAME
                    ? selectedXAxis.displayedOption + ' ' + selectedXAxis.params.DRUG_NAME
                    : selectedXAxis.displayedOption;
                return this.axisLabelService.generateGlobalPlotLabel(xAxisOption, this.tabId);
            }
        }
        return;
    }

    /**
     * Get y axis label for particular chart
     * @returns {string}
     */
    yAxisLabel(): string {
        //no yAxisLabel for Dose Proportionality boxplot by now
        if (this.trellisingObservables.getAdvancedAxisControlRequired(this.tabId)) {
            return '';
        }
        let trellising: any;
        if (this.plot) {
            trellising = this.plot.get('trellising');
        }
        if (this.trellisDesign && trellising) {
            if (this.yAxisOption && !this.trellisingObservables.isNewApproachTab(this.tabId)) {
                return this.axisLabelService.generatePlotLabelY(this.trellisDesign, trellising, this.yAxisOption);
            }
            if (this.newYAxisOption && this.trellisingObservables.isNewApproachTab(this.tabId)) {
                return this.axisLabelService.generatePlotLabelY(this.trellisDesign, trellising,
                    getSelectedOption(this.newYAxisOption).displayedOption);
            }
        }
        return;
    }

    /**
     * Get y axis label for the whole tab
     * @returns {string}
     */
    globalYAxisLabel(): string {
        if (this.trellisDesign) {
            if (this.yAxisOption && !this.trellisingObservables.isNewApproachTab(this.tabId)) {
                return this.axisLabelService.generateGlobalPlotLabel(this.yAxisOption, this.tabId);
            }
            if (this.newYAxisOption && this.trellisingObservables.isNewApproachTab(this.tabId)) {
                return this.axisLabelService.generateGlobalPlotLabel(getSelectedOption(this.newYAxisOption).displayedOption,
                    this.tabId, this.trellisingObservables.getPreserveGlobalTitle(this.tabId));
            }
        }
        return;
    }

    /**
     * Get currently selected colorby value
     * @returns {string} color by value
     */
    getColorByValue(): string {
        const colorBy = this.trellisingObservables.getCurrentTrellisingByTabId(this.tabId)
            .find(serie => serie.get('category') === TrellisCategory.NON_MANDATORY_SERIES);
        return colorBy.get('trellisedBy');
    }

    /**
     * Get scale type. At first we have zeros in zoom, so we need to check
     * if there are any zero and negative values in case of logarithmic scale
     * @returns {any}
     */
    getScaleType(): ScaleTypes {
        if (this.scaleType === ScaleTypes.LOGARITHMIC_SCALE) {
            switch (this.tabId) {
                case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
                case TabId.PK_RESULT_OVERALL_RESPONSE:
                    return this.yRange.min === 0 ? ScaleTypes.LINEAR_SCALE : ScaleTypes.LOGARITHMIC_SCALE;
                default:
                    return this.yAxisZoom.get('absMin') === 0 ? ScaleTypes.LINEAR_SCALE : ScaleTypes.LOGARITHMIC_SCALE;
            }
        } else {
            return this.scaleType;
        }
    }

    /**
     * Callback called from diff chart components to show marking dialogue on some location
     * @param {DialogueLocation} location - new location containing x and y coordinates
     */
    displayMarkingDialogue(location: DialogueLocation): void {
        if (!location) {
            this.hideMarkingDialogue();
        } else {
            this.showMarkingDialogue({x: location.x, y: location.y});
        }
    }

    listenToPageChanged(): Subscription {
        return this.trellisingObservables.page.subscribe((pageNum: number) => {
            if (this.currentPageNum !== pageNum) {
                this.currentPageNum = pageNum;
                this.hideMarkingDialogue();
            }
        });
    }

    private showMarkingDialogue(location: {x: number, y: number}): void {
        this.markingDialogue = {
            ...location,
            show: true
        };
    }

    private hideMarkingDialogue(): void {
        this.markingDialogue = null;
    }
}
