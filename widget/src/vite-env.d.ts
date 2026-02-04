/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_API_BASE_URL: string;
    readonly VITE_NEXXUS_API_PREFIX: string;
    readonly VITE_SESSION_VALIDATE_INTERVAL_MS: string;
    readonly VITE_SESSION_VALIDATE_INTERVAL_SECONDS: string;
}

export interface ImportMeta {
    readonly env: ImportMetaEnv;
}
