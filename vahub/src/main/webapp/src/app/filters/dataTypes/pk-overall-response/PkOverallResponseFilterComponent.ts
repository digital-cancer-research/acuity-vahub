import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {SessionEventService} from '../../../session/module';
import {FilterEventService} from '../../event/FilterEventService';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';
import {PkOverallResponseFiltersModel} from './PkOverallResponseFiltersModel';

@Component({
    selector: 'pk-overall-response-filter',
    template: `
        <filter-collection [filtersModel]="filtersModel" (clearAll)="onClearAll()"
                           (exportFilters)="onExportFilters($event)"></filter-collection>`
})
export class PkOverallResponseFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: PkOverallResponseFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'PkOverallResponseFilterComponent';
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
