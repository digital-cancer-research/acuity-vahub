import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class AdverseEventsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'preferredTerm', enableRowGroup: true},
        {field: 'highLevelTerm', enableRowGroup: true},
        {field: 'systemOrganClass', enableRowGroup: true},
        {field: 'specialInterestGroup', enableRowGroup: true},
        {field: 'maxSeverity', enableRowGroup: true},
        {field: 'startDate', enableRowGroup: true},
        {field: 'endDate', enableRowGroup: true},
        {field: 'daysOnStudyAtAEStart', enableRowGroup: true},
        {field: 'daysOnStudyAtAEEnd', enableRowGroup: true},
        {field: 'duration', aggFunc: 'sum'},
        {field: 'daysFromPreviousDoseToAEStart', enableRowGroup: true},
        {field: 'serious', enableRowGroup: true},
        {field: 'actionTaken', enableRowGroup: true},
        {field: 'requiresOrProlongsHospitalisation', enableRowGroup: true},
        {field: 'treatmentEmergent', enableRowGroup: true},
        {field: 'causality', enableRowGroup: true},
        {field: 'description'},
        {field: 'comment'},
        {field: 'outcome'},
        {field: 'requiredTreatment', enableRowGroup: true},
        {field: 'causedSubjectWithdrawal', enableRowGroup: true},
        {field: 'doseLimitingToxicity', enableRowGroup: true},
        {field: 'timePointOfDoseLimitingToxicity'},
        {field: 'immuneMediatedAE', enableRowGroup: true},
        {field: 'infusionReactionAE', enableRowGroup: true},
        {field: 'aeOfSpecialInterest', enableRowGroup: true, headerName: 'Ae of special interest'},
    ]);
}
