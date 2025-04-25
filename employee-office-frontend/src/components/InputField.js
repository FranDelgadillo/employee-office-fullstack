import React from 'react';
import { FormControl, FormLabel, Input } from '@chakra-ui/react';

export default function InputField({ label, type = 'text', value, onChange, name }) {
  return (
    <FormControl mb={4} isRequired>
      <FormLabel htmlFor={name}>{label}</FormLabel>
      <Input id={name} name={name} type={type} value={value} onChange={onChange} />
    </FormControl>
  );
}