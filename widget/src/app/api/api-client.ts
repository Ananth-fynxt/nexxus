import axios, {
    type AxiosInstance,
    type AxiosRequestConfig,
    type InternalAxiosRequestConfig,
} from 'axios';
import type { ApiRequestConfig, ApiResponse } from './types';
import { createApiError } from './errors';

class ApiClient {
    private client: AxiosInstance;
    private baseURL: string;

    constructor(baseURL?: string) {
        this.baseURL =
            baseURL ||
            `${import.meta.env.VITE_API_BASE_URL}${import.meta.env.VITE_NEXXUS_API_PREFIX}`;

        this.client = axios.create({
            baseURL: this.baseURL,
            timeout: 30000,
            headers: {
                'Content-Type': 'application/json',
                Accept: 'application/json',
            },
        });

        this.client.interceptors.request.use(
            this.handleRequest.bind(this),
            this.handleRequestError.bind(this),
        );

        this.client.interceptors.response.use(
            (response) => response,
            this.handleResponseError.bind(this),
        );
    }

    private handleRequest(config: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
        if (config.headers) {
            Object.assign(config.headers, (config as ApiRequestConfig).headers);
        }

        return config;
    }

    private handleRequestError(error: unknown): Promise<never> {
        return Promise.reject(createApiError(error));
    }

    private handleResponseError(error: unknown): Promise<never> {
        const apiError = createApiError(error);
        return Promise.reject(apiError);
    }

    async get<T = unknown>(
        url: string,
        config?: AxiosRequestConfig & ApiRequestConfig,
    ): Promise<ApiResponse<T>> {
        try {
            const response = await this.client.get<T>(url, config);
            return {
                data: response.data,
                status: response.status,
                message: (response.data as { message?: string })?.message,
            };
        } catch (error) {
            throw createApiError(error);
        }
    }

    async post<T = unknown, D = unknown>(
        url: string,
        data?: D,
        config?: AxiosRequestConfig & ApiRequestConfig,
    ): Promise<ApiResponse<T>> {
        try {
            const response = await this.client.post<T>(url, data, config);
            return {
                data: response.data,
                status: response.status,
                message: (response.data as { message?: string })?.message,
            };
        } catch (error) {
            throw createApiError(error);
        }
    }

    async put<T = unknown, D = unknown>(
        url: string,
        data?: D,
        config?: AxiosRequestConfig & ApiRequestConfig,
    ): Promise<ApiResponse<T>> {
        try {
            const response = await this.client.put<T>(url, data, config);
            return {
                data: response.data,
                status: response.status,
                message: (response.data as { message?: string })?.message,
            };
        } catch (error) {
            throw createApiError(error);
        }
    }

    async patch<T = unknown, D = unknown>(
        url: string,
        data?: D,
        config?: AxiosRequestConfig & ApiRequestConfig,
    ): Promise<ApiResponse<T>> {
        try {
            const response = await this.client.patch<T>(url, data, config);
            return {
                data: response.data,
                status: response.status,
                message: (response.data as { message?: string })?.message,
            };
        } catch (error) {
            throw createApiError(error);
        }
    }

    async delete<T = unknown>(
        url: string,
        config?: AxiosRequestConfig & ApiRequestConfig,
    ): Promise<ApiResponse<T>> {
        try {
            const response = await this.client.delete<T>(url, config);
            return {
                data: response.data,
                status: response.status,
                message: (response.data as { message?: string })?.message,
            };
        } catch (error) {
            throw createApiError(error);
        }
    }
}

export const apiClient = new ApiClient();

export { ApiClient };
