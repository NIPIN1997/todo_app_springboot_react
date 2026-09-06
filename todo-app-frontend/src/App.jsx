import "./App.css";
import { AppRoutes } from "../src/routes/AppRoutes.jsx";
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify";
import "bootstrap-icons/font/bootstrap-icons.css";

function App() {
  return (
    <>
      <AppRoutes />
      <ToastContainer />
    </>
  );
}

export default App;
