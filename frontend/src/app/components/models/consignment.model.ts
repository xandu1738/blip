export interface Consignment {
    id?: number;
    consignmentNumber: string;
    scheduleId: number;
    vehicleId: number;
    origin: string;
    destination: string;
    totalWeight: number;
    totalCost: number;
    status: string;
    createdBy: number;
    createdAt?: string;
    updatedAt?: string;
    lastUpdatedBy?: number;
}
