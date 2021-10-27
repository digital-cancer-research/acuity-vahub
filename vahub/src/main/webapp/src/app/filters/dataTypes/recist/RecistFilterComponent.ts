import { Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { RecistFiltersModel } from './RecistFiltersModel';
import { FilterEventService } from '../../event/FilterEventService';
import { SessionEventService } from '../../../session/module';
import { AbstractDataTypeFilter } from '../AbstractDataTypeFilter';

@Component({
    selector: 'recistfilter',
    templateUrl: 'RecistFilterComponent.html'
})
export class RecistFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: RecistFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'RecistFilterComponent';
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
