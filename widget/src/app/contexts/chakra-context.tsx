import { ChakraProvider as BaseChakraProvider } from '@chakra-ui/react';
import type { ReactNode } from 'react';
import { system } from '../styles/themes/theme';

interface ChakraProviderProps {
    children: ReactNode;
}

export function ChakraProvider({ children }: ChakraProviderProps) {
    return <BaseChakraProvider value={system}>{children}</BaseChakraProvider>;
}
