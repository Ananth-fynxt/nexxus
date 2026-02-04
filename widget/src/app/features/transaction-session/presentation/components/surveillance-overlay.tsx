import { Box, Text, VStack } from '@chakra-ui/react';
import { SvgIllustration } from '../../../../ui/svg-illustration';

type Props = {
    isOpen: boolean;
};

export function SurveillanceOverlay({ isOpen }: Props) {
    if (!isOpen) return null;

    return (
        <Box
            position='fixed'
            inset={0}
            zIndex={10000}
            bg='white'
            display='flex'
            alignItems='center'
            justifyContent='center'
            px={6}
        >
            <VStack align='center' gap={3} maxW='md' textAlign='center'>
                <SvgIllustration illustration='surveillance' alt='Security check' size={260} />
                <Text color='gray.800' fontWeight='semibold'>
                    Security check in progress
                </Text>
                <Text color='gray.600' fontSize='sm'>
                    Please close developer tools to continue securely.
                </Text>
            </VStack>
        </Box>
    );
}
