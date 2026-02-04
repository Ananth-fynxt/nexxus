import { useEffect, useRef, useState } from 'react';

function isDevtoolsLikelyOpen(): boolean {
    if (typeof window === 'undefined') return false;
    const widthGap = window.outerWidth - window.innerWidth;
    const heightGap = window.outerHeight - window.innerHeight;
    const threshold = 120;
    return widthGap > threshold || heightGap > threshold;
}

export function useDevtoolsGuard(): boolean {
    const [showSurveillance, setShowSurveillance] = useState(false);
    const lockedRef = useRef(false);
    const lockedAtRef = useRef<number | null>(null);
    const sawOpenRef = useRef(false);
    const lastClosedAtRef = useRef<number | null>(null);
    const intervalRef = useRef<number | null>(null);

    useEffect(() => {
        if (typeof window === 'undefined') return;

        // Once triggered, keep the surveillance overlay visible until DevTools is
        // likely closed for a short continuous window (best-effort heuristic).
        const lock = () => {
            lockedRef.current = true;
            lockedAtRef.current = Date.now();
            sawOpenRef.current = false;
            lastClosedAtRef.current = null;
            setShowSurveillance(true);
        };

        const unlockIfClosedLongEnough = () => {
            if (!lockedRef.current) return;
            const open = isDevtoolsLikelyOpen();
            if (open) {
                sawOpenRef.current = true;
                lastClosedAtRef.current = null;
                setShowSurveillance(true);
                return;
            }

            const now = Date.now();
            // If we never observe a "devtools open" signal, still keep the overlay
            // up for a while after an inspect attempt (best-effort). Some browsers /
            // undocked DevTools won't be detectable via size-gap.
            if (!sawOpenRef.current && lockedAtRef.current !== null) {
                if (now - lockedAtRef.current < 15000) {
                    setShowSurveillance(true);
                    return;
                }
            }

            if (lastClosedAtRef.current === null) {
                lastClosedAtRef.current = now;
                return;
            }

            // Require ~1.5s of "closed" signal before hiding the overlay,
            // to avoid flicker / false negatives.
            if (now - lastClosedAtRef.current >= 1500) {
                lockedRef.current = false;
                lockedAtRef.current = null;
                sawOpenRef.current = false;
                lastClosedAtRef.current = null;
                setShowSurveillance(false);
            }
        };

        const onContextMenu = (e: MouseEvent) => {
            e.preventDefault();
            lock();
        };

        const onKeyDown = (e: KeyboardEvent) => {
            const key = e.key.toLowerCase();
            const isF12 = e.key === 'F12';
            const isCtrlShift = e.ctrlKey && e.shiftKey;
            const isCmdOpt = e.metaKey && e.altKey;

            if (isF12) {
                e.preventDefault();
                lock();
                return;
            }
            if (isCtrlShift && (key === 'i' || key === 'j' || key === 'c')) {
                e.preventDefault();
                lock();
                return;
            }
            if (isCmdOpt && (key === 'i' || key === 'j' || key === 'c')) {
                e.preventDefault();
                lock();
            }
        };

        window.addEventListener('contextmenu', onContextMenu);
        window.addEventListener('keydown', onKeyDown, { capture: true });

        // Poll for DevTools open/close signal.
        intervalRef.current = window.setInterval(() => {
            if (isDevtoolsLikelyOpen()) {
                lock();
            }
            unlockIfClosedLongEnough();
        }, 250);

        return () => {
            window.removeEventListener('contextmenu', onContextMenu);
            window.removeEventListener('keydown', onKeyDown, {
                capture: true,
            } as AddEventListenerOptions);
            if (intervalRef.current !== null) {
                window.clearInterval(intervalRef.current);
                intervalRef.current = null;
            }
        };
    }, []);

    return showSurveillance;
}
