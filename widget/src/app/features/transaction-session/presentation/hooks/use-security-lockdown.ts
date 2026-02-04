import { useEffect } from 'react';

export type SecurityIncident = 'dataThief';

export function useSecurityLockdown(incident: SecurityIncident | null) {
    useEffect(() => {
        if (!incident) return;
        if (typeof window === 'undefined') return;

        try {
            window.sessionStorage.clear();
        } catch {
            // ignore
        }
        try {
            window.localStorage.clear();
        } catch {
            // ignore
        }

        const timeout = window.setTimeout(() => {
            try {
                window.close();
            } catch {
                // ignore
            }
            window.location.replace('about:blank');
        }, 2500);

        return () => {
            window.clearTimeout(timeout);
        };
    }, [incident]);
}
