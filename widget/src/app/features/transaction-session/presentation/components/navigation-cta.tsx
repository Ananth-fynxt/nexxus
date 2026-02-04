import { Button, Text, VStack } from '@chakra-ui/react';
import type { Navigation } from '../../domain/types';

type Props = {
    navigation: Navigation | undefined;
    onOpenPayment: () => void;
};

export function NavigationCta({ navigation, onOpenPayment }: Props) {
    if (!navigation) {
        return (
            <Text color='gray.600' fontSize='sm'>
                No further action required.
            </Text>
        );
    }

    if (navigation.type === 'redirect') {
        return (
            <VStack align='stretch' gap={3}>
                <Text color='gray.700'>Redirecting you to the payment page…</Text>
                <Text color='gray.600' fontSize='sm'>
                    If you are not redirected automatically, please allow pop-ups/redirects and try
                    again.
                </Text>
            </VStack>
        );
    }

    if (navigation.type === 'iframe' || navigation.type === 'form') {
        return (
            <VStack align='stretch' gap={3}>
                <Text color='gray.700'>Opening payment…</Text>
                <Button variant='outline' onClick={onOpenPayment}>
                    Open Payment
                </Button>
            </VStack>
        );
    }

    if (navigation.type === 'none') {
        return (
            <Text color='gray.600' fontSize='sm'>
                No further action required.
            </Text>
        );
    }

    return (
        <Text color='gray.600' fontSize='sm'>
            Your transaction is being processed. Please wait a moment.
        </Text>
    );
}
