export interface Parcel {
    id?: number;
    senderId: number;
    receiverName: string;
    receiverPhone: string;
    pickupLocation: string;
    dropOffLocation: string;
    weight: number;
    dimensions?: string;
    type: string;
    cost: number;
    status: string;
    createdAt?: string;
    createdBy?: number;
}
