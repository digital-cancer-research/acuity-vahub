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
import {List, Map} from 'immutable';
import {isEmpty} from 'lodash';

import {DisplayedGroupBySetting, GroupBySetting, XAxisOptions} from '../../store/actions/TrellisingActionCreator';
import {TabId} from '../../store';
import {TextUtils} from '../../../module';
import * as _ from 'lodash';

@Injectable()
export class NewXAxisLabelService {
}

interface TimestampOptionsInfo {
    names: string[];        // names of axes (more than one when all other options are the same and options are next to one another)
    whiteList?: string[];   // list of tabs that should have these axes (optional; if undefined, no whitelist check performed)
    blackList?: string[];   // list of tabs that shouldn't have these axes (optional; if undefined, no blacklist check performed)
    specialCheck?: (options: XAxisOptions) => boolean; // any specific check defining whether this axis should be displayed on tab or not
    drugAware?: boolean;    // defines if the axis has drug options
}

const timestampOptionsInfo: TimestampOptionsInfo[] = [
    {names: ['DATE'], whiteList: [TabId.CVOT_ENDPOINTS_OVER_TIME, TabId.CEREBROVASCULAR_EVENTS_OVER_TIME, TabId.CI_EVENT_OVERTIME]},
    {names: ['DAYS_SINCE_FIRST_DOSE']},
    {names: ['WEEKS_SINCE_FIRST_DOSE'], blackList: [TabId.CTDNA_PLOT] },
    {
        names: ['DAYS_SINCE_RANDOMISATION', 'WEEKS_SINCE_RANDOMISATION'],
        blackList: [ TabId.CTDNA_PLOT],
        specialCheck: options => options.get('hasRandomization')
    },
    {
        names: ['DAYS_SINCE_FIRST_DOSE_OF_DRUG', 'WEEKS_SINCE_FIRST_DOSE_OF_DRUG'],
        whiteList: [TabId.CVOT_ENDPOINTS_OVER_TIME, TabId.CEREBROVASCULAR_EVENTS_OVER_TIME, TabId.CI_EVENT_OVERTIME],
        specialCheck: options => options.get('drugs') && options.get('drugs').size > 0,
        drugAware: true
    }
];

export function generateAvailableOptions(options: XAxisOptions, tabId?: TabId): DisplayedGroupBySetting[] {
    const result = <DisplayedGroupBySetting[]>[];

    /**
     * We need to add the option on the frontend
     */
    if (_.includes([TabId.CVOT_ENDPOINTS_COUNTS, TabId.CI_EVENT_COUNTS, TabId.CEREBROVASCULAR_COUNTS], tabId)) {
        result.push(<DisplayedGroupBySetting>{
            displayedOption: 'NONE',
            groupByOption: 'NONE',
            params: {}
        });
    }

    options.get('options').forEach((option: Map<string, any>) => {
        if (option.get('timestampOption')) {
            // there is an option (like ctDNA plot, when x is timestamp option, but does not require bin
            const binSize = option.get('binableOption') ? 1 : undefined;
            timestampOptionsInfo.forEach(optionsInfo => {
                if ((!optionsInfo.specialCheck || optionsInfo.specialCheck(options))
                    && (!optionsInfo.whiteList || optionsInfo.whiteList.indexOf(<string>tabId) > -1)
                    && (!optionsInfo.blackList || optionsInfo.blackList.indexOf(<string>tabId) < 0)) {
                    optionsInfo.names.forEach(name => {
                        const params = {
                            TIMESTAMP_TYPE: name,
                            BIN_SIZE: binSize
                        };
                        if (optionsInfo.drugAware) {
                            params['DRUG_NAME'] = options.get('drugs').get(0);
                        }
                        result.push(<DisplayedGroupBySetting>{
                            displayedOption: name,
                            groupByOption: option.get('groupByOption'),
                            params: params
                        });
                    });
                }
            });
        } else {
            result.push(<DisplayedGroupBySetting>{
                displayedOption: option.get('groupByOption'),
                groupByOption: option.get('groupByOption'),
                params: option.get('binableOption') ? { BIN_SIZE: 1 } : {}
            });
        }
    });

    return result;
}

export function getSelectedOption(option: GroupBySetting): DisplayedGroupBySetting {
    if (option.get('params') && option.get('params').get('TIMESTAMP_TYPE')) {
        // TODO a magic number! please get rid of it if you can. there is a suspection that it isn't used at all in fact...
        const daysSinceFirstDoseOptionName = timestampOptionsInfo[1].names[0];
        return <DisplayedGroupBySetting>{
            displayedOption: option.get('params').get('TIMESTAMP_TYPE'),
            groupByOption: option.get('groupByOption'),
            params: option.get('params') ? option.get('params').toJS() :
                {
                    TIMESTAMP_TYPE: daysSinceFirstDoseOptionName,
                    BIN_SIZE: 1
                }
        };
    } else {
        return <DisplayedGroupBySetting>  {
            displayedOption: option.get('groupByOption'),
            groupByOption: option.get('groupByOption'),
            params: option.get('params')
        };
    }
}

export function getSelectedOptionWithTrellising(option: GroupBySetting, trellisingAddition: any,
                                                timepointType: string): DisplayedGroupBySetting {

    const addition = TextUtils.addDescriptionBeforeTimepoints(timepointType, trellisingAddition);
    const displayedOption = !isEmpty(trellisingAddition) ? `${option.get('groupByOption')} ${addition}`
        : option.get('groupByOption');
    return <DisplayedGroupBySetting>{
        displayedOption,
        groupByOption: option.get('groupByOption'),
        params: {}
    };
}

export function getTrellisingAxisOption(option: any, type: string): any {
    return option.getIn(['params', 'trellisingParams', type]);
}

export function generateAvailableOptionsWithTrellising(options: any): List<DisplayedGroupBySetting> {
    return options.first().getIn(['groupByOption', 'trellisOptions']).map((option: string) => <DisplayedGroupBySetting>{
        displayedOption: option,
        groupByOption: option,
        params: {}
    });
}

export function generateAvailableTimepointOptions(options: any, selectedOption: string): any {
    return options.map((option: Map<string, any>) => option.getIn(['groupByOption', 'timepointsByParameter', selectedOption])).first();
}

export function addDisplayedOption(option: GroupBySetting, displayedOption: string): DisplayedGroupBySetting {
    return <DisplayedGroupBySetting>{
        displayedOption,
        groupByOption: option.get('groupByOption'),
        params: {}
    };
}
