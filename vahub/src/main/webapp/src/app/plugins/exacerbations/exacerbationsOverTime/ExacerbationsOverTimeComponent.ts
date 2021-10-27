import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.EXACERBATIONS_OVER_TIME"></trellising-component>'
})
export class ExacerbationsOverTimeComponent extends TChartComponent { }
