import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.RENAL_CKD_BARCHART"></trellising-component>'
})
export class CkdStageBarChartComponent extends TChartComponent { }
