import React from 'react';
import './App.css';
import AvailableServices from './components/AvailableServices';
import AppointmentForm from './components/AppointmentForm';
import ServiceManager from './components/ServiceManager';

function App() {
  return (
    <div className="App" style={{ padding: '2rem', maxWidth: '800px', margin: 'auto' }}>
      <h1>📅 Appointment Booking System</h1>

      <AvailableServices />
      <hr style={{ margin: '2rem 0' }} />

      <AppointmentForm />
      <hr style={{ margin: '2rem 0' }} />

      <ServiceManager />
    </div>
  );
}

export default App;
