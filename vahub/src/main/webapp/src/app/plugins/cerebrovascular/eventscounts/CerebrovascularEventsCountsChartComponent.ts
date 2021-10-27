import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.CEREBROVASCULAR_COUNTS"></trellising-component>'
})
export class CerebrovascularEventsCountsChartComponent extends TChartComponent {

}
