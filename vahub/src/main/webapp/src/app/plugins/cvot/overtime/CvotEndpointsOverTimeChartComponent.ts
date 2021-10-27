import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.CVOT_ENDPOINTS_OVER_TIME"></trellising-component>'
})
export class CvotEndpointsOverTimeChartComponent extends TChartComponent {

}
