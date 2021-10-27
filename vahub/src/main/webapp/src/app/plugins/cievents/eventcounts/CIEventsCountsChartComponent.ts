import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.CI_EVENT_COUNTS"></trellising-component>'
})
export class CIEventsBarChartComponent extends TChartComponent {

}
