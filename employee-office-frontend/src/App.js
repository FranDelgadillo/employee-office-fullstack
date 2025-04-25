import { Navigate } from 'react-router-dom';
import { Routes, Route } from 'react-router-dom';
import { ChakraProvider, CSSReset } from '@chakra-ui/react';
import { AuthProvider } from './context/AuthContext';
import theme from './theme';
import Register from './pages/Register';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import PrivateRoute from './components/PrivateRoute';

const GlobalStyle = ({ children }) => (
  <>
    <CSSReset />
    {children}
  </>
);

function App() {
  return (
    <ChakraProvider theme={theme}>
      <GlobalStyle>
        <AuthProvider>
          <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/dashboard" element={
              <PrivateRoute>
                <Dashboard/>
              </PrivateRoute>
            }/>
            <Route path="*" element={<Navigate to="/register" />} />
          </Routes>
        </AuthProvider>
      </GlobalStyle>
    </ChakraProvider>
  );
}

export default App;