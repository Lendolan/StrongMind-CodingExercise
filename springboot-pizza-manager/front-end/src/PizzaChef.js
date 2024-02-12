import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Multiselect } from "multiselect-react-dropdown";

function PizzaChef() {
  const [pizzas, setPizzas] = useState([]);
  const [toppings, setToppings] = useState([]);
  const [newPizzaName, setNewPizzaName] = useState("");
  const [selectedToppings, setSelectedToppings] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const toppingsOptions = toppings.map((topping) => ({
    name: topping.name,
    id: topping.id,
  }));
  const [editingPizzaId, setEditingPizzaId] = useState(null);
  const [tempPizzaName, setTempPizzaName] = useState("");
  const [tempSelectedToppings, setTempSelectedToppings] = useState([]);

  // Fetch existing pizzas and toppings from the server
  const fetchData = async () => {
    try {
      const [pizzasRes, toppingsRes] = await Promise.all([
        axios.get("/api/pizzas"),
        axios.get("/api/toppings"),
      ]);
      setPizzas(pizzasRes.data);
      setToppings(toppingsRes.data);
    } catch (error) {
      console.error("There was an error fetching data:", error);
      setError("Could not fetch data.");
    }
  };

  // Fetch data on component mount
  useEffect(() => {
    fetchData();
  }, []);

  // Handlers for selecting and removing toppings from the dropdown
  const onSelect = (selectedList, selectedItem) => {
    setSelectedToppings(selectedList);
  };

  const onRemove = (selectedList, removedItem) => {
    setSelectedToppings(selectedList);
  };

  // Initiates edit mode for a pizza, setting up temporary states
  const startEditing = (pizza) => {
    setEditingPizzaId(pizza.id);
    setTempPizzaName(pizza.name);
    // Convert pizza toppings to the format expected by Multiselect
    const tempToppings = pizza.toppings.map((topping) => ({
      id: topping.id,
      name: topping.name,
    }));
    setTempSelectedToppings(tempToppings);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Check if the pizza name is provided
    if (!newPizzaName.trim()) {
      setError("Please provide a name for the pizza.");
      return; // Stop the function execution if no name is provided
    }

    if (!selectedToppings.length) {
      setError("Please select at least one topping for the pizza.");
      return; // Prevent submission without toppings
    }

    try {
      // Map selectedToppings to their ids
      const toppingIds = selectedToppings.map((topping) => topping.id);

      // Adjusting payload to match the new DTO structure expected by the backend
      const payload = {
        name: newPizzaName,
        toppingIds: toppingIds, // Send only the array of selected topping IDs
      };

      await axios.post("/api/pizzas", payload);

      // Resetting the form and re-fetching pizzas to reflect the new addition
      setNewPizzaName("");
      setSelectedToppings([]);
      setError("");
      await fetchData();
    } catch (error) {
      console.error("There was an error adding the pizza:", error);
      setError(
        `Could not add pizza. ${
          error.response ? error.response.data : "An unexpected error occurred."
        }`
      );
    }
  };

  // Deletes a pizza and refreshes the list
  const deletePizza = async (pizzaId) => {
    try {
      await axios.delete(`/api/pizzas/${pizzaId}`);
      fetchData();
    } catch (error) {
      console.error("Failed to delete pizza", error);
      setError("Failed to delete pizza.");
    }
  };

  // Updates an existing pizza and refreshes the list
  const updatePizza = async (pizzaId, newName, newToppingIds) => {
    const payload = {
      name: newName,
      toppingIds: newToppingIds,
    };

    try {
      await axios.put(`/api/pizzas/${pizzaId}`, payload);
      setEditingPizzaId(null); // Reset editing state
      fetchData(); // Fetch updated list of pizzas
    } catch (error) {
      console.error("Failed to update pizza", error);
      setError("Failed to update pizza.");
    }
  };

  // Renders the list of pizzas, with edit and delete options
  const renderPizzas = () => {
    if (pizzas.length === 0) {
      return <div className="text">There are currently no pizzas!</div>;
    }

    return (
      <div className="pizzas-container">
        {pizzas.map((pizza) => (
          <div className="pizza-item" key={pizza.id}>
            {editingPizzaId === pizza.id ? (
              <div className="editing-container">
                <div className="input-button-container">
                  <input
                    value={tempPizzaName}
                    onChange={(e) => setTempPizzaName(e.target.value)}
                    placeholder="Pizza Name"
                  />
                  <button
                    onClick={() =>
                      updatePizza(
                        pizza.id,
                        tempPizzaName,
                        tempSelectedToppings.map((topping) => topping.id)
                      )
                    }
                    className="smlbtn" // Ensure the button styling matches your existing buttons
                  >
                    Update
                  </button>
                </div>
                <Multiselect
                  options={toppingsOptions}
                  selectedValues={tempSelectedToppings}
                  onSelect={(selectedList) =>
                    setTempSelectedToppings(selectedList)
                  }
                  onRemove={(selectedList) =>
                    setTempSelectedToppings(selectedList)
                  }
                  displayValue="name"
                  placeholder="Select Toppings"
                  style={{ width: "100%" }} // Adjust the width as needed
                />
              </div>
            ) : (
              <>
                <div className="pizza-name-container">
                  <button onClick={() => startEditing(pizza)} className="icon">
                    ✏️
                  </button>
                  <h3>{pizza.name}</h3>
                  <button
                    onClick={() => deletePizza(pizza.id)}
                    className="icon"
                  >
                    🗑️
                  </button>
                </div>
                <ul className="toppings-list">
                  {pizza.toppings.map((topping) => (
                    <li key={topping.id}>{topping.name}</li>
                  ))}
                </ul>
              </>
            )}
          </div>
        ))}
      </div>
    );
  };

  // Main render method for the PizzaChef component
  return (
    <div>
      <Header title="Pizza Manager" />
      <form className="form-container" onSubmit={handleSubmit}>
        <div className="input-button-container">
          {" "}
          <input
            type="text"
            value={newPizzaName}
            onChange={(e) => setNewPizzaName(e.target.value)}
            placeholder="Pizza Name"
          />
          <button type="submit" className="smlbtn">
            Add Pizza
          </button>
        </div>
        <Multiselect
          key={new Date().toISOString()}
          options={toppingsOptions}
          selectedValues={selectedToppings}
          onSelect={onSelect}
          onRemove={onRemove}
          displayValue="name"
          placeholder="Select Toppings"
        />
      </form>
      {error && <div className="error">{error}</div>}
      <div className="pizzas-container">{renderPizzas()}</div>
      <div
        style={{ display: "flex", justifyContent: "center", marginTop: "20px" }}
      >
        <button className="btn" onClick={() => navigate("/")}>
          Return to Home
        </button>
      </div>
    </div>
  );
}

function Header() {
  return (
    <header className="header">
      <h1>Pizza Manager</h1>
    </header>
  );
}

export default PizzaChef;
