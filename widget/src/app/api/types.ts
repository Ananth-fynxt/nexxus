export interface ApiErrorResponse {
    message: string;
    statusCode?: number;
    errors?: Record<string, string[]>;
    error?: string;
}

export interface ApiResponse<T = unknown> {
    data: T;
    message?: string;
    status: number;
}

export interface ApiRequestConfig {
    headers?: Record<string, string>;
    skipAuth?: boolean;
    timeout?: number;
}
