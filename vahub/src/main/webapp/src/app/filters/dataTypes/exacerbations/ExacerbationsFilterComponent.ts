import {Component, OnInit, OnDestroy, AfterViewInit, EventEmitter, Output} from '@angular/core';
import {ExacerbationsFiltersModel} from './ExacerbationsFiltersModel';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/module';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';

@Component({
    selector: 'exacerbationsfilter',
    templateUrl: 'ExacerbationsFilterComponent.html'
})
export class ExacerbationsFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(public filtersModel: ExacerbationsFiltersModel,
                protected filterEventService: FilterEventService,
                protected sessionEventService: SessionEventService) {
        super();
        this.subKey = 'ExacerbationsFilterComponent';
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
