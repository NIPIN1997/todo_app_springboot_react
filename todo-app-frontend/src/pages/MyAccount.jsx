import { useEffect, useState } from "react";
import { Header } from "../components/Header";
import { NavigationBar } from "../components/NavigationBar";
import styles from "../styles/myaccount.module.css";
import { useAuth } from "../hooks/UseAuth";
import { getUserById } from "../api/UserApi";
import { toast } from "react-toastify";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import { editUser } from "../api/UserApi";
import Button from "react-bootstrap/Button";
import { Link } from "react-router-dom";

export function MyAccount() {
  const [user, setUser] = useState(null);
  const { jwtToken } = useAuth();
  const [isEdit, setIsEdit] = useState(false);
  const [data, setData] = useState({});
  useEffect(() => {
    setData(user);
  }, [user]);
  const getUser = async () => {
    try {
      const response = await getUserById(jwtToken);
      setUser(response.data.data);
    } catch (error) {
      toast.error("Failed to fetch account details. Error - ", error);
    }
  };
  useEffect(() => {
    getUser();
  }, []);
  const handleClick = () => {
    setIsEdit(true);
  };
  const handleChange = (event) => {
    setData((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    const response = await editUser(jwtToken, data);
    if (response.data.status == "SUCCESS") {
      toast.success("Data edited successfully.");
      setIsEdit(false);
      getUser();
    } else {
      toast.error("Failed to edit data. Please try again.");
      setIsEdit(false);
    }
  };
  return (
    <>
      <Header />
      <NavigationBar />
      {user ? (
        isEdit ? (
          <div className={styles.form_division}>
            <h1 className={styles.heading}>Edit Account</h1>
            <Form onSubmit={handleSubmit}>
              <Form.Group as={Row} className="mb-3">
                <Form.Label column sm="3" htmlFor="name">
                  Name
                </Form.Label>
                <Col sm="9">
                  <Form.Control
                    type="text"
                    id="name"
                    name="name"
                    required
                    value={data.name}
                    onChange={handleChange}
                  />
                </Col>
              </Form.Group>
              <Form.Group as={Row} className="mb-3">
                <Form.Label column sm="3" htmlFor="email">
                  Email
                </Form.Label>
                <Col sm="9">
                  <Form.Control
                    type="email"
                    id="email"
                    name="email"
                    value={data.email}
                    readOnly
                  />
                </Col>
              </Form.Group>
              <Form.Group as={Row} className="mb-3">
                <Form.Label column sm="3" htmlFor="contact">
                  Contact
                </Form.Label>
                <Col sm="9">
                  <Form.Control
                    type="text"
                    id="contact"
                    name="contact"
                    required
                    value={data.contact}
                    onChange={handleChange}
                  />
                </Col>
              </Form.Group>
              <div className={styles.button_division}>
                <Button
                  variant="success"
                  className={styles.edit_button}
                  type="submit"
                >
                  edit
                </Button>
                <Button
                  variant="danger"
                  className={styles.cancel_button}
                  type="submit"
                  onClick={() => {
                    setIsEdit(false);
                    setData(user);
                  }}
                >
                  cancel
                </Button>
              </div>
            </Form>
          </div>
        ) : (
          <div className={styles.account_division}>
            <h1 className={styles.heading}>Account</h1>
            <div className={styles.edit_button_icon_division}>
              <button className={styles.edit_button_icon} onClick={handleClick}>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="20"
                  height="20"
                  fill="currentColor"
                  className="bi bi-pencil-square"
                  viewBox="0 0 16 16"
                >
                  <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
                  <path
                    fillRule="evenodd"
                    d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"
                  />
                </svg>
              </button>
            </div>
            <h5 className="mb-3">
              <b>Name: </b>
              {user.name}
            </h5>
            <h5 className="mb-3">
              <b>Email: </b>
              {user.email}
            </h5>
            <h5 className="mb-3">
              <b>Contact: </b>
              {user.contact}
            </h5>
            <div className="mt-3">
              <Link to="/devices">
                <button className={styles.devices_button} type="button">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="currentColor"
                    class="bi bi-laptop me-2"
                    viewBox="0 0 16 16"
                  >
                    <path d="M13.5 3a.5.5 0 0 1 .5.5V11H2V3.5a.5.5 0 0 1 .5-.5zm-11-1A1.5 1.5 0 0 0 1 3.5V12h14V3.5A1.5 1.5 0 0 0 13.5 2zM0 12.5h16a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 0 12.5" />
                  </svg>
                  Devices
                </button>
              </Link>
            </div>
          </div>
        )
      ) : (
        <div></div>
      )}
    </>
  );
}
