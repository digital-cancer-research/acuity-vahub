import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.VITALS_BOXPLOT"></trellising-component>'
})
export class VitalsPopulationBoxPlotComponent extends TChartComponent { }
