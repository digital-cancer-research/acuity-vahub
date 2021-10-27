import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.LAB_BOXPLOT"></trellising-component>'
})
export class LabsPopulationBoxPlotComponent extends TChartComponent { }
