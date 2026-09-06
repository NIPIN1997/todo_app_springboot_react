import Container from "react-bootstrap/Container";
import { Header } from "../components/Header";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import styles from "../styles/login.module.css";
import { useEffect, useState } from "react";
import { useAuth } from "../hooks/UseAuth.jsx";
import { Link, useNavigate } from "react-router-dom";

export function Login() {
  const [data, setData] = useState({});
  const { login, rememberMeLogin } = useAuth();
  const navigate = useNavigate();
  const rememberMeLoginCheck = async () => {
    if (
      localStorage.getItem("rememberMeToken") != null &&
      localStorage.getItem("deviceId") != null
    ) {
      const payload = {
        rememberMeToken: localStorage.getItem("rememberMeToken"),
        deviceId: localStorage.getItem("deviceId"),
      };
      const { success } = await rememberMeLogin(payload);
      if (success) {
        navigate("/home");
      }
    }
  };
  useEffect(() => {
    rememberMeLoginCheck();
  }, []);
  const handleChange = (e) => {
    setData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };
  const [rememberMe, setRememberMe] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...data,
      rememberMe: rememberMe,
    };
    const { success, message } = await login(payload);
    if (success) {
      navigate("/home");
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
                pattern="[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$"
                title="Please enter valid email id."
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
          <Form.Group as={Row} className="mt-3">
            <Col sm="6" className={styles.remember_me_switch}>
              <Form.Check
                type="checkbox"
                id="rememberMe"
                checked={rememberMe}
                label="Remember me"
                onChange={(e) => setRememberMe(e.target.checked)}
              />
            </Col>
            <Col sm="6" className={styles.sign_up}>
              <Link to="/signup" className={styles.sign_up_link}>
                New user ? Sign up
              </Link>
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
