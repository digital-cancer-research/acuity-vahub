import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.EXACERBATIONS_GROUPED_COUNTS"></trellising-component>'
})
export class ExacerbationsCountComponent extends TChartComponent { }
