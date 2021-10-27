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

import {Input} from '@angular/core';
import {List} from 'immutable';

import {ITrellises, TrellisCategory} from '../store';
import {ALL_OPTION, ColorByOption, NONE} from './ColorByConstants';
import {ColorByService} from './ColorByService';

/**
 * This is a abstract class which contains base behavior for legend-control and table-axis.
 * These components are responsible for managing trellising option, but use different templates
 */
export class AbstractColorBy {
    isOpen = false;
    option: ColorByOption;
    options: ColorByOption[];
    drugOption: string;

    @Input() trellising: List<ITrellises>;
    @Input() baseTrellising: List<ITrellises>;
    @Input() selectedDrug: string;
    @Input() isAllColoringOptionAvailable: boolean;

    constructor(public colorByService: ColorByService) {
    }

    get allOption(): ColorByOption {
        return {...ALL_OPTION};
    }

    /**
     * This method toggles control from close to open and vice versa
     */
    toggle(): void {
        this.isOpen = !this.isOpen;
    }

    /**
     * This method updates current trellising option
     *
     * If option doesn't support drugs, control close and emit event
     * If option supports drugs, appear additional select
     */
    onDisplayOptionChange(): void {
        this.updateDisplayOption();

        if (!this.option.drugs) {
            this.applyChanges();
        }
    }

    applyChanges(isShapeByLegend?: boolean): void {
        if (isShapeByLegend) {
            this.colorByService.updateDrugForShapeBy(this.drugOption);
        } else {
            this.colorByService.updateDrugForColorBy(this.drugOption);
        }
        this.colorByService.updateTrellisingOptions(this.getCurrentTrellising());
        this.toggle();
    }

    protected updateDisplayOption(): void {
        const chosenOption = this.option.displayOption === ALL_OPTION.displayOption
            ? ALL_OPTION
            : this.options.find(o => o.displayOption === this.option.displayOption);

        this.option = {...chosenOption};
        this.drugOption = this.option.drugs ? this.option.drugs[0] : null;
    }

    private getCurrentTrellising(): List<ITrellises> {
        const newTrellising = <List<ITrellises>>this.trellising.filter((x: ITrellises) => {
            return x.get('category') !== TrellisCategory.NON_MANDATORY_SERIES;
        });

        const filteredBaseTrellising = <List<ITrellises>>this.baseTrellising.filter((trellising: ITrellises) => {
            return trellising.get('trellisedBy') !== NONE
                && trellising.get('trellisedBy') === this.option.displayOption;
        });

        return <List<ITrellises>>newTrellising.concat(filteredBaseTrellising);
    }
}
