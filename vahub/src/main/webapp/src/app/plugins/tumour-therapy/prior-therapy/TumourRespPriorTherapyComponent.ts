import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: `<trellising-component [tabId]="tabId.TUMOUR_RESPONSE_PRIOR_THERAPY"></trellising-component>`
})
export class TumourRespPriorTherapyComponent extends TChartComponent { }
