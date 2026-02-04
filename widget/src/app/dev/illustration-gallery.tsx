import { Box, Code, Grid, Heading, Text, VStack } from '@chakra-ui/react';
import { SvgIllustration, type SvgIllustrationKey } from '../ui/svg-illustration';

const ILLUSTRATIONS: SvgIllustrationKey[] = [
    'loading',
    'processing',
    'success',
    'warnings',
    'serverError',
    'serverDown',
    'connectionLost',
    'maintenance',
    'notFound',
    'noData',
    'empty',
    'void',
    'accessDenied',
    'security',
    'surveillance',
    'dataThief',
    'cancel',
];

export function IllustrationGallery() {
    return (
        <Box p={6} bg='white' minH='100vh'>
            <VStack align='stretch' gap={6} maxW='6xl' mx='auto'>
                <VStack align='stretch' gap={1}>
                    <Heading size='lg'>SVG Scenario Gallery</Heading>
                    <Text color='gray.600' fontSize='sm'>
                        DEV-only preview of all SVG illustrations in <Code>/public/svg</Code>.
                    </Text>
                </VStack>

                <Grid
                    gap={6}
                    templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(3, 1fr)' }}
                >
                    {ILLUSTRATIONS.map((key) => (
                        <Box
                            key={key}
                            borderWidth='1px'
                            borderColor='gray.200'
                            borderRadius='lg'
                            p={4}
                        >
                            <VStack align='stretch' gap={3}>
                                <Text fontWeight='semibold'>{key}</Text>
                                <SvgIllustration illustration={key} alt={key} size={220} />
                                <Code fontSize='xs'>{`illustration='${key}'`}</Code>
                            </VStack>
                        </Box>
                    ))}
                </Grid>
            </VStack>
        </Box>
    );
}
