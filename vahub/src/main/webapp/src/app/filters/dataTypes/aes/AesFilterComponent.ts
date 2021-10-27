import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter, Input} from '@angular/core';
import {AesFiltersModel} from './AesFiltersModel';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/module';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';

@Component({
    selector: 'aesfilter',
    templateUrl: 'AesFilterComponent.html'
})
export class AesFilterComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Input() isCohortFilter: boolean;
    @Output() clearAll = new EventEmitter<void>();
    @Output() exportFilters = new EventEmitter<MouseEvent>();

    constructor(public filtersModel: AesFiltersModel,
                protected filterEventService: FilterEventService,
                protected sessionEventService: SessionEventService) {
        super();
        this.subKey = 'AesFilterComponent';
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
