const BASE_URL = process.env.REACT_APP_API_URL || '/api/v1';

async function request(path, { method = 'GET', body, token } = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` })
  };
  const resp = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body,
  });
  if (!resp.ok) {
    const error = await resp.json().catch(() => ({ message: resp.statusText }));
    throw new Error(error.error || error.message || resp.statusText);
  }
  const text = await resp.text();
  return text ? JSON.parse(text) : null;
}

/* —— auth —— */
export const register = (username, password) =>
  request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });

export const login = (username, password) =>
  request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });

/* —— empleados —— */
export const getAllEmployees = (token) =>
  request('/employees', { token });

export const getEmployeeWithOffices = (id, token) =>
  request(`/employees/${id}/withOffices`, { token });

// Dashboard
export const getEmployeeWithOfficesById = (id, token) =>
  getEmployeeWithOffices(id, token);

export const getEmployeesWithOfficeNames = (token) =>
  request('/employees/withOffices', { token });

export const createEmployee = (employeeData, token) =>
  request('/employees', {
    method: 'POST',
    body: JSON.stringify(employeeData),
    token,
  });

export const updateEmployee = (id, employeeData, token) =>
  request(`/employees/${id}`, {
    method: 'PUT',
    body: JSON.stringify(employeeData),
    token,
  });

export const deleteEmployee = (id, token) =>
  request(`/employees/${id}`, { method: 'DELETE', token });

export const assignOfficesToEmployee = (id, officeIds, token) =>
  request(`/employees/${id}/assignOffices`, {
    method: 'PATCH',
    body: JSON.stringify(officeIds),
    token,
  });

/* —— oficinas —— */
export const getAllOffices = (token) =>
  request('/offices', { token });

export const getOfficeById = (id, token) =>
  request(`/offices/${id}`, { token });

export const createOffice = (officeData, token) =>
  request('/offices', {
    method: 'POST',
    body: JSON.stringify(officeData),
    token,
  });

export const updateOffice = (id, officeData, token) =>
  request(`/offices/${id}`, {
    method: 'PUT',
    body: JSON.stringify(officeData),
    token,
  });

export const deleteOffice = (id, token) =>
  request(`/offices/${id}`, { method: 'DELETE', token });