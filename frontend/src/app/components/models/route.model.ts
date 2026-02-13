export interface Route {

    id?: number;
    partnerCode: string;
    origin: string;
    destination: string;
    estimatedDistance: number;
    estimatedDuration: number;
    status: string;
    createdAt?: string;
    createdBy?: number;
}
