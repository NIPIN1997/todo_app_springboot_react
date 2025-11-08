import Container from "react-bootstrap/Container";
import { Header } from "../components/Header";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import styles from "../styles/login.module.css";
import { useState } from "react";
import { useAuth } from "../hooks/UseAuth.jsx";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

export function Login() {
  const [data, setData] = useState({});
  const { login } = useAuth();
  const navigate = useNavigate();
  const handleChange = (e) => {
    setData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { success, message } = await login(data);
    if (success) {
      navigate("/home");
    } else {
      toast.error(message);
    }
  };

  return (
    <Container fluid>
      <Header />
      <div className={styles.form_division}>
        <h1 className={styles.heading}>Login</h1>
        <Form onSubmit={handleSubmit}>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="3" htmlFor="email">
              Email
            </Form.Label>
            <Col sm="9">
              <Form.Control
                type="email"
                placeholder="Email"
                id="email"
                name="email"
                required
                onChange={handleChange}
              />
            </Col>
          </Form.Group>
          <Form.Group as={Row}>
            <Form.Label column sm="3" htmlFor="password">
              Password
            </Form.Label>
            <Col sm="9">
              <Form.Control
                type="password"
                placeholder="Password"
                id="password"
                name="password"
                required
                onChange={handleChange}
              />
            </Col>
          </Form.Group>
          <div className={styles.submit_button_division}>
            <button className={styles.submit_button} type="submit">
              login
            </button>
          </div>
        </Form>
      </div>
    </Container>
  );
}
