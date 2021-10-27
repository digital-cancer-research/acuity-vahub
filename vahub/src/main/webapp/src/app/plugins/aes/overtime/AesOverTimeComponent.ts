import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.AES_OVER_TIME"></trellising-component>'
})
export class AesOverTimeComponent extends TChartComponent { }
