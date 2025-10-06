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
