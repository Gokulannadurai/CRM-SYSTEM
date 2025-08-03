import React from 'react';
import styled from 'styled-components';

interface LayoutProps {
  children: React.ReactNode;
}

const LayoutContainer = styled.div`
  display: flex;
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  margin-top: 60px;
`;

const MainContent = styled.div`
  flex: 1;
  display: flex;
  overflow: hidden;
`;



const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <LayoutContainer>
      <MainContent>
        {children}
      </MainContent>
    </LayoutContainer>
  );
};

export default Layout; 