export interface Device {
    id: string;
    name: string;
    description: string;
}

export interface Firmware {
    id: string;
    version: string;
    description: string;
    uploadedAt: Date;
}