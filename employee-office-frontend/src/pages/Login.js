import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Heading, Flex, Image, useToast } from '@chakra-ui/react';
import InputField from '../components/InputField';
import AppButton from '../components/AppButton';
import { login as apiLogin } from '../services/api';
import { useAuth } from '../hooks/useAuth';

const nttColors = {
  primary: '#001A49',
  secondary: '#0070C0',
  accent: '#E2007A',
  background: '#F5F6FA',
  text: '#2D2D2D'
};

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const { login } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      const { token } = await apiLogin(form.username, form.password);
      login(token);
      navigate('/dashboard');
    } catch (err) {
      toast({ title: 'Error', description: err.message, status: 'error', duration: 4000 });
    }
  };

  return (
    <Box minH="100vh" bg={nttColors.background}>
    <Flex
        as="nav"
        bg={nttColors.primary}
        p={4}
        position="fixed"
        width="100%"
        top={0}
        zIndex={1000}
        align="center"
        justify="space-between"
      >
        <Image
          src={`${process.env.PUBLIC_URL}/images/ntt-logo.png`}
          alt="NTT Data Logo"
          height="40px"
        />
        <AppButton
          onClick={() => navigate('/register')}
          bg={nttColors.secondary}
          color="white"
          _hover={{
            bg: 'transparent',
            border: '2px solid white',
          }}
          borderRadius="20px"
          px={8}
          position="relative"
          top="8px"
        >
          Register
        </AppButton>
      </Flex>

      <Box pt="120px" px={8} maxW="md" mx="auto">
        <Box bg="white" p={8} borderRadius="xl" boxShadow="md">
          <Heading size="xl" textAlign="center" mb={8} color={nttColors.primary}>
            Inicio de Sesión
          </Heading>
          
          <form onSubmit={handleSubmit}>
            <InputField 
              label="Usuario" 
              name="username" 
              value={form.username} 
              onChange={handleChange}
              focusBorderColor={nttColors.secondary}
            />
            <InputField 
              label="Contraseña" 
              type="password" 
              name="password" 
              value={form.password} 
              onChange={handleChange}
              focusBorderColor={nttColors.secondary}
            />
            
            <AppButton 
              type="submit" 
              width="full" 
              bg={nttColors.secondary} 
              color="white" 
              _hover={{ bg: '#005A9E' }}
              mt={4}
            >
              Ingresar
            </AppButton>
          </form>
        </Box>
      </Box>
    </Box>
  );
}