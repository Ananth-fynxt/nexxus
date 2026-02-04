import { ChakraProvider } from '../../../contexts/chakra-context';
import { QueryProvider } from '../../../contexts/query-context';
import { TransactionSessionPage } from './transaction-session-page';

export function TransactionSessionApp() {
    return (
        <ChakraProvider>
            <QueryProvider>
                <TransactionSessionPage />
            </QueryProvider>
        </ChakraProvider>
    );
}
