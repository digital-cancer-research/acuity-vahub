import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class DoseColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study id', enableRowGroup: true},
        {field: 'studyPart', headerName: 'Study part', enableRowGroup: true},
        {field: 'subjectId', headerName: 'Subject id', enableRowGroup: true},
        {field: 'studyDrug', headerName: 'Study drug', enableRowGroup: true},
        {field: 'studyDrugCategory', headerName: 'Study drug category', enableRowGroup: true},
        {field: 'startDate', headerName: 'Start date', enableRowGroup: true},
        {field: 'endDate', headerName: 'End date', enableRowGroup: true},
        {field: 'dosePerAdmin', headerName: 'Dose per administration', enableValue: true},
        {field: 'doseUnit', headerName: 'Dose unit', enableRowGroup: true},
        {field: 'doseFreq', headerName: 'Dose frequency', enableRowGroup: true},
        {field: 'totalDailyDose', headerName: 'Total daily dose', enableValue: true},
        {field: 'plannedDose', headerName: 'Planned dose', enableValue: true},
        {field: 'plannedDoseUnits', headerName: 'Planned dose units', enableRowGroup: true},
        {field: 'plannedNoDaysTreatment', headerName: 'Planned No. of days treatment', enableValue: true},
        {field: 'formulation', headerName: 'Formulation', enableRowGroup: true},
        {field: 'route', headerName: 'Route', enableRowGroup: true},
        {field: 'actionTaken', headerName: 'Action taken', enableRowGroup: true},
        {field: 'mainReasonForActionTaken', headerName: 'Main reason for action taken', enableRowGroup: true},
        {field: 'mainReasonForActionTakenSpec', headerName: 'Main reason for action taken, Specification', enableRowGroup: true},
        {field: 'aeNumCausedActionTaken', headerName: 'AE number caused action taken', enableRowGroup: true},
        {field: 'aePtCausedActionTaken', headerName: 'AE PT caused action taken', enableRowGroup: true},
        {field: 'reasonForTherapy', headerName: 'Reason for therapy', enableRowGroup: true},
        {field: 'treatmentCycleDelayed', headerName: 'Treatment cycle delayed', enableRowGroup: true},
        {field: 'reasonTreatmentCycleDelayed', headerName: 'Reason treatment cycle delayed', enableRowGroup: true},
        {field: 'reasonTreatmentCycleDelayedOther', headerName: 'Reason treatment cycle delayed, Other', enableRowGroup: true},
        {field: 'aeNumCausedTreatmentCycleDelayed', headerName: 'AE number caused treatment cycle delayed', enableRowGroup: true},
        {field: 'aePtCausedTreatmentCycleDelayed', headerName: 'AE PT caused treatment cycle delayed', enableRowGroup: true},
        {field: 'medicationCode', headerName: 'Medication code', enableRowGroup: true},
        {field: 'medicationDictionaryText', headerName: 'Medication dictionary text', enableRowGroup: true},
        {field: 'atcCode', headerName: 'ATC code', enableRowGroup: true},
        {field: 'atcDictionaryText', headerName: 'ATC dictionary text', enableRowGroup: true},
        {field: 'medicationPt', headerName: 'Medication PT', enableRowGroup: true},
        {field: 'medicationGroupingName', headerName: 'Medication grouping name', enableRowGroup: true},
        {field: 'activeIngredients', headerName: 'Active ingredients', enableRowGroup: true}
    ]);
}
