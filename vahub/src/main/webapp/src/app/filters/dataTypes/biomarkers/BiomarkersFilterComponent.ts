import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';
import {BiomarkersFiltersModel} from './BiomarkersFiltersModel';

@Component({
    selector: 'biomarkers-filter',
    template: `<filter-collection [filtersModel]="filtersModel" (clearAll)="onClearAll()"
                                  (exportFilters)="onExportFilters($event)"></filter-collection>`
})
export class BiomarkersFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: BiomarkersFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'BiomarkersFilterComponent';
    }

    ngAfterViewInit(): void {
        this.afterViewInit();
    }

    ngOnInit(): void {
        this.onInit();
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }
}
