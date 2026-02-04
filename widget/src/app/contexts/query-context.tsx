import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useMemo, type ReactNode } from 'react';

interface QueryProviderProps {
    children: ReactNode;
    queryClient?: QueryClient;
}

export function QueryProvider({ children, queryClient: externalQueryClient }: QueryProviderProps) {
    const queryClient = useMemo(() => {
        if (externalQueryClient) {
            return externalQueryClient;
        }

        return new QueryClient({
            defaultOptions: {
                queries: {
                    refetchOnWindowFocus: false,
                    refetchOnReconnect: false,
                    retry: 1,
                    staleTime: 5 * 60 * 1000, // 5 minutes
                },
                mutations: {
                    retry: 1,
                },
            },
        });
    }, [externalQueryClient]);

    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
