import React, { useEffect, useState } from 'react';

function AvailableServices() {
  const [services, setServices] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8081/api/services')
      .then(res => res.json())
      .then(data => setServices(data))
      .catch(err => console.error('Error fetching services:', err));
  }, []);

  return (
    <div>
      <h2>🛎️ Available Services</h2>
      {services.length === 0 ? (
        <p>No services available.</p>
      ) : (
        <ul>
          {services.map(service => (
            <li key={service.id}>
              <strong>{service.name}</strong> – {service.description} ({service.duration} mins, ${service.price})
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default AvailableServices;
