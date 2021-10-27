import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import * as  _ from 'lodash';
import {DatasetViews} from '../../../security/DatasetViews';
import SavedFilterVO = Request.SavedFilterVO;
import SavedFilterInstance = Request.SavedFilterInstance;

@Component({
    selector: 'cohort-filter-list',
    templateUrl: 'FilterListComponent.html',
    styleUrls: ['CohortEditorComponent.css']
})
export class FilterListComponent implements OnChanges {

    @Input()
    availableCohorts: SavedFilterVO[];

    @Input()
    selectedCohortId: number;

    @Output()
    updateFilters = new EventEmitter<void>();

    @Output()
    hideEventFilters = new EventEmitter<void>();

    @Output()
    hidePopulationFilters = new EventEmitter<void>();

    @Output()
    displayEventFilters = new EventEmitter<void>();

    @Output()
    displayPopulationFilters = new EventEmitter<void>();

    selectedFilterInstance: string;
    addFilterOpen: boolean;

    readonly ID_FOR_NEW_FILTERS = -1;

    constructor(private datasetViews: DatasetViews) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['selectedCohortId']) {
            this.selectedFilterInstance = undefined;
        }
    }

    getFiltersForSelectedCohort(): string[] {
        const filters = <string[]> _.chain(this.availableCohorts)
            .filter(cohort => cohort.savedFilter.id === this.selectedCohortId)
            .flatMap('cohortFilters')
            .map('filterView')
            .filter(filterView => !_.isUndefined(filterView))
            .value();

        if (_.isUndefined(this.selectedFilterInstance)) {
            this.selectedFilterInstance = filters[filters.length - 1];
        }

        return filters;
    }

    formatFilterInstanceName(name: string): string {
        if (name === 'AES') {
            return 'AEs';
        }
        return _.capitalize(_.kebabCase(name));
    }

    selectFilterInstance(filterInstance: string): void {
        this.selectedFilterInstance = filterInstance;
        this.updateFilters.emit();
    }

    hasFilterInstance(filterInstance: string): boolean {
        return this.getFiltersForSelectedCohort().indexOf(filterInstance) > -1;
    }

    addPopulationFilter(): void {
        if (!this.hasFilterInstance('POPULATION')) {
            this.hideEventFilters.emit();
            this.displayPopulationFilters.emit();
            this.addFilterOpen = false;
            this.addNewFilter('POPULATION');
        }
    }

    addAeFilter(): void {
        if (!this.hasFilterInstance('AES') && this.datasetViews.hasAesData()) {
            this.hidePopulationFilters.emit();
            this.displayEventFilters.emit();
            this.addFilterOpen = false;
            this.addNewFilter('AES');
        }
    }

    private addNewFilter(filterView): void {
        this.selectedFilterInstance = filterView;
        const newFilter: SavedFilterInstance = {
            id: this.ID_FOR_NEW_FILTERS,
            filterView: filterView,
            json: ''
        };

        const selectedCohort = _.find(this.availableCohorts, (cohort: SavedFilterVO) => cohort.savedFilter.id === this.selectedCohortId);
        if (_.isEmpty(selectedCohort.cohortFilters)) {
            selectedCohort.cohortFilters = [newFilter];
        } else {
            selectedCohort.cohortFilters.push(newFilter);
        }
    }
}
