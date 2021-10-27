import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.RENAL_LABS_BOXPLOT"></trellising-component>'
})
export class CreatinineBoxPlotComponent extends TChartComponent { }
