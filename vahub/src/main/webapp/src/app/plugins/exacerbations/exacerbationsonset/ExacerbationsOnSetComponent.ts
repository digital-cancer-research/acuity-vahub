import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.EXACERBATIONS_COUNTS"></trellising-component>'
})
export class ExacerbationsOnSetComponent extends TChartComponent { }
