import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {VitalsFiltersModel} from './VitalsFiltersModel';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';

@Component({
    selector: 'vitalsfilter',
    templateUrl: 'VitalsFilterComponent.html'
})
export class VitalsFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: VitalsFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'VitalsFilterComponent';
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
