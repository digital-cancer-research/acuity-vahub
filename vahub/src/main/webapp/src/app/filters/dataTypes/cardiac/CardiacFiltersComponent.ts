import {Component, OnInit, OnDestroy, AfterViewInit, EventEmitter, Output} from '@angular/core';
import {CardiacFiltersModel} from './CardiacFiltersModel';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/module';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';

@Component({
    selector: 'cardiacfilter',
    templateUrl: 'CardiacFiltersComponent.html'
})
export class CardiacFiltersComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: CardiacFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'CardiacFiltersComponent';
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
