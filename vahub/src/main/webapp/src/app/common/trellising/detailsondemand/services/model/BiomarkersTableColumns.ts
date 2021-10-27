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

import {List} from 'immutable';

export function getColumnGroupDefs(columnHeaders: any): any {
    return List([
        {
            headerName: 'Studies, parts and subjects',
            children: [
                {headerName: columnHeaders.studyId, field: 'studyId', columnGroupShow: 'open'},
                {headerName: columnHeaders.studyPart, field: 'studyPart', columnGroupShow: 'open'},
                {headerName: columnHeaders.subjectId, field: 'subjectId', columnGroupShow: 'open'},
            ]
        },
        {
            headerName: 'Sample status',
            children: [
                {headerName: columnHeaders.sampleType, field: 'sampleType', columnGroupShow: 'open'},
                {headerName: columnHeaders.sampleId, field: 'sampleId', columnGroupShow: 'open'},
                {headerName: columnHeaders.variantCount, field: 'variantCount', columnGroupShow: 'open'}
            ]
        },
        {
            headerName: 'Gene',
            children: [
                {headerName: columnHeaders.gene, field: 'gene', columnGroupShow: 'open'},
                {headerName: columnHeaders.cdnaChange, field: 'cdnaChange', columnGroupShow: 'open'},
                {headerName: columnHeaders.somaticStatus, field: 'somaticStatus', columnGroupShow: 'open'},
                {headerName: columnHeaders.aminoAcidChange, field: 'aminoAcidChange', columnGroupShow: 'open'},
                {headerName: columnHeaders.externalVariantId, field: 'externalVariantId', columnGroupShow: 'open'}
            ]
        },
        {
            headerName: 'Location',
            children: [
                {headerName: columnHeaders.chromosome, field: 'chromosome', columnGroupShow: 'open'},
                {headerName: columnHeaders.chromosomeLocationStart, field: 'chromosomeLocationStart', columnGroupShow: 'open'},
                {headerName: columnHeaders.chromosomeLocationEnd, field: 'chromosomeLocationEnd', columnGroupShow: 'open'}
            ]
        },
        {
            headerName: 'Alteration',
            children: [
                {headerName: columnHeaders.variantType, field: 'variantType', columnGroupShow: 'open'},
                {headerName: columnHeaders.mutationType, field: 'mutationType', columnGroupShow: 'open'},
                {headerName: columnHeaders.copyNumber, field: 'copyNumber', columnGroupShow: 'open'},
                {headerName: columnHeaders.rearrangementGene1, field: 'rearrangementGene1', columnGroupShow: 'open'},
                {headerName: columnHeaders.rearrangementDescription, field: 'rearrangementDescription', columnGroupShow: 'open'}
            ]
        },
        {
            headerName: 'Quantification',
            children: [
                {headerName: columnHeaders.totalReads, field: 'totalReads', columnGroupShow: 'open'},
                {headerName: columnHeaders.germlineFrequency, field: 'germlineFrequency', columnGroupShow: 'open'},
                {headerName: columnHeaders.mutantAlleleFrequency, field: 'mutantAlleleFrequency', columnGroupShow: 'open'},
                {headerName: columnHeaders.copyNumberAlterationCopyNumber, field: 'copyNumberAlterationCopyNumber', columnGroupShow: 'open'},
                {headerName: columnHeaders.chromosomeInstabilityNumber, field: 'chromosomeInstabilityNumber', columnGroupShow: 'open'},
                {headerName: columnHeaders.tumourMutationalBurden, field: 'tumourMutationalBurden', columnGroupShow: 'open'}
            ]
        }
    ]);
}
