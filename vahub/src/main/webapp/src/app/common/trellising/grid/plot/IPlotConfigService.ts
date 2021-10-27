export interface IPlotConfigService {
    setCurrentDatasets(newStudy: any): void;
    createPlotConfig(...args): any;
}
