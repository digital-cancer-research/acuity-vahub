import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class AlcoholColumnModel extends AbstractColumnModel {
  _columnDefs: List<ColDef> = List([
    {field: 'studyId', headerName: 'Study id'},
    {field: 'studyPart', headerName: 'Study part'},
    {field: 'subjectId', headerName: 'Subject id'},
    {field: 'substanceCategory', enableRowGroup: true, headerName: 'Substance category'},
    {field: 'substanceUseOccurrence', enableRowGroup: true, headerName: 'Substance use occurrence'},
    {field: 'substanceType', enableRowGroup: true, headerName: 'Type of substance'},
    {field: 'otherSubstanceTypeSpec', enableRowGroup: true, headerName: 'Other substance type specification'},
    {field: 'substanceTypeUseOccurrence', enableRowGroup: true, headerName: 'Substance type use occurrence'},
    {field: 'substanceConsumption', enableValue: true, headerName: 'Substance consumption'},
    {field: 'frequency', enableRowGroup: true, headerName: 'Frequency'},
    {field: 'startDate', enableRowGroup: true, headerName: 'Start date'},
    {field: 'endDate', enableRowGroup: true, headerName: 'End date'},
  ]);
}
