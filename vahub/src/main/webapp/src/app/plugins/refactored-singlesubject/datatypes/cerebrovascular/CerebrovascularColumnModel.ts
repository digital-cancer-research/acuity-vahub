import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class CerebrovascularColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study ID', enableRowGroup: true},
        {field: 'studyPart', headerName: 'Study part', enableRowGroup: true},
        {field: 'subjectId', headerName: 'Subject ID', enableRowGroup: true},
        {field: 'eventType', headerName: 'Type of Event', enableRowGroup: true},
        {field: 'aeNumber', headerName: 'Associated AE No.', enableRowGroup: true},
        {field: 'startDate', headerName: 'Event Start Date', enableRowGroup: true},
        {field: 'term', headerName: 'Event Term', enableRowGroup: true},
        {field: 'primaryIschemicStroke', headerName: 'If Primary Ischemic Stroke', enableRowGroup: true},
        {field: 'traumatic', headerName: 'If Traumatic', enableRowGroup: true},
        {field: 'intraHemorrhageLoc', headerName: 'Loc. of Primary Intracranial Hemorrhage', enableRowGroup: true},
        {field: 'intraHemorrhageOtherLoc', headerName: 'Primary Intra. Hemorrhage Other, Specify', enableRowGroup: true},
        {field: 'symptomsDuration', headerName: 'Duration of Symptoms', enableRowGroup: true},
        {field: 'mrsPriorToStroke', headerName: 'MRS Prior to Stroke', enableRowGroup: true},
        {field: 'mrsDuringStrokeHosp', headerName: 'MRS During Stroke Hospitalisation', enableRowGroup: true},
        {field: 'mrsCurrVisitOr90dAfter', headerName: 'MRS at Current Visit or 90D After Stroke', enableRowGroup: true},
        {field: 'comment', headerName: 'Comment', enableRowGroup: true}
    ]);
}
