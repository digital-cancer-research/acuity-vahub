export const ALL = 'All';
export const NONE = 'NONE';

export interface ColorByOption {
    displayOption: string;
    drugs: string[];
}

export const ALL_OPTION = <ColorByOption>{
    displayOption: ALL,
    drugs: null
};

export const NONE_OPTION = <ColorByOption>{
    displayOption: NONE,
    drugs: null
};
