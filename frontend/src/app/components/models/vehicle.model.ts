export interface Vehicle {
    id?: number;
    registration_number: string;
    capacity: number;
    type: VehicleType;
    status?: string;
    partner_code?: string;
    created_at?: string;
    created_by?: number;
    vehicle_category?: string;
}

export enum VehicleType {
    BUS = 'BUS',
    DRONE = 'DRONE',
    MINI_VAN = 'MINI_VAN',
    MINI_BUS = 'MINI_BUS',
    TRUCK = 'TRUCK'
}
