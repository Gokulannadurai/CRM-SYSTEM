# CRM Frontend Application

A modern, responsive React.js frontend for the CRM system, built with TypeScript, Redux Toolkit, and Styled Components.

## 🚀 Features

### ✅ Core Features (Completed)
- **Modern UI/UX**: Clean, professional interface inspired by modern CRM systems
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Authentication**: Secure login/logout with JWT token management
- **Dashboard**: Comprehensive overview with statistics and quick actions
- **Customer Management**: Full CRUD operations for customer profiles
- **Lead Management**: Lead tracking and pipeline management
- **Task Management**: Task creation, assignment, and tracking
- **User Management**: Complete user administration with role-based access
- **File Management**: File upload, preview, and attachment system
- **Real-time Notifications**: Toast notifications for user feedback
- **Search & Filtering**: Advanced search capabilities across all entities
- **Role-based Access**: Different permissions for different user roles
- **Pagination**: Server-side pagination for all list views
- **Form Validation**: Comprehensive client-side and server-side validation

### 🎯 Key Capabilities
- **Dashboard Analytics**: Sales pipeline visualization and key metrics
- **Quick Actions**: Rapid navigation to create and view pages
- **File Attachments**: Support for multiple file types with preview
- **Advanced Search**: Search across multiple fields with filters
- **Responsive Design**: Mobile-first approach with touch-friendly interface
- **Error Handling**: Comprehensive error handling and user feedback
- **Loading States**: Smooth loading experiences with skeleton screens

## 🛠️ Technology Stack

- **React 18**: Latest React with hooks and modern patterns
- **TypeScript**: Type-safe development with comprehensive type definitions
- **Redux Toolkit**: State management with RTK Query for API calls
- **React Router v6**: Client-side routing with protected routes
- **Styled Components**: CSS-in-JS styling with theme support
- **Axios**: HTTP client for API communication with interceptors
- **React Hook Form**: Form handling and validation
- **Vite**: Fast build tool and development server

## 📁 Project Structure

```
src/
├── components/
│   ├── common/           # Reusable components
│   │   ├── LoadingSpinner.tsx
│   │   ├── NotificationContainer.tsx
│   │   ├── ConfirmationDialog.tsx
│   │   ├── AttachmentList.tsx
│   │   ├── Pagination.tsx
│   │   └── FileUploadField.tsx
│   └── layout/           # Layout components
│       ├── Layout.tsx
│       ├── Sidebar.tsx
│       └── Header.tsx
├── pages/                # Page components
│   ├── auth/            # Authentication pages
│   │   ├── LoginPage.tsx
│   │   └── ForgotPasswordPage.tsx
│   ├── dashboard/       # Dashboard page
│   │   └── DashboardPage.tsx
│   ├── customers/       # Customer management pages
│   │   ├── CustomerListPage.tsx
│   │   ├── CustomerDetailPage.tsx
│   │   └── CustomerFormPage.tsx
│   ├── users/          # User management pages
│   │   ├── UserListPage.tsx
│   │   ├── UserDetailPage.tsx
│   │   └── UserFormPage.tsx
│   └── sales/          # Sales management pages
│       ├── LeadListPage.tsx
│       ├── LeadDetailPage.tsx
│       ├── LeadFormPage.tsx
│       ├── TaskListPage.tsx
│       ├── TaskDetailPage.tsx
│       └── TaskFormPage.tsx
├── store/               # Redux store
│   ├── store.ts
│   └── slices/         # Redux slices
│       ├── authSlice.ts
│       ├── customerSlice.ts
│       ├── salesSlice.ts
│       ├── userSlice.ts
│       └── uiSlice.ts
├── services/            # API services
│   ├── api.ts
│   ├── authApi.ts
│   ├── customerApi.ts
│   ├── salesApi.ts
│   └── userApi.ts
├── types/               # TypeScript type definitions
│   └── index.ts
├── utils/               # Utility functions
│   ├── fileUtils.ts
│   ├── validation.ts
│   └── formatters.ts
├── hooks/               # Custom React hooks
│   ├── useDropdownData.ts
│   └── useAuth.ts
└── styles/              # Global styles
    └── GlobalStyles.ts
```

## 🚀 Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- Backend API running (see backend documentation)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd crm-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Environment Configuration**
   Create a `.env` file in the root directory:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   VITE_ENVIRONMENT=development
   ```

4. **Start the development server**
   ```bash
   npm run dev
   # or
   yarn dev
   ```

5. **Build for production**
   ```bash
   npm run build
   # or
   yarn build
   ```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Backend API base URL | `http://localhost:8080` |
| `VITE_ENVIRONMENT` | Environment (development/production) | `development` |

### API Configuration

The application is configured to communicate with the backend API. Ensure the backend services are running:

- **User Service**: Port 8081
- **Customer Service**: Port 8082
- **Sales Service**: Port 8083

## 📱 Features Overview

### Dashboard
- **Statistics Cards**: Key metrics and performance indicators
- **Quick Actions**: Rapid navigation to create and view pages
- **Recent Activity**: Latest updates and notifications
- **Sales Pipeline**: Visual representation of sales stages

### Customer Management
- **Customer List**: Paginated list with search and filtering
- **Customer Details**: Comprehensive customer profiles
- **Customer Forms**: Create and edit customer information
- **File Attachments**: Document management for customers

### Lead Management
- **Lead List**: Track all leads with status management
- **Lead Details**: Complete lead information and history
- **Lead Forms**: Create and update lead information
- **Pipeline Management**: Move leads through sales stages

### Task Management
- **Task List**: View and manage all tasks
- **Task Details**: Complete task information
- **Task Forms**: Create and edit tasks
- **Assignment**: Assign tasks to team members

### User Management
- **User List**: Administer system users
- **User Details**: View user profiles and permissions
- **User Forms**: Create and edit user accounts
- **Role Management**: Assign roles and permissions

## 🎨 UI/UX Features

### Design System
- **Consistent Styling**: Unified design language across all components
- **Color Scheme**: Professional color palette with accessibility support
- **Typography**: Clear, readable fonts with proper hierarchy
- **Spacing**: Consistent spacing and layout patterns

### Responsive Design
- **Mobile-First**: Optimized for mobile devices
- **Tablet Support**: Enhanced experience for tablet users
- **Desktop Optimization**: Full-featured desktop interface
- **Touch-Friendly**: Optimized for touch interactions

### Accessibility
- **Keyboard Navigation**: Full keyboard accessibility
- **Screen Reader Support**: ARIA labels and semantic HTML
- **Color Contrast**: WCAG compliant color combinations
- **Focus Management**: Proper focus indicators and management

## 🔒 Security Features

### Authentication
- **JWT Tokens**: Secure token-based authentication
- **Token Refresh**: Automatic token refresh mechanism
- **Session Management**: Secure session handling
- **Logout**: Secure logout with token invalidation

### Authorization
- **Role-Based Access**: Different permissions for different roles
- **Route Protection**: Protected routes based on user permissions
- **Component-Level Security**: Conditional rendering based on permissions
- **API Security**: Secure API communication with authentication headers

## 📊 Performance Optimizations

### Frontend Performance
- **Code Splitting**: Lazy loading of components and routes
- **Bundle Optimization**: Optimized bundle size and loading
- **Caching**: Intelligent caching strategies
- **Image Optimization**: Optimized image loading and display

### API Performance
- **Pagination**: Server-side pagination for large datasets
- **Caching**: API response caching where appropriate
- **Optimistic Updates**: Immediate UI updates with background sync
- **Error Handling**: Graceful error handling and recovery

## 🧪 Testing

### Testing Strategy
- **Unit Tests**: Component and utility function testing
- **Integration Tests**: Page and feature testing
- **E2E Tests**: End-to-end user workflow testing
- **Accessibility Tests**: Automated accessibility testing

### Running Tests
```bash
# Run all tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage
```

## 🚀 Deployment

### Production Build
```bash
# Create production build
npm run build

# Preview production build
npm run preview
```

### Deployment Options
- **Vercel**: Easy deployment with Vercel
- **Netlify**: Simple deployment with Netlify
- **AWS S3**: Static hosting on AWS S3
- **Docker**: Containerized deployment

## 📚 API Integration

### API Services
The frontend integrates with multiple backend services:

- **Authentication API**: User authentication and authorization
- **Customer API**: Customer management operations
- **Sales API**: Lead and task management
- **User API**: User administration

### API Communication
- **Axios Interceptors**: Automatic token handling and error management
- **Request/Response Transformation**: Data transformation for API compatibility
- **Error Handling**: Comprehensive error handling and user feedback
- **Loading States**: Loading indicators for API operations

## 🔧 Development

### Development Workflow
1. **Feature Development**: Create feature branches from main
2. **Code Review**: Submit pull requests for review
3. **Testing**: Ensure all tests pass
4. **Documentation**: Update documentation as needed
5. **Deployment**: Deploy to staging/production

### Code Standards
- **TypeScript**: Strict TypeScript configuration
- **ESLint**: Code linting and style enforcement
- **Prettier**: Code formatting
- **Git Hooks**: Pre-commit hooks for code quality

## 📈 Monitoring and Analytics

### Performance Monitoring
- **Bundle Analysis**: Analyze bundle size and composition
- **Performance Metrics**: Track key performance indicators
- **Error Tracking**: Monitor and track application errors
- **User Analytics**: Track user behavior and engagement

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Submit a pull request

### Code Guidelines
- Follow TypeScript best practices
- Write comprehensive tests
- Update documentation
- Follow the established code style

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Check the documentation
- Review the API documentation
- Contact the development team
- Submit issues on GitHub

---

**Status**: ✅ Production Ready
**Version**: 2.0.0
**Last Updated**: December 2024 