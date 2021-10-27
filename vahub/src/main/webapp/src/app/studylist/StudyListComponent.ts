/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {List} from 'immutable';
import {StudyListService} from './StudyListService';
import Dataset = Request.Dataset;

@Component({
    selector: 'study-list',
    templateUrl: 'StudyListComponent.html',
    styleUrls: ['StudyListComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class StudyListComponent {
    @Input() studyList: any;
    @Input() searchString: string;
    @Input() acls: any;
    @Input() datasetIdAndNumSubjectsMap: Map<string, any>;
    @Input() selectedDatasets: List<Map<string, any>>;
    @Input() studyWarnings: List<Map<string, any>>;
    @Input() userActionText: string;
    @Input() isMultipleCollapseAvailable: boolean;

    @Output() handleExportDodModal: EventEmitter<Dataset> = new EventEmitter<Dataset>();
    @Output() handleSelectDataset: EventEmitter<Map<string, any>> = new EventEmitter<Map<string, any>>();

    private expandedDrugProgrammes: string[] = [];

    isViewCollapsed = true;

    constructor() {
    }

    isDatasetSelected(dataset: Map<string, any>): boolean {
        return this.selectedDatasets.indexOf(dataset) !== -1;
    }

    isDatasetDisabled(dataset: Map<string, any>): boolean {
        if (this.selectedDatasets.isEmpty()) {
            return false;
        }

        return StudyListService.isDatasetDisabled(dataset, this.selectedDatasets.first());
    }

    getTooltip(dataset: Map<string, any>): string {
        if (this.isDatasetDisabled(dataset)) {
            return 'Ongoing and analysis data sets cannot be viewed simultaneously in ACUITY';
        }

        return '';
    }

    onSelectDataset(selectedDataset: Map<string, any>): void {
        if (!this.isDatasetDisabled(selectedDataset)) {
            this.handleSelectDataset.emit(selectedDataset);
        }
    }

    openExportDodModal(dataset: Dataset): void {
        this.handleExportDodModal.emit(dataset);
    }

    expandDrugProgramme(drugProgramme: string, drugProgrammeList?: any[]): void {
        if (drugProgrammeList) {
            this.expandedDrugProgrammes = this.isViewCollapsed ? [] : drugProgrammeList;
        } else {
            const index = this.expandedDrugProgrammes.indexOf(drugProgramme);
            if (index === -1) {
                this.expandedDrugProgrammes.push(drugProgramme);
            } else {
                this.expandedDrugProgrammes.splice(index, 1);
            }

            this.isViewCollapsed = this.expandedDrugProgrammes.length !== this.studyList.size;
        }
    }

    toggleAll(): void {
        this.isViewCollapsed = !this.isViewCollapsed;
        this.expandDrugProgramme(null, this.studyList.keySeq().toArray());
    }

    drugProgrammeIsExpanded(drugProgramme: string): boolean {
        return this.expandedDrugProgrammes.indexOf(drugProgramme) !== -1;
    }
}
