import {PopulationFiltersModel, AesFiltersModel} from '../../../filters/dataTypes/module';
import UserVO = Request.UserVO;

/**
 * DTO class to make calls to saving cohorts cleaner
 */
export class SaveCohortDto {
    cohortId: number;
    cohortName: string;
    populationFilterId: number;
    aeFilterId: number;
    localPopulationFiltersModel: PopulationFiltersModel;
    localAeFiltersModel: AesFiltersModel;
    shareWith: UserVO[];

    constructor(cohortId: number, cohortName: string, shareWith: UserVO[], populationFilterId: number, aeFilterId: number,
        localPopulationFiltersModel: PopulationFiltersModel, localAeFiltersModel: AesFiltersModel) {

        this.cohortId = cohortId;
        this.cohortName = cohortName;
        this.shareWith = shareWith;
        this.populationFilterId = populationFilterId;
        this.aeFilterId = aeFilterId;
        this.localPopulationFiltersModel = localPopulationFiltersModel;
        this.localAeFiltersModel = localAeFiltersModel;
    }
}
