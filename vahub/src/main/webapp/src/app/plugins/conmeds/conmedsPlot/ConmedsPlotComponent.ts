import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.CONMEDS_BARCHART"></trellising-component>'
})
export class ConmedsPlotComponent extends TChartComponent { }
