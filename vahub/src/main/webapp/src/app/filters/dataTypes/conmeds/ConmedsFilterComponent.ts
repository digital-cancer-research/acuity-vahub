import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {ConmedsFiltersModel} from './ConmedsFiltersModel';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/module';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';

@Component({
    selector: 'conmedsfilter',
    templateUrl: 'ConmedsFilterComponent.html'
})
export class ConmedsFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: ConmedsFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'ConmedsFilterComponent';
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
