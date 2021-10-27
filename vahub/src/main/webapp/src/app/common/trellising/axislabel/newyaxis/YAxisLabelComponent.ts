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

import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {fromJS, List} from 'immutable';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {omit} from 'lodash';
import {AxisLabelComponent} from '../AxisLabelComponent';
import {AxisLabelService} from '../AxisLabelService';
import {NO_AVAILABLE_PARAMETERS, PlotSettings, TabId, TimepointConstants, YAxisParameters} from '../../store';
import {DisplayedGroupBySetting, GroupBySetting, XAxisOptions} from '../../store/actions/TrellisingActionCreator';
import {
    addDisplayedOption,
    generateAvailableOptions,
    generateAvailableOptionsWithTrellising,
    generateAvailableTimepointOptions,
    getSelectedOption,
    getSelectedOptionWithTrellising,
    getTrellisingAxisOption
} from '../newxaxis/XAxisLabelService';
import {DatasetViews} from '../../../../security/DatasetViews';
import {SettingsPipe} from '../../../pipes';
import {TrellisingObservables} from '../../store/observable/TrellisingObservables';


@Component({
    selector: 'new-trellis-yaxis',
    templateUrl: 'AxisLabelComponent.html',
    styleUrls: ['YStyle.css'],
    animations: [
        trigger('enterLeaveTrigger', [
            transition(':enter', [
                style({
                    opacity: '0',
                    left: '0%'
                }),
                animate('400ms', style({
                    opacity: 1,
                    left: '50%'
                })),
            ]),
            transition(':leave', [
                animate('400ms', style({
                    opacity: 0,
                    left: '0%'
                }))
            ])
        ])
    ]
})
export class NewYAxisLabelComponent extends AxisLabelComponent implements OnInit, OnDestroy, OnChanges {
    tabID = TabId;
    @Input() option: GroupBySetting; // GroupBySetting
    @Input() options: XAxisOptions; // InMemory.AxisOptions
    @Input() tabId: TabId;
    @Output() update: EventEmitter<GroupBySetting> = new EventEmitter<GroupBySetting>();
    @Input() advancedMeasuredControlRequired: boolean;
    @Input() advancedAxisControlRequired: boolean;
    @Input() plotSettings: PlotSettings;
    @Input() settingLabelRequired: boolean;
    @Input() isTableControl: boolean;

    protected measureNumberOption: string;
    protected measureNumberOptions: string[] = null;
    protected unitOption: string;
    protected unitOptions: string[] = null;
    private readonly hasNotEmptyCycleAndDay: boolean;
    private settingString: string;

    //type of timepoint: one of TimepointConstants
    protected timepointType: string;

    //selectedTrellisAxisOptions and availableTrellisAxisOptions is used for PK Summary plots (advancedAxisControlRequired = true)
    //So please check timepoint and title of yaxis and plot if you change something related to these plots
    // List of 2 elements (0: "cycle 1", 1: "day 3") for CYCLE_DAY or "cycle 1 day 1" (just string) otherwise
    protected selectedTrellisAxisOptions: any = null;
    //Map<String, List<String>> for CYCLE_DAY. for example, {cycle1: List(day 1, day 2), cycle2: List(day 2, day 3)}
    //Because list of days should be consistent with selected cycle and these days can be taken by required cycle.
    //And List<String> for VISIT ot VISIT_NUMBER
    protected availableTrellisAxisOptions: any;
    selectedOption: DisplayedGroupBySetting;
    availableOptions: List<DisplayedGroupBySetting> | DisplayedGroupBySetting[];

    constructor(protected axisLabelService: AxisLabelService,
                private datasetViews: DatasetViews,
                private trellisingObservables: TrellisingObservables,
                private settingsPipe: SettingsPipe) {
        super(axisLabelService);
        this.tooltip = 'Y-Axis settings';
        this.timepointType = this.datasetViews.getTimepointType();
        this.hasNotEmptyCycleAndDay = this.timepointType === TimepointConstants.CYCLE_DAY;

    }

    ngOnInit(): void {
        this.onInit();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.advancedMeasuredControlRequired) {
            if (this.option) {
                this.measureNumberOption = this.axisLabelService.getdMeasureNumberOptionByFullOption(this.option.get('groupByOption'));
                this.unitOption = this.axisLabelService.getUnitOptionByFullOption(this.option.get('groupByOption'));
                this.selectedOption = getSelectedOption(<any>this.option);
            }
            if (this.options) {
                this.measureNumberOptions = this.axisLabelService.getAvailableMeasureNumberOptions(this.options.get('options')
                    .map(option => option.get('groupByOption')).toArray());
                this.unitOptions = this.axisLabelService.getAvailableUnitOptions(this.options.get('options')
                    .map(option => option.get('groupByOption')).toArray());
            }
        } else if (this.advancedAxisControlRequired) {
            if (this.option) {
                this.selectedTrellisAxisOptions = getTrellisingAxisOption(this.option,
                    YAxisParameters.MEASUREMENT_TIMEPOINT);
                this.selectedOption = getSelectedOptionWithTrellising(this.option, this.selectedTrellisAxisOptions,
                    this.timepointType);
                if (this.selectedTrellisAxisOptions === NO_AVAILABLE_PARAMETERS) {
                    this.selectedTrellisAxisOptions = List();
                }
            }
            if (this.options) {
                this.availableOptions = generateAvailableOptionsWithTrellising(this.options.get('options'));
                //There is a single element in map which corresponds to timepointType, for instance,
                //{"CYCLE_DAY": {cycle1: List(day 1, day 2), cycle2: List(day 2, day 3)}}. So used first() to get available options
                const availableTrellisYAxisOptions = generateAvailableTimepointOptions(this.options.get('options'),
                    this.selectedOption.groupByOption);
                this.availableTrellisAxisOptions = availableTrellisYAxisOptions ? availableTrellisYAxisOptions.first() : List();
            }
        } else if (this.settingLabelRequired) {
            if (this.option) {
                this.settingString = this.plotSettings.get('trellisedBy');
                const displayedOption = this.settingsPipe.transform(this.option.get('groupByOption'), false, this.settingString);
                this.selectedOption = addDisplayedOption(this.option, displayedOption);
            }
            if (this.options) {
                this.availableOptions = this.options.get('options').map(option => {
                    const transformed = this.settingsPipe.transform(option.get('groupByOption'), false, this.settingString);
                    return addDisplayedOption(option, transformed);
                });
            }
        } else {
            if (this.option) {
                this.selectedOption = getSelectedOption(<any>this.option);
            }
            if (this.options) {
                this.availableOptions = generateAvailableOptions(this.options, this.tabId);
            }
        }
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }

    updateMeasureNumberSelection(event: any): void {
        this.measureNumberOption = event.target.value;
    }

    updateUnitSelection(event: any): void {
        this.unitOption = event.target.value;
    }

    updateValue(): void {
        if (!this.advancedAxisControlRequired) {
            if (this.selectedOption.params && this.selectedOption.params.TIMESTAMP_TYPE) {
                this.selectedOption.params.TIMESTAMP_TYPE = this.selectedOption.displayedOption;
            } else if (this.settingLabelRequired) {
                this.selectedOption = (this.availableOptions as List<DisplayedGroupBySetting>)
                    .find(option => option.get('displayedOption') === this.selectedOption.displayedOption);
            } else {
                this.selectedOption.groupByOption = this.selectedOption.displayedOption;
            }
            this.update.emit(fromJS(omit(this.selectedOption, 'displayedOption')));
            this.isOpen = false;
        } else {
            this.availableTrellisAxisOptions = generateAvailableTimepointOptions(this.options.get('options'),
                this.selectedOption.groupByOption).first();
            //after updating a parameter we need to set default timepoint
            if (this.hasNotEmptyCycleAndDay) {
                //'opt' inside map is list of days. We need to take first day of first cycle
                this.selectedTrellisAxisOptions = List(this.availableTrellisAxisOptions
                    .map(opt => opt.first()).entries().next().value);
            } else {
                //take first of available visits or visitNumbers
                this.selectedTrellisAxisOptions = this.availableTrellisAxisOptions.first();
            }
        }

    }

    closeControl(): void {
        if (this.option) {
            if (this.advancedMeasuredControlRequired) {
                this.measureNumberOption = this.axisLabelService.getdMeasureNumberOptionByFullOption(this.option.get('groupByOption'));
                this.unitOption = this.axisLabelService.getUnitOptionByFullOption(this.option.get('groupByOption'));
                this.selectedOption = getSelectedOption(<any>this.option);
            } else if (this.advancedAxisControlRequired) {
                if (this.selectedOption.groupByOption !== NO_AVAILABLE_PARAMETERS) {
                    this.selectedTrellisAxisOptions = getTrellisingAxisOption(this.option,
                        YAxisParameters.MEASUREMENT_TIMEPOINT);
                    this.selectedOption = getSelectedOptionWithTrellising(this.option, this.selectedTrellisAxisOptions,
                        this.timepointType);
                    if (this.options && this.availableTrellisAxisOptions.size > 0) {
                        this.availableTrellisAxisOptions = generateAvailableTimepointOptions(this.options.get('options'),
                            this.selectedOption.groupByOption).first();
                    }
                }
            } else if (this.settingLabelRequired) {
                const displayedOption = this.settingsPipe.transform(this.option.get('groupByOption'), false, this.settingString);
                this.selectedOption = addDisplayedOption(this.option, displayedOption);
            } else {
                this.selectedOption = getSelectedOption(<any>this.option);
            }
        }
        this.isOpen = false;
    }

    onApply(): void {
        let option;
        let params;
        if (this.advancedAxisControlRequired) {
            option = this.selectedOption.groupByOption;
            params = {
                trellisingParams: {
                    'MEASUREMENT': this.selectedOption.groupByOption,
                    'MEASUREMENT_TIMEPOINT': this.selectedTrellisAxisOptions
                }
            };
        } else {
            option = this.axisLabelService.constructAxisLabel(this.measureNumberOption, this.unitOption);
            params = null;
        }
        if (!this.advancedAxisControlRequired || this.selectedOption.groupByOption !== NO_AVAILABLE_PARAMETERS) {
            this.update.emit(
                fromJS({
                    groupByOption: option,  // value option from x-axis response
                    params
                })
            );
        }
        this.isOpen = false;
    }

    getAvailableOptions(isDay = false): any {
        if (isDay) {
            //get available days by certain cycle
            return this.availableTrellisAxisOptions.get(this.selectedTrellisAxisOptions.get(0));
        } else {
            //cycles = keys of availableTrellisAxisOptions
            return this.hasNotEmptyCycleAndDay ? Array.from(this.availableTrellisAxisOptions.keys())
                : this.availableTrellisAxisOptions;
        }
    }

    updateSelectedTrellisOptions(event: any, timepointType = ''): void {
        if (timepointType === 'DAY') {
            this.selectedTrellisAxisOptions = this.selectedTrellisAxisOptions.set(1, event);
        } else if (timepointType === 'CYCLE') {
            this.selectedTrellisAxisOptions = this.selectedTrellisAxisOptions.set(0, event);
            //if we select new cycle, default day should be set (from list of days that are consistent with this cycle)
            this.selectedTrellisAxisOptions = this.selectedTrellisAxisOptions.set(1, this.getAvailableOptions(true).first());
        } else {
            //just string for VISIT or VISIT_NUMBER
            this.selectedTrellisAxisOptions = event;
        }
    }
}
