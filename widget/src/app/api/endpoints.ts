export const API_ENDPOINTS = {
    transactionSession: {
        get: (sessionToken: string) => `/sessions/${encodeURIComponent(sessionToken)}`,
    },
} as const;
