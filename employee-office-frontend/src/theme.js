import { extendTheme } from '@chakra-ui/react';

const theme = extendTheme({
  colors: {
    ntt: {
      primary: '#001A49',
      secondary: '#0070C0',
      accent: '#E2007A',
      background: '#F5F6FA',
      text: '#2D2D2D'
    }
  },
  fonts: {
    heading: 'Arial, sans-serif',
    body: 'Arial, sans-serif',
  },
  components: {
    Button: {
      baseStyle: {
        fontWeight: '500',
        borderRadius: '20px',
      },
    },
    Input: {
      baseStyle: {
        field: {
          _focus: {
            borderColor: '#0070C0',
            boxShadow: '0 0 0 1px #0070C0'
          }
        }
      }
    }
  }
});

export default theme;