import { useEffect, useState } from "react";
import Modal from "react-bootstrap/Modal";
import Table from "react-bootstrap/Table";
import {
  addDashboardMemberAPi,
  editDashboardColumnName,
  editDashboardNameApi,
  getDashboardDetailsForEdit,
  promoteDashboardMember,
  removeDashboardMember,
} from "../api/DashboardApi";
import Button from "react-bootstrap/Button";
import { useAuth } from "../hooks/UseAuth";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import { toast } from "react-toastify";

export function EditDashboard({ dashboardId, show, onHide, ...props }) {
  const [dashboard, setDashboard] = useState(null);
  const [dashboardName, setDashboardName] = useState("");
  const [dashboardMembers, setDashboardMembers] = useState(null);
  const [dashboardColumns, setDashboardColumns] = useState(null);
  const { jwtToken } = useAuth();
  const [editDashboardName, setEditDashboardName] = useState(false);
  const [addMemberUsername, setAddMemberUsername] = useState(null);
  const [editingColumnId, setEditingColumnId] = useState(null);
  const [editingColumnName, setEditingColumnName] = useState(null);
  const getDashboardDetails = async () => {
    const response = await getDashboardDetailsForEdit(dashboardId, jwtToken);
    setDashboard(response.data.data);
    setDashboardName(response.data.data.name);
    setDashboardMembers(response.data.data.members);
    setDashboardColumns(response.data.data.columns);
  };
  const editDashboardNameFunction = async () => {
    const data = {
      id: dashboardId,
      name: dashboardName,
    };
    await editDashboardNameApi(jwtToken, data);
    toast.success("Dashboard name edited.");
    getDashboardDetails();
    setEditDashboardName(false);
  };
  const removeMember = async (memberId, memberName) => {
    const data = {
      dashboardId: dashboardId,
      memberId: memberId,
    };
    await removeDashboardMember(jwtToken, data);
    toast.success(`${memberName} removed from dashboard.`);
    getDashboardDetails();
  };
  const promoteMember = async (memberId, memberName) => {
    const data = {
      dashboardId: dashboardId,
      memberId: memberId,
    };
    await promoteDashboardMember(jwtToken, data);
    toast.success(`${memberName} promoted to master.`);
    getDashboardDetails();
  };
  const addDashboardMember = async () => {
    const data = {
      dashboardId: dashboardId,
      username: addMemberUsername,
    };
    try {
      await addDashboardMemberAPi(jwtToken, data);
      toast.success("Invitation sent to user.");
    } finally {
      setAddMemberUsername("");
      getDashboardDetails();
    }
  };
  const editColumnName = async () => {
    const data = {
      columnID: editingColumnId,
      columnName: editingColumnName,
    };
    await editDashboardColumnName(jwtToken, data);
    toast.success("Column name edited.");
    setEditingColumnId(null);
    setEditingColumnName(null);
    getDashboardDetails();
  };
  useEffect(() => {
    if (!dashboardId) {
      return;
    }
    getDashboardDetails();
  }, [dashboardId, show]);
  return (
    <Modal
      show={show}
      onHide={onHide}
      {...props}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">
          Edit Dashboard
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
                value={dashboardName}
                readOnly={!editDashboardName}
                onChange={(e) => setDashboardName(e.target.value)}
              />
            </Col>
            {!editDashboardName ? (
              <Col sm="3">
                <Button
                  variant="secondary"
                  onClick={() => setEditDashboardName(true)}
                  style={{ color: "white" }}
                >
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
                </Button>
              </Col>
            ) : (
              <></>
            )}
            {editDashboardName ? (
              <>
                <Col sm="1">
                  <Button
                    variant="success"
                    onClick={() => editDashboardNameFunction()}
                  >
                    edit
                  </Button>
                </Col>
                <Col sm="1">
                  <Button
                    variant="danger"
                    onClick={() => {
                      setEditDashboardName(false);
                      setDashboardName(dashboard.name);
                    }}
                  >
                    cancel
                  </Button>
                </Col>
              </>
            ) : (
              <></>
            )}
          </Form.Group>
        </Form>
        {dashboard?.isPrivate ? (
          <></>
        ) : (
          <>
            <h5 className="mb-3">Members</h5>
            <Form.Group as={Row} className="mb-3">
              <Row>
                <Col>
                  <Form.Control
                    type="text"
                    placeholder="Enter username"
                    id="username"
                    name="username"
                    value={addMemberUsername}
                    onChange={(e) => setAddMemberUsername(e.target.value)}
                  />
                </Col>
                <Col>
                  <Button
                    variant="outline-primary rounded-pill"
                    onClick={() => addDashboardMember()}
                  >
                    + Add member
                  </Button>
                </Col>
              </Row>
            </Form.Group>
            {dashboardMembers?.length > 0 ? (
              <>
                <div className="bordered rounded-3 overflow-hidden mb-2">
                  <Table hover className="align-middle mb-0 w-75">
                    <thead className="table-light border-bottom">
                      <th
                        scope="col"
                        className="py-3 px-3 text-secondary text-start"
                      >
                        Name
                      </th>
                      <th
                        scope="col"
                        className="py-3 px-3 text-start text-secondary"
                        colSpan={2}
                      >
                        Options
                      </th>
                    </thead>
                    <tbody>
                      {dashboardMembers?.map((member) => (
                        <tr key={member.id}>
                          <td className="px-3 py-2 text-start">
                            {member.name}
                          </td>
                          <td className="py-2 text-end" style={{ width: "1%" }}>
                            <Button
                              variant="outline-danger"
                              size="sm"
                              className="d-inline-flex align-items-center gap-1 px-3 py-1 rounded-pill"
                              onClick={() =>
                                removeMember(member.id, member.name)
                              }
                            >
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                fill="currentColor"
                                class="bi bi-x-circle-fill me-1"
                                viewBox="0 0 16 16"
                              >
                                <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z" />
                              </svg>
                              Remove
                            </Button>
                          </td>
                          <td>
                            <Button
                              variant="outline-primary"
                              size="sm"
                              className="d-inline-flex align-items-center gap-1 px-3 py-1 rounded-pill"
                              onClick={() =>
                                promoteMember(member.id, member.name)
                              }
                            >
                              Promote To Master
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              </>
            ) : (
              <></>
            )}
          </>
        )}
        <h5 className="mb-3">Columns</h5>
        <div className="bordered rounded-3 overflow-hidden mb-2">
          <Table hover className="align-middle mb-0 w-75">
            <thead className="table-light border-bottom">
              <th scope="col" className="py-3 px-3 text-secondary text-start">
                Column Name
              </th>
              <th
                scope="col"
                className="py-3 px-3 text-start text-secondary text-start"
              >
                Position
              </th>
              <th
                scope="col"
                className="py-3 px-3 text-start text-secondary text-center"
                colSpan={2}
              >
                Options
              </th>
            </thead>
            <tbody>
              {dashboardColumns?.map((column) => {
                const isEditingRow = editingColumnId === column.id;
                return (
                  <tr key={column.id}>
                    {isEditingRow ? (
                      <>
                        <td>
                          <Form.Control
                            type="text"
                            placeholder="Column Name"
                            id="columnName"
                            name="columnName"
                            required
                            value={editingColumnName}
                            onChange={(e) =>
                              setEditingColumnName(e.target.value)
                            }
                          />
                        </td>
                      </>
                    ) : (
                      <>
                        <td className="px-3 py-2 text-start">{column.name}</td>
                      </>
                    )}
                    <td className="px-3 py-2 text-start">{column.position}</td>
                    <td>
                      {isEditingRow ? (
                        <>
                          <Button
                            variant="outline-success"
                            className="me-1 fw-bold"
                            size="sm"
                            onClick={() => {
                              editColumnName();
                            }}
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              width="16"
                              height="16"
                              fill="currentColor"
                              class="bi bi-check-lg"
                              viewBox="0 0 16 16"
                            >
                              <path d="M12.736 3.97a.733.733 0 0 1 1.047 0c.286.289.29.756.01 1.05L7.88 12.01a.733.733 0 0 1-1.065.02L3.217 8.384a.757.757 0 0 1 0-1.06.733.733 0 0 1 1.047 0l3.052 3.093 5.4-6.425z" />
                            </svg>
                          </Button>
                          <Button
                            variant="outline-danger"
                            className="fw-bold"
                            size="sm"
                            onClick={() => {
                              setEditingColumnId(null);
                              setEditingColumnName(null);
                            }}
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              width="16"
                              height="16"
                              fill="currentColor"
                              class="bi bi-x-lg"
                              viewBox="0 0 16 16"
                            >
                              <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8z" />
                            </svg>
                          </Button>
                        </>
                      ) : (
                        <>
                          <Button
                            variant="outline-success"
                            size="sm"
                            className="d-inline-flex align-items-center gap-1 px-3 py-1 rounded-pill"
                            onClick={() => {
                              setEditingColumnId(column.id);
                              setEditingColumnName(column.name);
                            }}
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              width="16"
                              height="16"
                              fill="currentColor"
                              class="bi bi-pencil-square me-1"
                              viewBox="0 0 16 16"
                            >
                              <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
                              <path
                                fill-rule="evenodd"
                                d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"
                              />
                            </svg>
                            edit
                          </Button>
                        </>
                      )}
                    </td>
                    <td>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        className="d-inline-flex align-items-center gap-1 px-3 py-1 rounded-pill"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="16"
                          height="16"
                          fill="currentColor"
                          class="bi bi-x-circle-fill me-1"
                          viewBox="0 0 16 16"
                        >
                          <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z" />
                        </svg>
                        Remove
                      </Button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </div>
      </Modal.Body>
    </Modal>
  );
}
