import {Injectable} from '@angular/core';
import {StudyWarningNames} from '../studyselection/models';
import {Map} from 'immutable';
import {StudyService} from '../common/StudyService';
import Dataset = Request.Dataset;

@Injectable()
export class StudyListService {
    /**
     * Check whether both datasets have the same type and
     * are part of the same drug programme
     * @param current - Map<string, any>
     * @param candidate - Map<string, any>
     * @returns {boolean}
     */
    static isDatasetDisabled(current, candidate): boolean {
        return StudyService.isOngoingStudy(candidate.get('type')) !== StudyService.isOngoingStudy(current.get('type')) &&
            candidate.get('drugProgramme') === current.get('drugProgramme');
    }

    static getWarnings(warnings: Map<string, any>): string {
        const warningAcc = [];

        if (warnings.get('blinded')) {
            warningAcc.push(StudyWarningNames.BLINDED);
        }
        if (warnings.get('randomised')) {
            warningAcc.push(StudyWarningNames.RANDOMISED);
        }
        if (warnings.get('forRegulatoryPurposes')) {
            warningAcc.push(StudyWarningNames.REGULATORY);
        }

        return warningAcc.join(', ');
    }

    /**
     * Get number of subjects for dataset by ID
     * @param datasetId {number}
     * @param datasetIdMap {Map<string, Dataset>}
     * @returns {number}
     */
    static getNumberOfDosedSubjects(datasetId, datasetIdMap): number {
        if (datasetId) {
            return datasetIdMap.getIn([datasetId, 'numberOfDosedSubjects']) || 0;
        }

        return 0;
    }

    private getStudySelectionMap(combinedStudyInfo: any): any[] {
        return [];
    }
}
