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

import {Injectable} from '@angular/core';
import {isNil} from 'lodash';
import {List} from 'immutable';

import {DynamicAxis, IPlot, PlotSettings, PlotType, TabId, TrellisDesign} from '../../store/ITrellising';
import {TitleService} from './services/TitleService';
import {GroupBySetting} from '../../store/actions/TrellisingActionCreator';
import {getSelectedOption} from '../../axislabel/newxaxis/XAxisLabelService';
import {DatasetViews} from '../../../../security/DatasetViews';
import {ScaleTypes, YAxisParameters} from '../../store';
import {LogarithmicScalePipe, SentenceCasePipe, SettingsPipe} from '../../../pipes';
import {TextUtils} from '../../../module';
import TrellisedScatterPlot = InMemory.TrellisedScatterPlot;
import TrellisedShiftPlot = InMemory.TrellisedShiftPlot;

@Injectable()
export class PlotService {

    constructor(private titleService: TitleService,
                private datasetViews: DatasetViews,
                private logarithmicScalePipe: LogarithmicScalePipe,
                private settingsPipe: SettingsPipe,
                private sentenceCasePipe: SentenceCasePipe) {
    }

    /**
     * OLD APPROACH
     *
     * @param {IPlot} plot - plot object containing plot type, data and trellising
     * @param {TrellisDesign} trellisDesign - design of trellising for this plot(CONT_OVER_TIME, etc.)
     * @param {DynamicAxis} xAxisOption - selected x axis option
     * @param {string} yAxisOption - selected y axis option
     * @param {TabId} tabId - current tab id
     * @returns {string} - returns plot title containing both axes names
     */
    getPlotTitle(plot: IPlot, trellisDesign: TrellisDesign, xAxisOption: DynamicAxis, yAxisOption: string, tabId: TabId): string {
        let trellising: any;
        if (plot) {
            trellising = plot.get('trellising');
        }
        if (xAxisOption && yAxisOption && trellisDesign && trellising) {
            let xAxisOptionString = xAxisOption.get('stringarg', false)
                ? xAxisOption.get('value') + ' ' + xAxisOption.get('stringarg')
                : xAxisOption.get('value');
            let yAxisOptionString = yAxisOption;
            if (!isNil(plot.get('data')) && (plot.get('plotType') === PlotType.SCATTERPLOT)) {
                const plotData: TrellisedScatterPlot<any, any> = plot.get('data').toJS();
                xAxisOptionString = plotData.xaxisLabel;
                yAxisOptionString = plotData.yaxisLabel;
            }
            return this.titleService.generateTitle(trellisDesign, trellising, xAxisOptionString, yAxisOptionString, tabId);
        }
        return;
    }

    /**
     *
     * @param {IPlot} plot - plot object containing plot type, data and trellising
     * @param {TrellisDesign} trellisDesign - design of trellising for this plot(CONT_OVER_TIME, etc.)
     * @param {GroupBySetting} xAxisOption - selected x axis option
     * @param {GroupBySetting} yAxisOption - selected y axis option
     * @param {TabId} tabId - current tab id
     * @param {ScaleTypes} scaleType - current scaleType (logarithmic, linear)
     * @param {boolean} preserveTitle - if title should be preserved for this tab
     * @param {List} plotSettings - current settings that are applied. We have it here to add them to title if needed
     * @returns {string} - returns plot title containing both axes names
     */
    getNewApproachPlotTitle(plot: IPlot, trellisDesign: TrellisDesign, xAxisOption: GroupBySetting,
                            yAxisOption: GroupBySetting, tabId: TabId, scaleType: ScaleTypes,
                            preserveTitle: boolean, plotSettings: PlotSettings, logarithmicScaleRequired: boolean,
                            settingLabelRequired: boolean): string {
        let trellising: any;
        if (plot) {
            trellising = plot.get('trellising');
        }
        const selectedXAxis = getSelectedOption(xAxisOption);
        const selectedYAxis = getSelectedOption(yAxisOption);
        if (selectedXAxis && selectedYAxis && trellisDesign && trellising) {
            let xAxisOptionString = selectedXAxis.params && selectedXAxis.params.DRUG_NAME
                ? selectedXAxis.displayedOption + ' ' + selectedXAxis.params.DRUG_NAME
                : selectedXAxis.displayedOption;
            let yAxisOptionString = selectedYAxis.displayedOption;
            if (!isNil(plot.get('data'))
                && (plot.get('plotType') === PlotType.SCATTERPLOT || plot.get('plotType') === PlotType.ERRORPLOT)) {
                const plotUnit = plot.getIn(['data', 'unit']);
                xAxisOptionString = plotUnit
                    ? xAxisOptionString + (plotUnit.toLowerCase() === '(empty)' ? `${plotUnit}` : `(${plotUnit})`)
                    : xAxisOptionString;
                yAxisOptionString = trellising ? trellising.filter(option => option.trellisedBy === YAxisParameters.MEASUREMENT)
                    .map(option => option.trellisOption).join(',') : yAxisOptionString;
            }
            const trellisingParams = selectedYAxis.params ? selectedYAxis.params.get('trellisingParams') : undefined;
            if (trellisingParams && trellisingParams.get(YAxisParameters.MEASUREMENT_TIMEPOINT)) {
                const additionalOptions = TextUtils.addDescriptionBeforeTimepoints(this.datasetViews.getTimepointType(),
                    trellisingParams.get(YAxisParameters.MEASUREMENT_TIMEPOINT));
                yAxisOptionString = `${trellisingParams.get(YAxisParameters.MEASUREMENT)} ${additionalOptions}`;
            }
            if (settingLabelRequired) {
                const settingString = plotSettings.get('trellisedBy');
                yAxisOptionString = this.settingsPipe.transform(yAxisOptionString, false, settingString);
                yAxisOptionString += `_${this.settingsPipe.transform(settingString, true)}`;
            }

            let yAxisString =  this.titleService.generateNewApproachTitle(trellisDesign, trellising,
                xAxisOptionString, yAxisOptionString, preserveTitle, tabId);
            if (logarithmicScaleRequired) {
                yAxisString = this.logarithmicScalePipe.transform(yAxisString, scaleType, preserveTitle);
            }
            return yAxisString;
        }
        return;
    }
}
