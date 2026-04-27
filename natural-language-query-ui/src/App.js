import React, { useState } from 'react';
import './App.css';

function App() {
  const [query, setQuery] = useState('');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!query.trim()) return;
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/orders?query=${encodeURIComponent(query)}`);
      const result = await response.json();
      setData(result);
    } catch (error) {
      console.error(error);
      setData([]);
    }
    setLoading(false);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="App">
      <h1>Natural Language Query UI</h1>
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyPress={handleKeyPress}
        placeholder="Enter your query"
      />
      <button onClick={handleSearch}>Search</button>
      {loading && <p>Loading...</p>}
      {data.length > 0 && (
        <table border="1">
          <thead>
            <tr>
              <th>ID</th>
              <th>Order ID</th>
              <th>Customer</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Delivery Address</th>
              <th>Delivery Date</th>
              <th>Order Placement Date</th>
              <th>Order Items</th>
              <th>Order Status</th>
            </tr>
          </thead>
          <tbody>
            {data.map((order) => (
              <tr key={order.id}>
                <td>{order.id}</td>
                <td>{order.orderId}</td>
                <td>{order.customer.name}</td>
                <td>{order.contactDetails.email}</td>
                <td>{order.contactDetails.phone}</td>
                <td>{order.deliveryAddress.city}, {order.deliveryAddress.country} ({order.deliveryAddress.postCode})</td>
                <td>{order.deliveryDateTime}</td>
                <td>{order.orderPlacementDateTime}</td>
                <td>
                  <ul>
                    {order.orderItems.map((item) => (
                      <li key={item.lineNumber}>
                        {item.productName} - Qty: {item.quantity}
                      </li>
                    ))}
                  </ul>
                </td>
                <td>{order.orderStatus}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default App;
