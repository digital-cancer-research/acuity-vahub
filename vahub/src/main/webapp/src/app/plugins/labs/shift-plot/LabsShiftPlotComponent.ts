import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.LAB_SHIFTPLOT"></trellising-component>'
})
export class LabsShiftPlotComponent extends TChartComponent { }
