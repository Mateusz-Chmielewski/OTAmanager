export interface Device {
    id: string;
    name: string;
    description: string;
    groupId: string;
}

export interface Firmware {
    id: string;
    version: string;
    description: string;
    uploadedAt: Date;
    isActive: boolean;
}

export interface DeviceGroup {
    id: string;
    name: string;
    description: string;
    userId: number;
}