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
import {find} from 'lodash';
import {TrellisDesign, TrellisCategory, YAxisOptions, TabId} from '../../../index';
import {SentenceCasePipe} from '../../../../pipes/SentenceCasePipe';
import {LabelPipe} from '../../../../pipes/LabelPipe';
import {YAxisParameters} from '../../../store';
export interface Trellises {
    category: TrellisCategory;
    trellisedBy: string;
    trellisOption: string;
}

@Injectable()
export class AxisLabelService {
    constructor(private sentenceCasePipe: SentenceCasePipe,
                private analysisVisitPipe: LabelPipe) {

    }

    /**
     *  Generate the plot label for y axis
     *  # TODO: get units
     */
    public generatePlotLabelY(trellisDesign: TrellisDesign, trellis: Trellises[], yAxisOption: string): string {
        if (!trellis || trellis.length === 0) {
            return;
        }
        let label: string;
        switch (trellisDesign) {
            case TrellisDesign.CONTINUOUS_OVER_TIME:
            case TrellisDesign.CATEGORICAL_OVER_TIME:
                if (find(trellis, {'trellisedBy': 'ANALYTE'})) {
                    return yAxisOption;
                }
                label = find(trellis, {'trellisedBy': YAxisParameters.MEASUREMENT}).trellisOption;
                return this.yAxisLabelForContinuousOverTime(label, yAxisOption);
            case TrellisDesign.VARIABLE_Y_VARIABLE_X:
                label = find(trellis, {'trellisedBy': YAxisParameters.MEASUREMENT}).trellisOption;
                return this.yAxisLabelForContinuousOverTime(label, yAxisOption);
            case TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES:
                label = find(trellis, {'trellisedBy': YAxisParameters.MEASUREMENT})
                    ? find(trellis, {'trellisedBy': YAxisParameters.MEASUREMENT}).trellisOption : undefined;
                if (!label) {
                    return;
                } else {
                    return label;
                }
            default:
                return;
        }
    }

    public generateGlobalPlotLabel(axisOption: string, tabId: TabId, preserveTitle?: boolean): string {
        axisOption = this.analysisVisitPipe.transform(axisOption, tabId);
        return preserveTitle ? axisOption : this.sentenceCasePipe.transform(axisOption);
    }

    private yAxisLabelForContinuousOverTime(label: string, yAxisOption: string): string {
        //Check label already has units
        const matches: string[] = label.match(/\((.*?)\)/g);

        switch (YAxisOptions[yAxisOption]) {
            case YAxisOptions.ACTUAL_VALUE:
                // label += (matches ? '' : ' (unit)');
                break;
            case YAxisOptions.ABSOLUTE_CHANGE_FROM_BASELINE:
                if (!matches) {
                    label += ' (abs change)';
                } else {
                    label = label.replace(matches[0], '(abs change in ' + matches[0].slice(1, matches[0].length - 1) + ')');
                }
                break;
            case YAxisOptions.PERCENTAGE_CHANGE_FROM_BASELINE:
                if (!matches) {
                    label += ' (% change)';
                } else {
                    label = label.replace(matches[0], '(% change)');
                }
                break;
            case YAxisOptions.REF_RANGE_NORM_VALUE:
                if (!matches) {
                    label += ' (Ref. Ranges)';
                } else {
                    label = label.replace(matches[0], '(Ref. Ranges)');
                }
                break;
            case YAxisOptions.TIMES_LOWER_REF_VALUE:
                if (!matches) {
                    label += ' (Times Lower Ref.)';
                } else {
                    label = label.replace(matches[0], '(Times Lower Ref.)');
                }
                break;
            case YAxisOptions.TIMES_UPPER_REF_VALUE:
                if (!matches) {
                    label += ' (Times Upper Ref.)';
                } else {
                    label = label.replace(matches[0], '(Times Upper Ref.)');
                }
                break;
            default:
                break;
        }
        return label;
    }

}
