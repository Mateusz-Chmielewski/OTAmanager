export interface Device {
    id: string;
    name: string;
    description: string;
}

export interface Firmware {
    id: string;
    version: number;
    uploadedAt: Date;
}