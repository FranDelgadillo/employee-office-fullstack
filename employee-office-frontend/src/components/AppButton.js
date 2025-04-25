import React from 'react';
import { Button } from '@chakra-ui/react';

export default function AppButton({ children, ...props }) {
  return (
    <Button colorScheme="teal" mb={4} {...props}>
      {children}
    </Button>
  );
}
