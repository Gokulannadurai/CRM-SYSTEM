import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { testApiConnectivity, testAuthentication, getServiceUrls, validateApiConfig } from '../../utils/apiTest';

const StatusPanel = styled.div`
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
`;

const ServiceStatus = styled.div<{ status: string }>`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  margin: 8px 0;
  border-radius: 6px;
  background: ${props => {
    switch (props.status) {
      case 'online': return '#d4edda';
      case 'offline': return '#f8d7da';
      default: return '#fff3cd';
    }
  }};
  border: 1px solid ${props => {
    switch (props.status) {
      case 'online': return '#c3e6cb';
      case 'offline': return '#f5c6cb';
      default: return '#ffeaa7';
    }
  }};
`;

const StatusIndicator = styled.div<{ status: string }>`
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: ${props => {
    switch (props.status) {
      case 'online': return '#28a745';
      case 'offline': return '#dc3545';
      default: return '#ffc107';
    }
  }};
  margin-right: 8px;
`;

const Button = styled.button`
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin: 4px;
  
  &:hover {
    background: #0056b3;
  }
  
  &:disabled {
    background: #6c757d;
    cursor: not-allowed;
  }
`;

const TestSection = styled.div`
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #dee2e6;
`;

const Input = styled.input`
  padding: 8px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  margin: 4px;
  width: 200px;
`;

const ApiStatusPanel: React.FC = () => {
  const [connectivityResult, setConnectivityResult] = useState<any>(null);
  const [isTesting, setIsTesting] = useState(false);
  const [testCredentials, setTestCredentials] = useState({ username: 'admin', password: 'admin123' });
  const [authResult, setAuthResult] = useState<any>(null);
  const [serviceUrls] = useState(getServiceUrls());

  const runConnectivityTest = async () => {
    setIsTesting(true);
    try {
      const result = await testApiConnectivity();
      setConnectivityResult(result);
    } catch (error) {
      console.error('Connectivity test failed:', error);
    } finally {
      setIsTesting(false);
    }
  };

  const runAuthTest = async () => {
    setIsTesting(true);
    try {
      const result = await testAuthentication(testCredentials.username, testCredentials.password);
      setAuthResult(result);
    } catch (error) {
      console.error('Authentication test failed:', error);
    } finally {
      setIsTesting(false);
    }
  };

  const configValidation = validateApiConfig();

  useEffect(() => {
    runConnectivityTest();
  }, []);

  return (
    <StatusPanel>
      <h3>API Service Status</h3>
      
      {/* Configuration Validation */}
      <div>
        <h4>Configuration</h4>
        {configValidation.isValid ? (
          <div style={{ color: '#28a745' }}>✓ Configuration is valid</div>
        ) : (
          <div>
            <div style={{ color: '#dc3545' }}>✗ Configuration issues found:</div>
            <ul>
              {configValidation.issues.map((issue, index) => (
                <li key={index} style={{ color: '#dc3545' }}>{issue}</li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* Service URLs */}
      <div>
        <h4>Service URLs</h4>
        <div>User Service: {serviceUrls.userService}</div>
        <div>Customer Service: {serviceUrls.customerService}</div>
        <div>Sales Service: {serviceUrls.salesService}</div>
      </div>

      {/* Connectivity Status */}
      <div>
        <h4>Connectivity Status</h4>
        <Button onClick={runConnectivityTest} disabled={isTesting}>
          {isTesting ? 'Testing...' : 'Test Connectivity'}
        </Button>
        
        {connectivityResult && (
          <div>
            <div style={{ marginTop: '10px' }}>
              Overall Status: 
              <span style={{ 
                color: connectivityResult.overallStatus === 'all_online' ? '#28a745' : 
                       connectivityResult.overallStatus === 'partial_offline' ? '#ffc107' : '#dc3545',
                fontWeight: 'bold',
                marginLeft: '8px'
              }}>
                {connectivityResult.overallStatus.replace('_', ' ').toUpperCase()}
              </span>
            </div>
            
            {connectivityResult.services.map((service: any, index: number) => (
              <ServiceStatus key={index} status={service.status}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <StatusIndicator status={service.status} />
                  <div>
                    <strong>{service.service}</strong>
                    <div style={{ fontSize: '12px', color: '#666' }}>{service.url}</div>
                  </div>
                </div>
                <div>
                  {service.status === 'online' && service.responseTime && (
                    <span style={{ color: '#28a745' }}>{service.responseTime}ms</span>
                  )}
                  {service.status === 'offline' && service.error && (
                    <span style={{ color: '#dc3545', fontSize: '12px' }}>{service.error}</span>
                  )}
                </div>
              </ServiceStatus>
            ))}
          </div>
        )}
      </div>

      {/* Authentication Test */}
      <TestSection>
        <h4>Authentication Test</h4>
        <div>
          <Input
            type="text"
            placeholder="Username"
            value={testCredentials.username}
            onChange={(e) => setTestCredentials(prev => ({ ...prev, username: e.target.value }))}
          />
          <Input
            type="password"
            placeholder="Password"
            value={testCredentials.password}
            onChange={(e) => setTestCredentials(prev => ({ ...prev, password: e.target.value }))}
          />
          <Button onClick={runAuthTest} disabled={isTesting}>
            {isTesting ? 'Testing...' : 'Test Authentication'}
          </Button>
        </div>
        
        {authResult && (
          <div style={{ marginTop: '10px' }}>
            {authResult.success ? (
              <div style={{ color: '#28a745' }}>
                ✓ Authentication successful
                <div style={{ fontSize: '12px', marginTop: '5px' }}>
                  Token: {authResult.data?.data?.token?.substring(0, 20)}...
                </div>
              </div>
            ) : (
              <div style={{ color: '#dc3545' }}>
                ✗ Authentication failed: {authResult.error}
                {authResult.status && <div>Status: {authResult.status}</div>}
              </div>
            )}
          </div>
        )}
      </TestSection>

      {/* Last Updated */}
      {connectivityResult && (
        <div style={{ marginTop: '20px', fontSize: '12px', color: '#666' }}>
          Last updated: {new Date(connectivityResult.timestamp).toLocaleString()}
        </div>
      )}
    </StatusPanel>
  );
};

export default ApiStatusPanel; 