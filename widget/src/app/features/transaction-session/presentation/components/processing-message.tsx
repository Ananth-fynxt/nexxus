import { Text, VStack } from '@chakra-ui/react';

export function ProcessingMessage() {
    return (
        <VStack align='stretch' gap={2}>
            <Text color='gray.700' fontWeight='semibold'>
                Your transaction is being processed
            </Text>
            <Text color='gray.600' fontSize='sm'>
                Kindly wait for a moment. You can keep this page open.
            </Text>
        </VStack>
    );
}
