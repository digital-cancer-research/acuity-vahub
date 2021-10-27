import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: `<trellising-component [tabId]="tabId.CEREBROVASCULAR_EVENTS_OVER_TIME"></trellising-component>`
})
export class CVEOverTimeChartComponent extends TChartComponent {

}
