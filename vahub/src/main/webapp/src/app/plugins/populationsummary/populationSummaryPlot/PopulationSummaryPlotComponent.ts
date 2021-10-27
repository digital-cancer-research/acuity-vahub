import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.POPULATION_BARCHART"></trellising-component>'
})
export class PopulationSummaryPlotComponent extends TChartComponent { }
