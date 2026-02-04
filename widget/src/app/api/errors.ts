import type { ApiErrorResponse } from './types';

export class ApiError extends Error {
    public statusCode: number;
    public response?: ApiErrorResponse;
    public originalError?: unknown;

    constructor(
        message: string,
        statusCode: number = 500,
        response?: ApiErrorResponse,
        originalError?: unknown,
    ) {
        super(message);
        this.name = 'ApiError';
        this.statusCode = statusCode;
        this.response = response;
        this.originalError = originalError;

        const captureStackTrace = (
            Error as ErrorConstructor & {
                captureStackTrace?: (targetObject: object) => void;
            }
        ).captureStackTrace;
        captureStackTrace?.(this);
    }

    getErrorMessage(): string {
        if (this.response?.message) {
            return this.response.message;
        }
        if (this.response?.error) {
            return this.response.error;
        }
        return this.message || 'An unknown error occurred';
    }

    getValidationErrors(): Record<string, string[]> | null {
        return this.response?.errors || null;
    }

    isClientError(): boolean {
        return this.statusCode >= 400 && this.statusCode < 500;
    }

    isServerError(): boolean {
        return this.statusCode >= 500 && this.statusCode < 600;
    }

    isAuthError(): boolean {
        return this.statusCode === 401;
    }

    isForbiddenError(): boolean {
        return this.statusCode === 403;
    }

    isNotFoundError(): boolean {
        return this.statusCode === 404;
    }

    isNetworkError(): boolean {
        return this.statusCode === 0;
    }
}

export function createApiError(error: unknown): ApiError {
    if (error instanceof ApiError) {
        return error;
    }

    if (typeof error === 'object' && error !== null && 'isAxiosError' in error) {
        const axiosLike = error as {
            isAxiosError?: boolean;
            code?: string;
            message?: string;
            response?: {
                status?: number;
                data?: ApiErrorResponse;
                statusText?: string;
            };
        };

        if (axiosLike.isAxiosError) {
            const hasResponse =
                typeof axiosLike.response === 'object' && axiosLike.response !== null;

            if (!hasResponse) {
                const message =
                    axiosLike.code === 'ECONNABORTED' ? 'Request timed out' : 'Network error';
                return new ApiError(message, 0, undefined, error);
            }

            const statusCode = axiosLike.response?.status || 500;
            const response = axiosLike.response?.data;
            const message =
                response?.message ||
                response?.error ||
                axiosLike.response?.statusText ||
                axiosLike.message ||
                'An error occurred';

            return new ApiError(message, statusCode, response, error);
        }
    }

    if (
        typeof error === 'object' &&
        error !== null &&
        'response' in error &&
        typeof (error as { response?: unknown }).response === 'object'
    ) {
        const axiosError = error as {
            response?: {
                status?: number;
                data?: ApiErrorResponse;
                statusText?: string;
            };
            message?: string;
        };

        const statusCode = axiosError.response?.status || 500;
        const response = axiosError.response?.data;
        const message =
            response?.message ||
            response?.error ||
            axiosError.response?.statusText ||
            axiosError.message ||
            'An error occurred';

        return new ApiError(message, statusCode, response, error);
    }

    if (
        typeof error === 'object' &&
        error !== null &&
        'message' in error &&
        typeof (error as { message?: string }).message === 'string'
    ) {
        const networkError = error as { message?: string };
        return new ApiError(networkError.message || 'Network error occurred', 0, undefined, error);
    }

    return new ApiError(
        error instanceof Error ? error.message : 'An unknown error occurred',
        500,
        undefined,
        error,
    );
}
