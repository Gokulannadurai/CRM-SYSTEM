import React, { useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchCustomerStats } from '../../store/slices/customerSlice';
import { fetchLeadStats, fetchTaskStats } from '../../store/slices/salesSlice';

const DashboardContainer = styled.div`
  padding: 20px;
`;

const DashboardHeader = styled.div`
  margin-bottom: 30px;
`;

const Title = styled.h1`
  color: #333;
  font-size: 2rem;
  margin-bottom: 10px;
`;

const Subtitle = styled.p`
  color: #666;
  font-size: 1rem;
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
`;

const StatCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #667eea;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }
`;

const StatHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
`;

const StatTitle = styled.h3`
  color: #666;
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
`;

const StatIcon = styled.div`
  font-size: 1.5rem;
  opacity: 0.7;
`;

const StatValue = styled.div`
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
`;

const StatChange = styled.div<{ positive: boolean }>`
  font-size: 0.9rem;
  color: ${props => props.positive ? '#4caf50' : '#f44336'};
  display: flex;
  align-items: center;
  gap: 4px;
`;

const ContentGrid = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 30px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const ChartCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const ChartTitle = styled.h3`
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 20px;
`;

const PlaceholderChart = styled.div`
  height: 300px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 1.1rem;
`;

const RecentActivityCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const ActivityTitle = styled.h3`
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 20px;
`;

const ActivityList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

const ActivityItem = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f8f9fa;
  transition: background-color 0.2s;

  &:hover {
    background: #e9ecef;
  }
`;

const ActivityIcon = styled.div`
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
`;

const ActivityContent = styled.div`
  flex: 1;
`;

const ActivityText = styled.div`
  color: #333;
  font-size: 0.9rem;
  margin-bottom: 4px;
`;

const ActivityTime = styled.div`
  color: #666;
  font-size: 0.8rem;
`;

const QuickActionsCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const QuickActionsTitle = styled.h3`
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 20px;
`;

const ActionButtons = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const ActionButton = styled.button`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 16px 20px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s;
  }

  &:hover::before {
    left: 100%;
  }
`;

const DashboardPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { stats: customerStats } = useSelector((state: RootState) => state.customer);
  const { leadStats, taskStats } = useSelector((state: RootState) => state.sales);
  const { user } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    dispatch(fetchCustomerStats());
    dispatch(fetchLeadStats());
    dispatch(fetchTaskStats());
  }, [dispatch]);

  // Navigation functions for quick actions
  const handleCreateCustomer = () => {
    navigate('/customers/new');
  };

  const handleCreateLead = () => {
    navigate('/leads/new');
  };

  const handleCreateTask = () => {
    navigate('/tasks/new');
  };

  const handleViewReports = () => {
    navigate('/reports');
  };

  const handleCreateUser = () => {
    navigate('/users/new');
  };

  const handleViewAllCustomers = () => {
    navigate('/customers');
  };

  const handleViewAllLeads = () => {
    navigate('/leads');
  };

  const handleViewAllTasks = () => {
    navigate('/tasks');
  };

  const handleViewAllUsers = () => {
    navigate('/users');
  };

  const recentActivities = [
    {
      id: 1,
      type: 'customer',
      text: 'New customer "ABC Corp" added',
      time: '2 hours ago',
      icon: '👥'
    },
    {
      id: 2,
      type: 'lead',
      text: 'Lead "John Smith" moved to Qualified stage',
      time: '4 hours ago',
      icon: '🎯'
    },
    {
      id: 3,
      type: 'task',
      text: 'Task "Follow up call" completed',
      time: '6 hours ago',
      icon: '✅'
    },
    {
      id: 4,
      type: 'lead',
      text: 'New lead "Tech Solutions Inc" created',
      time: '1 day ago',
      icon: '🎯'
    }
  ];

  return (
    <DashboardContainer>
      <DashboardHeader>
        <Title>Welcome back, {user?.firstName}!</Title>
        <Subtitle>Here's what's happening with your CRM today.</Subtitle>
      </DashboardHeader>

      <StatsGrid>
        <StatCard>
          <StatHeader>
            <StatTitle>Total Customers</StatTitle>
            <StatIcon>👥</StatIcon>
          </StatHeader>
          <StatValue>{customerStats?.totalCustomers || 0}</StatValue>
          <StatChange positive={true}>
            ↗ +12% from last month
          </StatChange>
        </StatCard>

        <StatCard>
          <StatHeader>
            <StatTitle>Active Leads</StatTitle>
            <StatIcon>🎯</StatIcon>
          </StatHeader>
          <StatValue>{leadStats?.totalLeads || 0}</StatValue>
          <StatChange positive={true}>
            ↗ +8% from last week
          </StatChange>
        </StatCard>

        <StatCard>
          <StatHeader>
            <StatTitle>Pipeline Value</StatTitle>
            <StatIcon>💰</StatIcon>
          </StatHeader>
          <StatValue>${leadStats?.pipelineValue?.toLocaleString() || 0}</StatValue>
          <StatChange positive={true}>
            ↗ +15% from last month
          </StatChange>
        </StatCard>

        <StatCard>
          <StatHeader>
            <StatTitle>Pending Tasks</StatTitle>
            <StatIcon>✅</StatIcon>
          </StatHeader>
          <StatValue>{taskStats ? taskStats.totalTasks - taskStats.completedTasks : 0}</StatValue>
          <StatChange positive={false}>
            ↘ -5% from yesterday
          </StatChange>
        </StatCard>
      </StatsGrid>

      <ContentGrid>
        <ChartCard>
          <ChartTitle>Sales Pipeline Overview</ChartTitle>
          <PlaceholderChart>
            📊 Sales Pipeline Chart Coming Soon
          </PlaceholderChart>
        </ChartCard>

        <RecentActivityCard>
          <ActivityTitle>Recent Activity</ActivityTitle>
          <ActivityList>
            {recentActivities.map((activity) => (
              <ActivityItem key={activity.id}>
                <ActivityIcon>{activity.icon}</ActivityIcon>
                <ActivityContent>
                  <ActivityText>{activity.text}</ActivityText>
                  <ActivityTime>{activity.time}</ActivityTime>
                </ActivityContent>
              </ActivityItem>
            ))}
          </ActivityList>
        </RecentActivityCard>
      </ContentGrid>

      <QuickActionsCard>
        <QuickActionsTitle>Quick Actions</QuickActionsTitle>
        <ActionButtons>
          <ActionButton onClick={handleCreateCustomer}>
            ➕ Add New Customer
          </ActionButton>
          <ActionButton onClick={handleCreateLead}>
            🎯 Create New Lead
          </ActionButton>
          <ActionButton onClick={handleCreateTask}>
            ✅ Create New Task
          </ActionButton>
          <ActionButton onClick={handleCreateUser}>
            👤 Add New User
          </ActionButton>
          <ActionButton onClick={handleViewReports}>
            📊 View Reports
          </ActionButton>
        </ActionButtons>
        
        <QuickActionsTitle style={{ marginTop: '24px', marginBottom: '16px' }}>View All</QuickActionsTitle>
        <ActionButtons>
          <ActionButton onClick={handleViewAllCustomers} style={{ background: 'linear-gradient(135deg, #4CAF50 0%, #45a049 100%)' }}>
            👥 View All Customers
          </ActionButton>
          <ActionButton onClick={handleViewAllLeads} style={{ background: 'linear-gradient(135deg, #FF9800 0%, #F57C00 100%)' }}>
            🎯 View All Leads
          </ActionButton>
          <ActionButton onClick={handleViewAllTasks} style={{ background: 'linear-gradient(135deg, #2196F3 0%, #1976D2 100%)' }}>
            ✅ View All Tasks
          </ActionButton>
          <ActionButton onClick={handleViewAllUsers} style={{ background: 'linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%)' }}>
            👤 View All Users
          </ActionButton>
        </ActionButtons>
      </QuickActionsCard>
    </DashboardContainer>
  );
};

export default DashboardPage; 