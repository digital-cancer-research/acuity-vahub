import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.LUNG_FUNCTION_BOXPLOT"></trellising-component>'
})
export class LungFunctionBoxPlotComponent extends TChartComponent { }
