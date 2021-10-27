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
import {SentenceCasePipe} from '../../../../pipes';

import {TabId, TrellisCategory, TrellisDesign} from '../../../index';
import {AxisLabelService, Trellises} from './AxisLabelService';
import {YAxisParameters} from '../../../store';

@Injectable()
export class TitleService {

    constructor(private axisLabelService: AxisLabelService, private sentenceCasePipe: SentenceCasePipe) {
    }

    generateNewApproachTitle(trellisDesign: any, trellis: Trellises[], xAxisOption: string,
                             yAxisOption: string, preserveTitle: boolean, tabId?: TabId): string {

        let xAxisLabel: string;
        let yAxisLabel: string;
        const trellisingString: string = trellis ?
            trellis
                .filter(option => option.trellisedBy !== YAxisParameters.MEASUREMENT)
                .map(option => option.trellisOption).join(', ')
            : '';

        switch (trellisDesign) {
            case TrellisDesign.CONTINUOUS_OVER_TIME:
            case TrellisDesign.CATEGORICAL_OVER_TIME:
                xAxisLabel = this.axisLabelService.generateGlobalPlotLabel(xAxisOption, tabId);
                yAxisLabel = this.axisLabelService.generatePlotLabelY(trellisDesign, trellis, yAxisOption)
                    || this.axisLabelService.generateGlobalPlotLabel(yAxisOption, tabId);

                break;
            case TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES:
                xAxisLabel = this.axisLabelService.generateGlobalPlotLabel(xAxisOption, tabId);
                yAxisLabel = this.joinNotEmpty(
                    [
                        this.axisLabelService.generatePlotLabelY(trellisDesign, trellis, yAxisOption),
                        this.axisLabelService.generateGlobalPlotLabel(yAxisOption, tabId)
                    ],
                    ', ');

                break;
            case TrellisDesign.VARIABLE_Y_CONST_X:
            case TrellisDesign.VARIABLE_Y_VARIABLE_X:
                xAxisLabel = xAxisOption;
                yAxisLabel = yAxisOption;

                break;
            default:
                return '';
        }

        return this.composeTitle(xAxisLabel, yAxisLabel, trellisingString, preserveTitle);
    }

    private composeTitle(xAxisLabel, yAxisLabel, suffix, preserveTitle) {
        if (!preserveTitle) {
            yAxisLabel = this.sentenceCasePipe.transform(yAxisLabel);
        }
        xAxisLabel = this.sentenceCasePipe.transform(xAxisLabel);

        const title = `${yAxisLabel} vs. ${xAxisLabel}`;

        return this.joinNotEmpty([title, suffix], ', ');
    }

    private joinNotEmpty(arr, separator) {
        return arr.filter(item => !!item).join(separator);
    }

    generateTitle(trellisDesign: any, trellis: Trellises[], xAxisOption: string, yAxisOption: string, tabId?: TabId): string {
        let xAxisLabel: string;
        let yAxisLabel: string;
        let nonMandatoryTrellis: string;
        let nonMandatorySeries: string;
        switch (TrellisDesign[trellisDesign]) {
            case TrellisDesign.CONTINUOUS_OVER_TIME:
            case TrellisDesign.CATEGORICAL_OVER_TIME:
                xAxisLabel = this.axisLabelService.generateGlobalPlotLabel(xAxisOption, tabId);
                yAxisLabel = this.axisLabelService.generatePlotLabelY(trellisDesign, trellis, yAxisOption);
                nonMandatoryTrellis = this.generateNonMandatoryTrellisTitle(trellis);
                return this.sentenceCasePipe.transform(yAxisLabel + ' vs. ' + xAxisLabel) + nonMandatoryTrellis;
            case TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES:
                xAxisLabel = this.axisLabelService.generateGlobalPlotLabel(xAxisOption, tabId);
                yAxisLabel = this.axisLabelService.generatePlotLabelY(trellisDesign, trellis, yAxisOption) ?
                    this.axisLabelService.generatePlotLabelY(
                        trellisDesign, trellis, yAxisOption) + ', ' + this.axisLabelService.generateGlobalPlotLabel(yAxisOption, tabId) :
                    this.axisLabelService.generateGlobalPlotLabel(yAxisOption, tabId);
                nonMandatoryTrellis = this.generateNonMandatoryTrellisTitle(trellis);
                nonMandatorySeries = this.generateNonMandatorySeriesTitle(trellis);
                return this.sentenceCasePipe.transform(yAxisLabel + ' vs. ' + xAxisLabel) + nonMandatoryTrellis + nonMandatorySeries;
            case TrellisDesign.VARIABLE_Y_CONST_X:
            case TrellisDesign.VARIABLE_Y_VARIABLE_X:
                nonMandatoryTrellis = this.generateNonMandatoryTrellisTitle(trellis);
                return this.sentenceCasePipe.transform(yAxisOption + ' vs. ' + xAxisOption) + nonMandatoryTrellis;
            default:
                return '';
        }
    }

    private generateNonMandatoryTrellisTitle(trellis: Trellises[]): string {
        const nonMandatoryTrellisItems: string[] = trellis.filter(
            trellisItem => trellisItem.category === TrellisCategory.NON_MANDATORY_TRELLIS).map(x => x.trellisOption);
        return nonMandatoryTrellisItems.length > 0 ? ', ' + nonMandatoryTrellisItems.join(', ') : '';
    }

    private generateNonMandatorySeriesTitle(trellis: Trellises[]): string {
        const nonMandatorySeriesItems: string[] = trellis.filter(
            trellisItem => trellisItem.category === TrellisCategory.NON_MANDATORY_SERIES).map(x => x.trellisedBy);
        return nonMandatorySeriesItems.length > 0 ? ', ' + nonMandatorySeriesItems.join(', ') : '';
    }
}
