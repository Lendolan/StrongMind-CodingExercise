import { useNavigate } from "react-router-dom";

function App() {
  // Hook to programmatically navigate between routes
  const navigate = useNavigate();
  return (
    <div className="app">
      <button className="btn" onClick={() => navigate("/store-owner")}>
        Store Owner Login
      </button>
      <button className="btn" onClick={() => navigate("/pizza-chef")}>
        Pizza Chef Login
      </button>
    </div>
  );
}

export default App;
