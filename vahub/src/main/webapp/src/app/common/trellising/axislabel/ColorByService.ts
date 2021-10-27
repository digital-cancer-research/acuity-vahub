import {Injectable} from '@angular/core';
import {List} from 'immutable';
import {uniq} from 'lodash';

import {ALL_OPTION, ColorByOption, NONE_OPTION} from './ColorByConstants';
import {Trellising} from '../store/Trellising';
import {ITrellises, TrellisCategory} from '../store';

/**
 * Base service for legend-control and table-axis.
 * This is bridge between components and middleware
 */
@Injectable()
export class ColorByService {

    constructor(private trellisingMiddleware: Trellising) {
    }

    getSelectedOption(trellising: List<ITrellises>, isAllColoringOptionAvailable: boolean): ColorByOption {
        const option = this.filterByNonMandatorySeries(trellising)
            .map(this.mapOptions)
            .first();

        return option ? option : isAllColoringOptionAvailable
            ? {...ALL_OPTION}
            : {...NONE_OPTION};
    }

    getAllAvailableOptions(baseTrellising: List<ITrellises>): ColorByOption[] {
        return this.filterByNonMandatorySeries(baseTrellising)
            .map(this.mapOptions)
            .toArray();
    }

    getDrugOption(selectedDrug: string, selectedOption: ColorByOption): string {
        const defaultDrug = selectedOption.drugs ? selectedOption.drugs[0] : null;

        return selectedDrug ? selectedDrug : defaultDrug;
    }

    canComponentBeEnabled(baseTrellising: List<ITrellises>): boolean {
        return this.filterByNonMandatorySeries(baseTrellising).size > 0;
    }

    updateDrugForColorBy(drugOption: string): void {
    }

    updateDrugForShapeBy(drugOption: string): void {
    }

    updateTrellisingOptions(trellising: List<ITrellises>): void {
        this.trellisingMiddleware.updateTrellisingOptions(trellising);
    }

    private filterByNonMandatorySeries(trellising: List<ITrellises>): any {
        return trellising && trellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        });
    }

    /**
     * If option has drug support it will be in the following format: trellisingOption--drugName
     * For all options we create model with trellising option and drugs
     *
     * @param {ITrellises} trellising
     * @returns {ColorByOption}
     */
    private mapOptions(trellising: ITrellises): ColorByOption {
        const trellisedBy = trellising.get('trellisedBy');
        const trellisOptions = trellising.get('trellisOptions').toJS();
        const hasDrugOptions = /--/.test(trellisOptions[0]);
        const sortedOptions = hasDrugOptions ? uniq(trellisOptions.map(o => o.split('--')[0])).sort() : null;

        return <ColorByOption>{
            displayOption: trellisedBy,
            drugs: sortedOptions
        };
    }
}
