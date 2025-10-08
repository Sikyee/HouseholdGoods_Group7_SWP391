import React, { useEffect, useState } from 'react';

function AppointmentManager() {
  const [appointments, setAppointments] = useState([]);
  const [services, setServices] = useState([]);
  const [form, setForm] = useState({
    id: '',
    customerName: '',
    customerEmail: '',
    appointmentTime: '',
    serviceId: '',
    status: 'PENDING'
  });

  const [newService, setNewService] = useState({
    name: '',
    description: '',
    duration: '',
    price: ''
  });

  const fetchAppointments = () => {
    fetch('http://localhost:8081/api/appointments')
      .then(res => res.json())
      .then(data => setAppointments(data));
  };

  const fetchServices = () => {
    fetch('http://localhost:8081/api/services')
      .then(res => res.json())
      .then(data => setServices(data));
  };

  useEffect(() => {
    fetchAppointments();
    fetchServices();
  }, []);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleServiceChange = e => {
    const { name, value } = e.target;
    setNewService({ ...newService, [name]: value });
  };

  const handleSubmit = e => {
    e.preventDefault();
    const method = form.id ? 'PUT' : 'POST';
    const url = form.id
      ? `http://localhost:8081/api/appointments/${form.id}`
      : 'http://localhost:8081/api/appointments';

    const payload = {
      id: form.id,
      customerName: form.customerName,
      customerEmail: form.customerEmail,
      appointmentTime: form.appointmentTime,
      status: form.status,
      serviceId: form.serviceId
    };

    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(() => {
        fetchAppointments();
        setForm({
          id: '',
          customerName: '',
          customerEmail: '',
          appointmentTime: '',
          serviceId: '',
          status: 'PENDING'
        });
      });
  };

  const handleAddService = e => {
    e.preventDefault();
    fetch('http://localhost:8081/api/services', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newService)
    })
      .then(() => {
        fetchServices();
        setNewService({ name: '', description: '', duration: '', price: '' });
      });
  };

  const handleEdit = a => {
    setForm({
      id: a.id,
      customerName: a.customerName,
      customerEmail: a.customerEmail,
      appointmentTime: a.appointmentTime,
      serviceId: a.service.id,
      status: a.status
    });
  };

  const handleDelete = id => {
    fetch(`http://localhost:8081/api/appointments/${id}`, { method: 'DELETE' })
      .then(() => fetchAppointments());
  };

  return (
    <div>
      <h2>📋 Manage Appointments</h2>
      <form onSubmit={handleSubmit}>
        <input name="customerName" placeholder="Name" value={form.customerName} onChange={handleChange} required />
        <input name="customerEmail" placeholder="Email" value={form.customerEmail} onChange={handleChange} required />
        <input name="appointmentTime" type="datetime-local" value={form.appointmentTime} onChange={handleChange} required />

        <select name="serviceId" value={form.serviceId} onChange={handleChange} required>
          <option value="">-- Select Service --</option>
          {services.map(s => (
            <option key={s.id} value={s.id}>
              {s.name} ({s.duration} mins, ${s.price})
            </option>
          ))}
        </select>

        <select name="status" value={form.status} onChange={handleChange}>
          <option value="PENDING">Pending</option>
          <option value="CONFIRMED">Confirmed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>

        <button type="submit">{form.id ? 'Update' : 'Create'}</button>
      </form>

      <h3 style={{ marginTop: '2rem' }}>➕ Add New Service</h3>
      <form onSubmit={handleAddService}>
        <input name="name" placeholder="Service Name" value={newService.name} onChange={handleServiceChange} required />
        <input name="description" placeholder="Description" value={newService.description} onChange={handleServiceChange} required />
        <input name="duration" type="number" placeholder="Duration (mins)" value={newService.duration} onChange={handleServiceChange} required />
        <input name="price" type="number" placeholder="Price ($)" value={newService.price} onChange={handleServiceChange} required />
        <button type="submit">Add Service</button>
      </form>

      <ul style={{ marginTop: '2rem' }}>
        {appointments.map(a => (
          <li key={a.id}>
            {a.customerName} – {a.appointmentTime} – {a.status} – Service: {a.service.name} (ID: {a.service.id})
            <button onClick={() => handleEdit(a)}>Edit</button>
            <button onClick={() => handleDelete(a.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default AppointmentManager;
