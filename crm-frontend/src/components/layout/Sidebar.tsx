import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { addNotification } from '../../store/slices/uiSlice';
import { logout } from '../../store/slices/authSlice';
import TokenManager from '../../utils/tokenManager';

interface SidebarProps {
  isOpen: boolean;
}

const SidebarContainer = styled.aside<{ isOpen: boolean }>`
  width: ${props => props.isOpen ? '250px' : '60px'};
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transition: width 0.3s ease;
  overflow: hidden;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  z-index: 999;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 60px;
  left: 0;
  bottom: 0;
  height: calc(100vh - 60px);
`;



const NavMenu = styled.nav`
  padding: 20px 0;
`;

const NavItem = styled(Link)<{ active: boolean; isMain?: boolean; isOpen: boolean }>`
  display: flex;
  align-items: center;
  justify-content: ${props => props.isOpen ? 'flex-start' : 'center'};
  padding: ${props => props.isOpen 
    ? (props.isMain ? '16px 20px' : '10px 20px 10px 40px')
    : '16px 0'
  };
  color: white;
  text-decoration: none;
  transition: background-color 0.2s;
  border-left: 3px solid ${props => props.active ? '#fff' : 'transparent'};
  background-color: ${props => props.active ? 'rgba(255, 255, 255, 0.1)' : 'transparent'};
  font-weight: ${props => props.isMain ? '600' : '400'};
  font-size: ${props => props.isMain ? '1rem' : '0.9rem'};

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
    text-decoration: none;
    color: white;
  }
`;

const NavIcon = styled.span<{ isOpen: boolean }>`
  margin-right: ${props => props.isOpen ? '12px' : '0'};
  font-size: 1.1rem;
  min-width: 20px;
  text-align: center;
`;

const NavText = styled.span`
  white-space: nowrap;
  font-weight: 500;
`;

const UserSection = styled.div`
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-top: auto;
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 10px;
`;

const UserAvatar = styled.div`
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-weight: bold;
`;

const UserDetails = styled.div`
  flex: 1;
  white-space: nowrap;
`;

const UserName = styled.div`
  font-weight: 600;
  font-size: 0.9rem;
`;

const UserRole = styled.div`
  font-size: 0.8rem;
  opacity: 0.8;
`;

const LogoutButton = styled.button`
  width: 100%;
  background: rgba(220, 53, 69, 0.8);
  border: 1px solid rgba(220, 53, 69, 0.9);
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;

  &:hover {
    background-color: rgba(220, 53, 69, 1);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
  }

  &:active {
    transform: translateY(0);
  }
`;

const Sidebar: React.FC<SidebarProps> = ({ isOpen }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { user } = useSelector((state: RootState) => state.auth);

  const navigationItems = [
    { path: '/', label: 'Dashboard', icon: '📊', isMain: true },
    { path: '/customers', label: 'Customers', icon: '👥', isMain: false },
    { path: '/leads', label: 'Leads', icon: '🎯', isMain: false },
    { path: '/tasks', label: 'Tasks', icon: '✅', isMain: false },
    { path: '/users', label: 'Users', icon: '👤', isMain: false },
  ];

  const handleLogout = () => {
    // Clear authentication data
    TokenManager.clearTokens();
    localStorage.removeItem('user');
    
    // Dispatch logout action
    dispatch(logout());
    
    // Add notification
    dispatch(addNotification({
      type: 'success',
      message: 'You have been logged out successfully',
      title: 'Logout',
      duration: 3000,
    }));

    // Redirect to login page
    navigate('/login');
  };

  return (
    <SidebarContainer isOpen={isOpen}>
      <NavMenu>
        {navigationItems.map((item) => (
          <NavItem
            key={item.path}
            to={item.path}
            active={location.pathname === item.path}
            isMain={item.isMain}
            isOpen={isOpen}
          >
            <NavIcon isOpen={isOpen}>{item.icon}</NavIcon>
            {isOpen && <NavText>{item.label}</NavText>}
          </NavItem>
        ))}
      </NavMenu>

      <UserSection>
        {isOpen ? (
          <>
            {user && (
              <UserInfo>
                <UserAvatar>
                  {(user.first_name || user.firstName || '').charAt(0)}{(user.last_name || user.lastName || '').charAt(0)}
                </UserAvatar>
                <UserDetails>
                  <UserName>{user.first_name || user.firstName || ''} {user.last_name || user.lastName || ''}</UserName>
                  <UserRole>{user.roles[0]?.name || 'User'}</UserRole>
                </UserDetails>
              </UserInfo>
            )}
            <LogoutButton onClick={handleLogout}>
              <span>🚪</span>
              Logout
            </LogoutButton>
          </>
        ) : (
          <LogoutButton onClick={handleLogout} style={{ padding: '12px', justifyContent: 'center' }}>
            <span>🚪</span>
          </LogoutButton>
        )}
      </UserSection>
    </SidebarContainer>
  );
};

export default Sidebar; 