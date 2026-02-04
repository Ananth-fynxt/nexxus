import { useEffect, useState } from 'react';

function getSessionTokenFromUrl(): string | null {
    if (typeof window === 'undefined') return null;

    const qs = new URLSearchParams(window.location.search);
    const qp = qs.get('session') || qs.get('sessionToken') || qs.get('token') || null;
    const qpTrimmed = qp?.trim();
    if (qpTrimmed) return qpTrimmed;

    const path = window.location.pathname.replace(/\/+$/, '');
    if (!path) return null;

    const marker = '/sessions/';
    const idx = path.indexOf(marker);
    if (idx >= 0) {
        const token = path.slice(idx + marker.length);
        return token || null;
    }

    const parts = path.split('/').filter(Boolean);
    if (parts.length === 0) return null;
    return parts[parts.length - 1] || null;
}

function isBackForwardNavigation(): boolean {
    if (typeof window === 'undefined') return false;
    const nav = performance.getEntriesByType('navigation')[0];
    if (!nav) return false;
    return (nav as PerformanceNavigationTiming).type === 'back_forward';
}

export function useSessionToken(): string | null {
    const [token, setToken] = useState<string | null>(() => getSessionTokenFromUrl());

    useEffect(() => {
        if (typeof window === 'undefined') return;

        const updateTokenFromUrl = () => {
            setToken(getSessionTokenFromUrl());
        };

        const onPageShow = (event: PageTransitionEvent) => {
            if (event.persisted || isBackForwardNavigation()) {
                window.location.reload();
                return;
            }
            updateTokenFromUrl();
        };

        window.addEventListener('popstate', updateTokenFromUrl);
        window.addEventListener('pageshow', onPageShow);

        return () => {
            window.removeEventListener('popstate', updateTokenFromUrl);
            window.removeEventListener('pageshow', onPageShow);
        };
    }, []);

    return token;
}
