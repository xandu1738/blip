export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  roleCode: string;
  domain: string;
  createdAt: string;
  lastLoggedInAt: string;
  isActive: boolean;
  username: string;
}

export interface LoginRequest {
  data: {
    email: string;
    password: string;
  };
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
  permissions: string[];
}

export interface CreateUserRequest {
  data: {
    role: string;
    first_name: string;
    last_name: string;
    email: string;
    password: string;
    partner?: string;
  };
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface RefreshTokenRequest {
  data: {
    refreshToken: string;
  };
}

export interface Partners {
  partnerName: string;
  accountNumber:string;
  contactPerson:string;
  contactPhone:string;
  accountId:number;
  businessReference:string;
  active:boolean;
  logo:string;
  package:string;
}


export interface Module {
  name: string;
  description: string;
}

export interface ModuleEdit {
  code: string;
  name: string;
  description: string;
}

export interface ModuleSubscription {
  partnerCode: string;
  moduleCode: string;
  startDate: string; // yyyy-MM-dd HH:mm:ss
  endDate: string;   // yyyy-MM-dd HH:mm:ss
}

export interface Route {
  origin: string;
  destination: string;
  partnerCode: string;
  estimatedDistance: number;
  estimatedDuration: number;
}

export interface RouteEdit {
  routeId: number;
  origin: string;
  destination: string;
  estimatedDistance: number;
  estimatedDuration: number;
  status: "ACTIVE" | "INACTIVE" | "DELETED";
}


export interface Trip {
  routeId: number;
  busId: number;
  tripDate: string; // yyyy-MM-dd
}

export interface TripEdit {
  id: number;
  routeId: number;
  busId: number;
  tripDate: string; // yyyy-MM-dd
}


export interface UserCreate {
  role: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  partner: string;
}

export interface UserLogin {
  email: string;
  password: string;
}

export interface UserRefresh {
  refreshToken: string;
}



export interface Vehicle {
  registrationNumber: string;
  type: string; // BUS, SHUTTLE, etc.
  partnerCode: string;
  capacity: number;
}

export interface VehicleEdit {
  vehicleId: number;
  registrationNumber: string;
  type: string; // BUS, SHUTTLE, etc.
  capacity: number;
  status: string;
}




