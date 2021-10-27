import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.BIOMARKERS_HEATMAP_PLOT"></trellising-component>'
})
export class BiomarkersPlotComponent extends TChartComponent {

}
