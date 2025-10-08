import React, { useEffect, useState } from 'react';

function ServiceManager() {
  const [services, setServices] = useState([]);
  const [form, setForm] = useState({
    id: '',
    name: '',
    description: '',
    duration: '',
    price: ''
  });

  const fetchServices = () => {
    fetch('http://localhost:8081/api/services')
      .then(res => res.json())
      .then(data => setServices(data));
  };

  useEffect(() => {
    fetchServices();
  }, []);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = e => {
    e.preventDefault();
    const method = form.id ? 'PUT' : 'POST';
    const url = form.id
      ? `http://localhost:8081/api/services/${form.id}`
      : 'http://localhost:8081/api/services';

    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    })
      .then(() => {
        fetchServices();
        setForm({ id: '', name: '', description: '', duration: '', price: '' });
      });
  };

  const handleEdit = service => setForm(service);
  const handleDelete = id => {
    fetch(`http://localhost:8081/api/services/${id}`, { method: 'DELETE' })
      .then(() => fetchServices());
  };

  return (
    <div>
      <h2>🛠️ Manage Services</h2>
      <form onSubmit={handleSubmit}>
        <input name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
        <input name="description" placeholder="Description" value={form.description} onChange={handleChange} required />
        <input name="duration" type="number" placeholder="Duration" value={form.duration} onChange={handleChange} required />
        <input name="price" type="number" placeholder="Price" value={form.price} onChange={handleChange} required />
        <button type="submit">{form.id ? 'Update' : 'Create'}</button>
      </form>

      <ul>
        {services.map(service => (
          <li key={service.id}>
            <strong>{service.name}</strong> – {service.description} ({service.duration} mins, ${service.price})
            <button onClick={() => handleEdit(service)}>Edit</button>
            <button onClick={() => handleDelete(service.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ServiceManager;
