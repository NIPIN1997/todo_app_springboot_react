import Container from "react-bootstrap/esm/Container";
import { Header } from "../components/Header";
import styles from "../styles/signup.module.css";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import { useState } from "react";
import { toast } from "react-toastify";
import { signupApi } from "../api/UserApi";
import { useNavigate } from "react-router-dom";

export function Signup() {
  const [data, setData] = useState({});
  const navigate = useNavigate();
  const handleChange = (e) => {
    setData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    if (data["password"] === data["confirm-password"]) {
      const payload = {
        name: data["name"],
        contact: data["contact"],
        email: data["email"],
        password: data["password"],
      };
      const response = await signupApi(payload);
      if (response.status === 201) {
        toast.success("Account created. Please login.");
        navigate("/");
      } else {
        toast.error("Failed to create account. Please try again.");
        window.location.reload();
      }
    } else {
      toast.warning("Passwords donot match");
    }
  };
  return (
    <Container fluid>
      <Header />
      <div className={styles.form_division}>
        <h1 className={styles.heading}>Signup</h1>
        <Form onSubmit={handleSubmit}>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="2" htmlFor="name">
              Name
            </Form.Label>
            <Col sm="10">
              <Form.Control
                type="text"
                placeholder="Enter name"
                id="name"
                name="name"
                required
                pattern="[a-zA-Z]{3,}"
                title="Please enter valid name."
                onChange={handleChange}
              />
            </Col>
          </Form.Group>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="2" htmlFor="contact">
              Contact
            </Form.Label>
            <Col sm="10">
              <Form.Control
                type="text"
                placeholder="98********"
                id="contact"
                name="contact"
                required
                pattern="[1-9]{1}[0-9]{9}"
                title="Please enter valid contact number."
                onChange={handleChange}
              />
            </Col>
          </Form.Group>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="2" htmlFor="email">
              Email
            </Form.Label>
            <Col sm="10">
              <Form.Control
                type="email"
                placeholder="name@email.com"
                id="email"
                name="email"
                required
                pattern="[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$"
                title="Please enter valid email id."
                onChange={handleChange}
              />
            </Col>
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="password">Password</Form.Label>
            <Form.Control
              type="password"
              placeholder="Password"
              id="password"
              name="password"
              required
              onChange={handleChange}
            />
          </Form.Group>
          <Form.Group>
            <Form.Label htmlFor="confirm-password">Confirm Password</Form.Label>
            <Form.Control
              type="password"
              placeholder="Password"
              id="confirm-password"
              name="confirm-password"
              required
              onChange={handleChange}
            />
          </Form.Group>
          <div className={styles.submit_button_division}>
            <button className={styles.submit_button} type="submit">
              signup
            </button>
          </div>
        </Form>
      </div>
    </Container>
  );
}
