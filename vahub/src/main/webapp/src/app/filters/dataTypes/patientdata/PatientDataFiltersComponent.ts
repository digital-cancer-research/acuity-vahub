import {Component, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {FilterEventService} from '../../event/FilterEventService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {AbstractDataTypeFilter} from '../AbstractDataTypeFilter';
import {PatientDataFiltersModel} from './PatientDataFiltersModel';

@Component({
    selector: 'patientdatafilter',
    templateUrl: 'PatientDataFiltersComponent.html'
})
export class PatientDataFiltersComponent extends AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {
    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    constructor(
        public filtersModel: PatientDataFiltersModel,
        protected filterEventService: FilterEventService,
        protected sessionEventService: SessionEventService
    ) {
        super();
        this.subKey = 'PatientDataFiltersComponent';
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
