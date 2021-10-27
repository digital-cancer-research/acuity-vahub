import {Component, OnInit, OnDestroy} from '@angular/core';
import {ApplySafetyFiltersModalService} from './ApplySafetyFiltersModalService';
import {DatasetViews} from '../../../security/DatasetViews';
import {PopulationFiltersModel} from '../../../filters/dataTypes/population/PopulationFiltersModel';

/**
 * Modal window that offers to apply safety population filter
 *
 * @property {boolean} modalIsVisible
 */
@Component({
    selector: 'apply-safety-filters-modal',
    templateUrl: 'ApplySafetyFiltersModalComponent.html'
})
export class ApplySafetyFiltersModalComponent implements OnInit, OnDestroy {

    modalIsVisible = false;

    constructor(private applySafetyFiltersModalService: ApplySafetyFiltersModalService,
                private populationFiltersModel: PopulationFiltersModel,
                protected datasetViews: DatasetViews) {
    }

    /**
     * Sets modal window visible
     */
    ngOnInit(): void {
        if (this.datasetViews.hasSafetyAsNoInPopulation() && this.applySafetyFiltersModalService.isReShowMessage()) {
            this.modalIsVisible = true;
        }
    }

    ngOnDestroy(): void {
    }

    /**
     * Hides modal window on cancel
     */
    cancelModal(): void {
        this.modalIsVisible = false;
        this.applySafetyFiltersModalService.setReShowMessage(false);
    }

    /**
     * Applies safety filter on OK
     */
    okModal(): void {
        this.modalIsVisible = false;
        this.applySafetyFiltersModalService.setReShowMessage(false);
        this.populationFiltersModel.setSafetyPopulationAsY();
        this.populationFiltersModel.getFilters(true, true);
    }
}
