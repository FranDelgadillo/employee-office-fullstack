import { Global, css } from '@emotion/react';

export const GlobalStyle = props => (
  <Global
    {...props}
    styles={css`
      body {
        background-color: #F5F6FA;
      }
    `}
  />
);