import {TestBed, inject} from '@angular/core/testing';
import {TitleService} from './TitleService';
import {TrellisDesign, TrellisCategory, XAxisOptions, YAxisOptions, TabId} from '../../../index';
import {Trellises, AxisLabelService} from './AxisLabelService';
import {SentenceCasePipe, LabelPipe} from '../../../../pipes/index';
import {YAxisParameters} from '../../../store';

describe('GIVEN TitleService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TitleService,
                {
                    provide: AxisLabelService,
                    useClass: AxisLabelService,
                    deps: [SentenceCasePipe, LabelPipe]
                },
                SentenceCasePipe,
                LabelPipe]
        });
    });

    describe('WHEN trellis design VARIABLE_Y_VARIABLE_X', () => {
        let trellisDesign: string;
        let xAxisOption: string;
        let yAxisOption: string;
        let trellis: Trellises[];
        beforeEach(() => {
            xAxisOption = 'Baseline';
            yAxisOption = 'PH-HYPO';
            trellisDesign = 'VARIABLE_Y_VARIABLE_X';
            trellis = [
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    'trellisOption': 'PH-HYPO'
                },
                {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'ARM', trellisOption: 'SuperDex 10 mg'}
            ];
        });
        it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
            expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption, TabId.LAB_SHIFTPLOT))
                .toEqual('Ph-hypo vs. baseline, SuperDex 10 mg');
        }));

    });

    describe('WHEN trellis design VARIABLE_Y_VARIABLE_X', () => {
        let trellisDesign: string;
        let xAxisOption: string;
        let yAxisOption: string;
        let trellis: Trellises[];
        beforeEach(() => {
            xAxisOption = 'Baseline';
            yAxisOption = 'PH-HYPO';
            trellisDesign = 'VARIABLE_Y_VARIABLE_X';
            trellis = [
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    'trellisOption': 'PH-HYPO'
                },
                {
                    category: TrellisCategory.NON_MANDATORY_TRELLIS,
                    trellisedBy: 'ARM',
                    trellisOption: 'SuperDex 10 mg'
                }
            ];
        });
        it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
            expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption, TabId.LAB_SHIFTPLOT))
                .toEqual('Ph-hypo vs. baseline, SuperDex 10 mg');
        }));

    });

    describe('WHEN trellis design CONTINUOUS_OVER_TIME', () => {
        let trellisDesign: string;
        let xAxisOption: string;
        let yAxisOption: string;
        beforeEach(() => {
            xAxisOption = XAxisOptions.STUDY_DEFINED_WEEK.toString();
            yAxisOption = YAxisOptions.PERCENTAGE_CHANGE_FROM_BASELINE.toString();
            trellisDesign = TrellisDesign.CONTINUOUS_OVER_TIME.toString();
        });
        describe('AND no non mandatory trellis', () => {
            let trellis: Trellises[];
            beforeEach(() => {
                trellis = [{
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOption: 'Albumin'
                }];
            });
            it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
                expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption))
                    .toEqual('Albumin (% change) vs. analysis visit');
            }));
        });

        describe('AND 1 non mandatory trellis', () => {
            let trellis: Trellises[];
            beforeEach(() => {
                trellis = [
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOption: 'Albumin'
                    },
                    {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'ARM', trellisOption: 'Placebo'}
                ];
            });
            it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
                expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption))
                    .toEqual('Albumin (% change) vs. analysis visit, Placebo');
            }));
        });

        describe('AND 2 non mandatory trellis', () => {
            let trellis: Trellises[];
            beforeEach(() => {
                trellis = [
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOption: 'Albumin'
                    },
                    {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'ARM', trellisOption: 'Placebo'},
                    {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'GENDER', trellisOption: 'Male'}
                ];
            });
            it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
                expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption))
                    .toEqual('Albumin (% change) vs. analysis visit, Placebo, Male');
            }));
        });

    });

    describe('WHEN trellis design CATEGORICAL_OVER_TIME', () => {
        let trellisDesign: string;
        let xAxisOption: string;
        let yAxisOption: string;
        beforeEach(() => {
            xAxisOption = XAxisOptions.STUDY_DEFINED_WEEK.toString();
            yAxisOption = YAxisOptions.PERCENTAGE_CHANGE_FROM_BASELINE.toString();
            trellisDesign = TrellisDesign.CATEGORICAL_OVER_TIME.toString();
        });
        describe('AND no non mandatory trellis', () => {
            let trellis: Trellises[];
            beforeEach(() => {
                trellis = [{
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: 'MEASUREMENT', trellisOption: 'Bilirubin, Total'
                }];
            });
            it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
                expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption))
                    .toEqual('Bilirubin, total (% change) vs. analysis visit');
            }));
        });

        describe('AND 1 non mandatory trellis', () => {
            let trellis: Trellises[];
            beforeEach(() => {
                trellis = [
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: 'MEASUREMENT',
                        trellisOption: 'Bilirubin, Total'
                    },
                    {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'ARM', trellisOption: 'Placebo'}
                ];
            });
            it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
                expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption))
                    .toEqual('Bilirubin, total (% change) vs. analysis visit, Placebo');
            }));
        });

        describe('AND 2 non mandatory trellis', () => {
            let trellis: Trellises[];
            beforeEach(() => {
                trellis = [
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: 'MEASUREMENT',
                        trellisOption: 'Bilirubin, Total'
                    },
                    {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'ARM', trellisOption: 'Placebo'},
                    {category: TrellisCategory.NON_MANDATORY_TRELLIS, trellisedBy: 'GENDER', trellisOption: 'Male'}
                ];
            });
            it('THEN returns title', inject([TitleService], (titleService: TitleService) => {
                expect(titleService.generateTitle(trellisDesign, trellis, xAxisOption, yAxisOption))
                    .toEqual('Bilirubin, total (% change) vs. analysis visit, Placebo, Male');
            }));
        });

    });

});
