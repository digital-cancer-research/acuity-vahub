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
