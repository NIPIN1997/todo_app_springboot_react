import Modal from "react-bootstrap/Modal";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import { useEffect, useState } from "react";
import Button from "react-bootstrap/Button";
import { toast } from "react-toastify";
import { checkUsername } from "../api/UserApi";
import { addDashboardApi } from "../api/DashboardApi";

export function AddDashboardModal(props) {
  const [fields, setFields] = useState([]);
  const [fieldName, setFieldName] = useState("");
  const [fieldPosition, setFieldPosition] = useState("");
  const [isPrivate, setIsPrivate] = useState(false);
  const NAME_REGEX = /^[a-zA-Z ]{3,}$/;
  const DASHBOARD_NAME_REGEX = /^[a-zA-Z0-9 ]{3,}$/;
  const POSITION_REGEX = /^[1-9][0-9]*$/;
  const [members, setMembers] = useState([]);
  const [memberUsername, setMemberUsername] = useState("");
  const [name, setName] = useState("");

  useEffect(() => {
    if (!props.show) {
      setFields([]);
      setFieldName("");
      setFieldPosition("");
      setIsPrivate(false);
      setMembers([]);
      setMemberUsername("");
    }
  }, [props.show]);

  const addField = () => {
    const name = fieldName.trim();
    const position = fieldPosition.toString().trim();
    if (!NAME_REGEX.test(name)) {
      toast.warn("Enter valid field name.");
      return;
    }
    if (!POSITION_REGEX.test(position)) {
      toast.warn("Enter valid position.");
      return;
    }
    if (fields.some((e) => e.fieldPosition == position)) {
      toast.warn(
        `Field position: ${position} already exists. Please enter another position.`,
      );
      return;
    }
    setFields((prev) => [
      ...prev,
      { fieldName: fieldName, fieldPosition: fieldPosition },
    ]);
    setFieldName("");
    setFieldPosition("");
  };

  const handleSwitchChange = (e) => {
    const switchStatus = e.target.checked;
    if (switchStatus) {
      setIsPrivate(true);
      setMembers([]);
      setMemberUsername("");
    } else {
      setIsPrivate(false);
    }
  };

  const addMembers = async () => {
    if (members.includes(memberUsername)) {
      setMemberUsername("");
      toast.warn("Member already added to dashboard.");
    } else {
      const response = await checkUsername(
        sessionStorage.getItem("jwtToken"),
        memberUsername,
      );
      if (response.data.data === "true") {
        setMembers((prev) => [...prev, memberUsername]);
        toast.info("Member added successfully.");
        setMemberUsername("");
      } else if (response.data.data === "false") {
        toast.warn("Member doesnot exist.");
      } else {
        toast.warn("You are already a member of this dashboard.");
      }
    }
  };

  const deleteField = (name) => {
    setFields((prev) => prev.filter((e) => e.fieldName !== name));
  };

  const deleteMember = (name) => {
    setMembers((prev) => prev.filter((e) => e !== name));
  };

  const addDashboard = async () => {
    if (DASHBOARD_NAME_REGEX.test(name)) {
      const data = {
        name: name,
        members: members,
        fields: fields,
        isPrivate: isPrivate,
      };
      const response = await addDashboardApi(
        sessionStorage.getItem("jwtToken"),
        data,
      );
      if (response.data.status === "SUCCESS") {
        toast.success("Dashboard created.");
        props.onHide();
      }
    } else {
      toast.warn("Dashboard name should contain minimum three letters.");
    }
  };

  return (
    <Modal
      {...props}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">
          Add Dashboard
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="1" htmlFor="name">
              Name
            </Form.Label>
            <Col sm="5">
              <Form.Control
                type="text"
                placeholder="Name"
                id="name"
                name="name"
                required
                pattern="[a-zA-Z ]{3,}"
                title="Please enter valid name."
                onChange={(e) => setName(e.target.value)}
              />
            </Col>
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Add Fields</Form.Label>
            <Row>
              <Col sm="4">
                <Form.Control
                  type="text"
                  placeholder="Field name"
                  name="field_name"
                  required
                  onChange={(e) => {
                    setFieldName(e.target.value);
                  }}
                  value={fieldName}
                />
              </Col>
              <Col sm="4">
                <Form.Control
                  type="number"
                  placeholder="Field position"
                  name="field_position"
                  required
                  onChange={(e) => setFieldPosition(e.target.value)}
                  value={fieldPosition}
                />
              </Col>
              <Col sm="4">
                <Button variant="primary" onClick={addField} type="button">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="currentColor"
                    class="bi bi-plus-lg"
                    viewBox="0 0 16 16"
                  >
                    <path
                      fill-rule="evenodd"
                      d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2"
                    />
                  </svg>
                  Add
                </Button>
              </Col>
            </Row>
          </Form.Group>
          {fields.length > 0 ? (
            <>
              <div>
                <Form.Label>Fields</Form.Label>
                <ul
                  style={{
                    border: "1px solid rgb(221,221,221)",
                    borderRadius: "10px",
                    height: "100px",
                    overflowY: "auto",
                    width: "50%",
                    paddingTop: "10px",
                    paddingBottom: "10px",
                  }}
                >
                  {[...fields]
                    .sort(
                      (a, b) =>
                        Number(a.fieldPosition) - Number(b.fieldPosition),
                    )
                    .map((e, index) => (
                      <li key={index}>
                        <b>Name:</b> {e.fieldName} | <b>Position:</b>{" "}
                        {e.fieldPosition}
                        <button
                          style={{
                            color: "red",
                            backgroundColor: "white",
                            border: "none",
                          }}
                          onClick={() => deleteField(e.fieldName)}
                          type="button"
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            fill="currentColor"
                            class="bi bi-x-circle-fill"
                            viewBox="0 0 16 16"
                          >
                            <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z" />
                          </svg>
                        </button>
                      </li>
                    ))}
                </ul>
              </div>
            </>
          ) : (
            <></>
          )}
          <Form.Check
            type="switch"
            id="custom-switch"
            label="Private Dashboard"
            onChange={handleSwitchChange}
          />
          {!isPrivate ? (
            <>
              <Form.Group className="mb-3">
                <Form.Label>Add Members</Form.Label>
                <Row>
                  <Col sm="4">
                    <Form.Control
                      type="text"
                      placeholder="Enter username"
                      name="username"
                      required
                      onChange={(e) => {
                        setMemberUsername(e.target.value);
                      }}
                      value={memberUsername}
                    />
                  </Col>
                  <Col sm="4">
                    <Button
                      variant="primary"
                      onClick={addMembers}
                      type="button"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        fill="currentColor"
                        class="bi bi-plus-lg"
                        viewBox="0 0 16 16"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2"
                        />
                      </svg>
                      Add
                    </Button>
                  </Col>
                </Row>
              </Form.Group>
              {members.length > 0 ? (
                <>
                  <div>
                    <Form.Label>Members</Form.Label>
                    <ol
                      style={{
                        border: "1px solid rgb(221,221,221)",
                        borderRadius: "10px",
                        height: "100px",
                        overflowY: "auto",
                        width: "50%",
                        paddingTop: "10px",
                        paddingBottom: "10px",
                      }}
                    >
                      {members.map((e, index) => (
                        <li key={index}>
                          {e}
                          <button
                            style={{
                              color: "red",
                              backgroundColor: "white",
                              border: "none",
                            }}
                            onClick={() => deleteMember(e)}
                            type="button"
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              width="16"
                              height="16"
                              fill="currentColor"
                              class="bi bi-x-circle-fill"
                              viewBox="0 0 16 16"
                            >
                              <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z" />
                            </svg>
                          </button>
                        </li>
                      ))}
                    </ol>
                  </div>
                </>
              ) : (
                <></>
              )}
            </>
          ) : (
            <></>
          )}
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="danger" onClick={props.onHide}>
          Cancel
        </Button>
        <Button
          variant="success"
          onClick={() => {
            addDashboard();
          }}
        >
          Save
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
