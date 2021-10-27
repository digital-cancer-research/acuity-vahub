import {Component, Input} from '@angular/core';
import {StudyService} from '../StudyService';

export interface SpotfireAnalysis {
    id: number;
    moduleType: string;
    name: string;
}

@Component({
    selector: 'spotfire-component',
    templateUrl: 'SpotfireComponent.html',
    styleUrls: ['SpotfireComponent.css']
})
export class SpotfireComponent {
    @Input() moduletype: string;

    constructor(private studyService: StudyService) {
    }

    extractSpotfireModules(): Array<SpotfireAnalysis> {
        const moduleTypeLower = this.moduletype.toLowerCase();
        if (!this.studyService.metadataInfo ||
            !this.studyService.metadataInfo[moduleTypeLower] ||
            !this.studyService.metadataInfo[moduleTypeLower].spotfireModules) {
            return [];
        }

        return <Array<SpotfireAnalysis>>this.studyService.metadataInfo[moduleTypeLower].spotfireModules;
    }
}
