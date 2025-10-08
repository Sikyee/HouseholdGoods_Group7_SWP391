import React, { useState } from 'react';

function AppointmentForm() {
  const [form, setForm] = useState({
    customerName: '',
    customerEmail: '',
    appointmentTime: '',
    service: { id: '' }
  });

  const handleChange = e => {
    const { name, value } = e.target;
    if (name === 'serviceId') {
      setForm({ ...form, service: { id: value } });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  const handleSubmit = e => {
    e.preventDefault();
    fetch('http://localhost:8081/api/appointments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to create appointment');
        return res.json();
      })
      .then(data => {
        alert('Appointment created successfully!');
        setForm({
          customerName: '',
          customerEmail: '',
          appointmentTime: '',
          service: { id: '' }
        });
      })
      .catch(err => console.error('Error creating appointment:', err));
  };

  return (
    <div>

    </div>
  );
}

export default AppointmentForm;
