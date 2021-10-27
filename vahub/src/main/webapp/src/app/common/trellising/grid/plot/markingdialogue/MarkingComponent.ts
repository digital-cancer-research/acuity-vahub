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

import {
    Component, Input, Output, OnChanges, SimpleChanges, ChangeDetectionStrategy, EventEmitter,
} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {List, Map} from 'immutable';

import {MarkingDialogue} from './MarkingDialogue';
import {ISelectionDetail, IMultiSelectionDetail} from '../../../index';
import {PopulationFiltersModel, AesFiltersModel} from '../../../../../filters/module';
import {AesHttpService} from '../../../../../data/aes/AesHttpService';
import {FilterEventService} from '../../../../../filters/module';
import {SessionEventService} from '../../../../../session/event/SessionEventService';
import {IChartSelection} from '../../../store/ITrellising';

@Component({
    selector: 'marking-dialogue',
    templateUrl: 'MarkingComponent.html',
    styleUrls: ['./MarkingComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MarkingComponent implements OnChanges {
    @Input() dialogueBox: MarkingDialogue;
    @Input() selectionDetail: ISelectionDetail;
    @Input() multiSelectionDetail: IMultiSelectionDetail;
    @Input() selection: List<IChartSelection>;
    @Input() jumpToAesFromAeNumberLocation: string;
    @Output() clearMarkings: EventEmitter<any> = new EventEmitter<any>();
    @Output() setTimelineState: EventEmitter<any> = new EventEmitter<any>();
    public box: MarkingDialogue = {x: 0, y: 0, show: false};

    constructor(public populationFiltersModel: PopulationFiltersModel,
                public aesFiltersModel: AesFiltersModel,
                private aesHttpService: AesHttpService,
                private sessionEventService: SessionEventService,
                protected filterEventService: FilterEventService,
                private router: Router,
                private location: Location) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['dialogueBox']) {
            this.box = this.dialogueBox;
        }
        if (changes['selection'] && (!this.selection || this.selection.size === 0)) {
            this.box = {x: 0, y: 0, show: false};
        }
        //If nothing in selection clear selection
        if (changes['selectionDetail'] && this.selectionDetail && this.selectionDetail.subjectIds.length === 0) {
            //Bug in angular lifecycle requires timeout
            setTimeout(() => {
                this.box = {x: 0, y: 0, show: false};
                this.clearAllMarkingsAction();
            });
        }
    }

    close(): void {
        this.box.show = false;
    }

    setAsPopulation(): void {
        if (!this.selectionDetail && !this.multiSelectionDetail) {
            return;
        }
        const subjectIds = this.selectionDetail ? this.selectionDetail.subjectIds : this.multiSelectionDetail.subjectIds;
        this.populationFiltersModel.setAsPopulation(subjectIds);
        this.box.show = false;
        this.clearMarkings.next(null);
    }

    setAsTimeline(): void {
        //Change filters
        this.setAsPopulation();
        //Change initial timeline state
        this.setTimelineState.next(null);
        //Navigate to timeline
        this.router.navigate(['/plugins/timeline/']);
    }

    setAesNumberFilterAndGotoAes(): void {
        if (!this.selectionDetail) {
            return;
        }

        this.aesFiltersModel.itemsModels.forEach((itemModel) => {
            if (itemModel.key === AesFiltersModel.AES_NUMBER_KEY) {
                this.aesHttpService.getAssociatedAesNumbersFromEventIds(
                    this.sessionEventService.currentSelectedDatasets,
                    this.selectionDetail.eventIds,
                    this.jumpToAesFromAeNumberLocation
                )
                    .map(res => res).subscribe(res => {
                    console.log('Got ' + +' from eventIds for ' + this.jumpToAesFromAeNumberLocation);
                    const listItemModel: any = itemModel;

                    listItemModel.appliedSelectedValues = res;
                    listItemModel.selectedValues = res;
                    listItemModel.numberOfSelectedFilters = res.length;

                    console.log(this.aesFiltersModel.transformFiltersToServer());
                    this.aesFiltersModel.getFilters(true);
                    this.clearAllMarkingsAction();

                    //Navigate to timeline
                    this.router.navigate(['/plugins/aes/subject-counts/']);
                });
            }
        });
    }

    clearAllMarkingsAction(): void {
        this.box.show = false;
        this.clearMarkings.next(null);
    }

    percentageOfSubjects(): string {
        if (!this.selectionDetail) {
            return;
        }
        return (100.0 * this.selectionDetail.subjectIds.length / this.selectionDetail.totalSubjects).toFixed(2);

    }

    percentageOfSubjectsMulti(): string {
        if (!this.multiSelectionDetail) {
            return;
        }
        return (100.0 * this.multiSelectionDetail.subjectIds.length / this.multiSelectionDetail.totalSubjects).toFixed(2);

    }

    isSingleSubjectView(): boolean {
        return this.location.path().indexOf('singlesubject') !== -1;
    }

    shouldBeDisabled(): boolean {
        if (this.multiSelectionDetail) {
            return !this.multiSelectionDetail.subjectIds || this.multiSelectionDetail.subjectIds.length >= 1000;
        }
        if (this.selectionDetail) {
            return !this.selectionDetail.subjectIds || this.selectionDetail.subjectIds.length >= 1000;
        }
        return true;
    }

}
