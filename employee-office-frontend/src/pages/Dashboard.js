import React, { useState, useEffect } from 'react';
import {
  Box, Heading, Flex, Table, Thead, Tbody, Tr, Th, Td,
  VStack, HStack, Select, Divider, useToast, IconButton,
  Input, Modal, ModalOverlay, ModalContent, ModalHeader,
  ModalBody, ModalFooter, Button, useDisclosure, Text,
  Checkbox, CheckboxGroup, Stack, Image
} from '@chakra-ui/react';
import { Edit2, Trash2 } from 'lucide-react';
import AppButton from '../components/AppButton';
import { useAuth } from '../hooks/useAuth';
import * as api from '../services/api';

const nttColors = {
  primary: '#001A49',
  secondary: '#0070C0',
  accent: '#E2007A',
  background: '#F5F6FA',
  text: '#2D2D2D'
};

export default function Dashboard() {
  const { token, logout } = useAuth();
  const toast = useToast();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [confirmInfo, setConfirmInfo] = useState({ type: '', id: null });

  const [employees, setEmployees] = useState([]);
  const [offices, setOffices] = useState([]);

  const initialEmp = { firstName: '', lastName: '', phone: '', dni: '', address: '', birthDate: '' };
  const [empForm, setEmpForm] = useState(initialEmp);
  const [editingEmpId, setEditingEmpId] = useState(null);

  const initialOff = { name: '', location: '' };
  const [offForm, setOffForm] = useState(initialOff);
  const [editingOffId, setEditingOffId] = useState(null);

  const [assign, setAssign] = useState({ empId: '', officeIds: [] });

  const [searchId, setSearchId] = useState('');
  const [searched, setSearched] = useState(null);

  useEffect(() => { fetchAll(); }, []);

  const fetchAll = async () => {
    try {
      const emps = await api.getEmployeesWithOfficeNames(token);
      const offs = await api.getAllOffices(token);
      setEmployees(emps);
      setOffices(offs);
    } catch (err) {
      toast({ title: 'Error al cargar datos', description: err.message, status: 'error' });
    }
  };

  const safeDelete = async (fn, id, token) => {
    try {
      await fn(id, token);
      toast({ title: confirmInfo.type === 'emp' ? 'Empleado eliminado' : 'Oficina eliminada', status: 'success' });
      fetchAll();
    } catch (err) {
      toast({ title: 'Error eliminando', description: err.message, status: 'error' });
    }
  };

  const handleEmpChange = e => setEmpForm({ ...empForm, [e.target.name]: e.target.value });
  const handleOffChange = e => setOffForm({ ...offForm, [e.target.name]: e.target.value });
  const handleSearchChange = e => setSearchId(e.target.value);

  const submitEmployee = async () => {
    if (Object.values(empForm).some(field => field === '')) {
      toast({ 
        title: 'Ups!', 
        description: 'Se deben completar todos los campos', 
        status: 'warning',
        duration: 3000,
        isClosable: true 
      });
      return;
    }
    
    try {
      if (editingEmpId) await api.updateEmployee(editingEmpId, empForm, token);
      else await api.createEmployee(empForm, token);
      toast({ title: editingEmpId ? 'Empleado actualizado' : 'Empleado creado', status: 'success' });
      setEditingEmpId(null);
      setEmpForm(initialEmp);
      fetchAll();
    } catch (err) {
      toast({ title: 'Error', description: err.message, status: 'error' });
    }
  };
  const editEmployee = emp => { setEditingEmpId(emp.id); setEmpForm(emp); };

  const submitOffice = async () => {
    if (Object.values(offForm).some(field => field === '')) {
      toast({
        title: 'Ups!',
        description: 'Se deben completar todos los campos',
        status: 'warning',
        duration: 3000,
        isClosable: true
      });
      return;
    }
    
    try {
      if (editingOffId) await api.updateOffice(editingOffId, offForm, token);
      else await api.createOffice(offForm, token);
      toast({ title: editingOffId ? 'Oficina actualizada' : 'Oficina creada', status: 'success' });
      setEditingOffId(null);
      setOffForm(initialOff);
      fetchAll();
    } catch (err) {
      toast({ title: 'Error', description: err.message, status: 'error' });
    }
  };
  const editOffice = off => { setEditingOffId(off.id); setOffForm(off); };

  const confirmDelete = (type, id) => { setConfirmInfo({ type, id }); onOpen(); };
  const onConfirm = () => {
    onClose();
    if (confirmInfo.type === 'emp') safeDelete(api.deleteEmployee, confirmInfo.id, token);
    else safeDelete(api.deleteOffice, confirmInfo.id, token);
  };

  const handleAssignEmp = e => setAssign({ ...assign, empId: e.target.value });
  const handleAssignOffices = (values) => setAssign({ ...assign, officeIds: values.map(Number) });
  const doAssign = async () => {
    if (!assign.empId) {
      toast({
        title: 'Ups!',
        description: 'Debe seleccionar un empleado primero',
        status: 'warning',
        duration: 3000,
        isClosable: true
      });
      return;
    }
    
    try {
      await api.assignOfficesToEmployee(assign.empId, assign.officeIds, token);
      toast({ title: 'Oficinas asignadas', status: 'success' });
      setAssign({ empId: '', officeIds: [] });
      fetchAll();
    } catch (err) {
      toast({ title: 'Error', description: err.message, status: 'error' });
    }
  };

  const doSearch = async () => {
    if (!searchId.trim()) {
      toast({
        title: 'Ups!',
        description: 'Se debe ingresar un ID de Empleado',
        status: 'warning',
        duration: 3000,
        isClosable: true
      });
      return;
    }
  
    try {
      const res = await api.getEmployeeWithOfficesById(searchId, token);
      setSearched(res);
    } catch (err) {
      toast({ 
        title: 'No encontrado', 
        description: err.message, 
        status: 'warning' 
      });
      setSearched(null);
    }
  };

  return (
    <Box minH="100vh" bg={nttColors.background}>
      <Flex as="nav" bg={nttColors.primary} p={4} position="fixed" width="100%" top={0} zIndex={1000} align="center" justify="space-between">
        <Image src={`${process.env.PUBLIC_URL}/images/ntt-logo.png`} alt="NTT Data Logo" height="40px"/>
        <AppButton onClick={logout} bg={nttColors.secondary} color="white"   _hover={{
    bg: 'transparent',
    border: '2px solid white',
  }} borderRadius="20px" px={8} position="relative" top="8px">
          Cerrar Sesión
        </AppButton>
      </Flex>

      <Box pt="100px" px={8}>
        <Heading size="xl" textAlign="center" mb={6} color={nttColors.primary} fontFamily="Arial, sans-serif">
          Gestión de Empleados y Oficinas
        </Heading>

        <Divider mb={6} borderColor={nttColors.primary}/>

        <Modal isOpen={isOpen} onClose={onClose} isCentered>
          <ModalOverlay/>
          <ModalContent borderRadius="xl">
            <ModalHeader bg={nttColors.primary} color="white" borderTopRadius="xl">Confirmar eliminación</ModalHeader>
            <ModalBody py={6}><Text>¿Estás seguro que deseas eliminar este registro?</Text></ModalBody>
            <ModalFooter>
              <Button variant="outline" onClick={onClose} borderColor={nttColors.primary} color={nttColors.primary} _hover={{ bg: nttColors.background }}>
                Cancelar
              </Button>
              <Button bg={nttColors.accent} color="white" _hover={{ bg: '#C1006A' }} ml={3} onClick={onConfirm}>
                Confirmar
              </Button>
            </ModalFooter>
          </ModalContent>
        </Modal>

        <Flex gap={6} wrap="wrap" alignItems="flex-start">
          <VStack align="start" flex="1" minW="400px" bg="white" p={6} borderRadius="xl" boxShadow="md">
            <Heading size="md" color={nttColors.primary} mb={4}>{editingEmpId ? 'Editar Empleado' : 'Nuevo Empleado'}</Heading>
            <Input placeholder="Nombre" name="firstName" value={empForm.firstName} onChange={handleEmpChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <Input placeholder="Apellido" name="lastName" value={empForm.lastName} onChange={handleEmpChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <Input placeholder="Teléfono" name="phone" value={empForm.phone} onChange={handleEmpChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <Input placeholder="DNI" name="dni" value={empForm.dni} onChange={handleEmpChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <Input placeholder="Dirección" name="address" value={empForm.address} onChange={handleEmpChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <Input placeholder="Fecha de nacimiento" type="date" name="birthDate" value={empForm.birthDate} onChange={handleEmpChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <AppButton onClick={submitEmployee} bg={nttColors.secondary} color="white" _hover={{ bg: '#005A9E' }} width="100%">
              {editingEmpId ? 'Guardar' : 'Crear'}
            </AppButton>
            <Box w="100%" overflowX="auto" mt={6}>
              <Table variant="striped" colorScheme="gray">
                <Thead bg={nttColors.primary}>
                  <Tr>
                    <Th color="white">ID</Th><Th color="white">Nombre</Th><Th color="white">DNI</Th><Th color="white">Teléfono</Th>
                    <Th color="white">Dirección</Th><Th color="white">Fecha Nac.</Th><Th color="white">Oficinas</Th><Th color="white">Acciones</Th>
                  </Tr>
                </Thead>
                <Tbody>
                  {employees.map(emp => (
                    <Tr key={emp.id} _hover={{ bg: nttColors.background }}>
                      <Td>{emp.id}</Td>
                      <Td>{emp.firstName} {emp.lastName}</Td>
                      <Td>{emp.dni}</Td>
                      <Td>{emp.phone}</Td>
                      <Td>{emp.address}</Td>
                      <Td>{emp.birthDate || 'N/A'}</Td>
                      <Td>{emp.officeNames?.join(', ') || 'Ninguna'}</Td>
                      <Td>
                        <HStack>
                          <IconButton icon={<Edit2/>} onClick={() => editEmployee(emp)} size="xs" colorScheme="blue"/>
                          <IconButton icon={<Trash2/>} onClick={() => confirmDelete('emp', emp.id)} size="xs" colorScheme="red"/>
                        </HStack>
                      </Td>
                    </Tr>
                  ))}
                </Tbody>
              </Table>
            </Box>
          </VStack>

          <VStack align="start" flex="0 0 300px" bg="white" p={6} borderRadius="xl" boxShadow="md">
            <Heading size="md" color={nttColors.primary} mb={4}>{editingOffId ? 'Editar Oficina' : 'Nueva Oficina'}</Heading>
            <Input placeholder="Nombre" name="name" value={offForm.name} onChange={handleOffChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <Input placeholder="Ubicación" name="location" value={offForm.location} onChange={handleOffChange} focusBorderColor={nttColors.secondary} mb={3}/>
            <AppButton onClick={submitOffice} bg={nttColors.secondary} color="white" _hover={{ bg: '#005A9E' }} width="100%">
              {editingOffId ? 'Guardar' : 'Crear'}
            </AppButton>
            <Box w="100%" maxH="300px" overflowY="auto" mt={6}>
              <Table variant="striped" colorScheme="gray">
                <Thead bg={nttColors.primary}>
                  <Tr><Th color="white">ID</Th><Th color="white">Nombre</Th><Th color="white">Ubicación</Th><Th color="white">Acciones</Th></Tr>
                </Thead>
                <Tbody>
                  {offices.map(off => (
                    <Tr key={off.id} _hover={{ bg: nttColors.background }}>
                      <Td>{off.id}</Td>
                      <Td>{off.name}</Td>
                      <Td>{off.location}</Td>
                      <Td>
                        <HStack>
                          <IconButton icon={<Edit2/>} onClick={() => editOffice(off)} size="xs" colorScheme="blue"/>
                          <IconButton icon={<Trash2/>} onClick={() => confirmDelete('off', off.id)} size="xs" colorScheme="red"/>
                        </HStack>
                      </Td>
                    </Tr>
                  ))}
                </Tbody>
              </Table>
            </Box>
          </VStack>

          <VStack align="start" flex="0 0 300px" bg="white" p={6} borderRadius="xl" boxShadow="md">
            <Heading size="md" color={nttColors.primary} mb={4}>Asignar Oficinas</Heading>
            <Select placeholder="Selecciona empleado" value={assign.empId} onChange={handleAssignEmp} focusBorderColor={nttColors.secondary} mb={3}>
              {employees.map(emp => <option key={emp.id} value={emp.id}>{emp.firstName} {emp.lastName}</option>)}
            </Select>
            <Box w="100%" maxH="300px" overflowY="auto" border="1px solid" borderColor="gray.200" p={2} borderRadius="md">
              <CheckboxGroup value={assign.officeIds.map(String)} onChange={handleAssignOffices}>
                <Stack spacing={1}>
                  {offices.map(off => (
                    <Checkbox key={off.id} value={off.id.toString()} colorScheme="blue">{off.name}</Checkbox>
                  ))}
                </Stack>
              </CheckboxGroup>
            </Box>
            <AppButton onClick={doAssign} bg={nttColors.accent} color="white" _hover={{ bg: '#C1006A' }} width="100%" mt={3}>
              Asignar
            </AppButton>
          </VStack>
        </Flex>

        <VStack align="start" mt={10} bg="white" p={6} borderRadius="xl" boxShadow="md">
          <Heading size="md" color={nttColors.primary} mb={4}>Búsqueda de Empleado por ID</Heading>
          <HStack w="100%" mb={4} alignItems="flex-start">
            <Input 
              placeholder="ID del Empleado" 
              value={searchId} 
              onChange={handleSearchChange} 
              w="200px" 
              h="40px" 
              focusBorderColor={nttColors.secondary}
              mb="2px"
            />
            <AppButton 
              onClick={doSearch} 
              h="40px" 
              px={4} 
              bg={nttColors.secondary} 
              color="white" 
              _hover={{ bg: '#005A9E' }}
            >
              Buscar
            </AppButton>
          </HStack>
          {searched && (
            <Box w="100%" p={4} borderWidth={1} borderRadius="md">
              <Heading size="sm" color={nttColors.primary} mb={2}>Empleado {searched.id}</Heading>
              <Text color={nttColors.text}>Nombre: {searched.firstName} {searched.lastName}</Text>
              <Text color={nttColors.text}>Oficinas: {searched.officeNames.join(', ') || 'Ninguna'}</Text>
            </Box>
          )}
        </VStack>
      </Box>
    </Box>
  );
}