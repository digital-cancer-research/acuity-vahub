import {Injectable} from '@angular/core';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class LabsColumnModel extends AbstractColumnModel {
    _columnDefs: List<any> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementCategory', enableRowGroup: true},
        {field: 'measurementName', enableRowGroup: true},
        {field: 'measurementTimePoint', enableRowGroup: true},
        {field: 'daysOnStudy', enableValue: true},
        {field: 'analysisVisit', enableValue: true},
        {field: 'visitNumber', enableValue: true},
        {field: 'protocolScheduleTimepoint'},
        {field: 'resultValue', enableValue: true, cellStyle: this.colourIfOutOfRange},
        {field: 'resultUnit', enableRowGroup: true},
        {field: 'valueDipstick', headerName: 'Laboratory value dipstick'},
        {field: 'baselineValue', enableValue: true},
        {field: 'changeFromBaseline', enableValue: true},
        {field: 'percentChangeFromBaseline', enableValue: true},
        {field: 'baselineFlag', enableRowGroup: true},
        {field: 'refRangeNormValue', enableValue: true},
        {field: 'timesUpperRefValue', enableValue: true},
        {field: 'timesLowerRefValue', enableValue: true},
        {field: 'lowerRefRangeValue', enableValue: true},
        {field: 'upperRefRangeValue', enableValue: true},
        {
            headerName: 'Device',
            children: [
                {headerName: 'Source type', field: 'sourceType', columnGroupShow: 'open', enableValue: true},
                {headerName: 'Device name', field: 'deviceName', columnGroupShow: 'open', enableValue: true},
                {headerName: 'Device version', field: 'deviceVersion', columnGroupShow: 'open', enableValue: true},
                {headerName: 'Device type', field: 'deviceType', columnGroupShow: 'open', enableValue: true}
            ]
        }
    ]);

    private colourIfOutOfRange(params: any): any {
        if (params.data && (+params.value < +params.data.lowerRefRangeValue || +params.value > +params.data.upperRefRangeValue)) {
            return {backgroundColor: AbstractColumnModel.WARNING_COLOUR};
        }
        return {};
    }
}
