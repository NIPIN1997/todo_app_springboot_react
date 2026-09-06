import Modal from "react-bootstrap/Modal";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";
import Button from "react-bootstrap/Button";
import { useEffect, useState } from "react";
import {
  getColumnNamesForDashboard,
  getMemberNamesForDashboard,
} from "../api/DashboardApi";
import { addTaskApi, editTaskApi } from "../api/TaskApi";
import { toast } from "react-toastify";

export function EditTaskModal(props) {
  const [taskTitle, setTaskTitle] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [description, setDescription] = useState("");
  const [assignedTo, setAssignedTo] = useState("");
  const [status, setStatus] = useState("");
  const dashboardId = props.dashboardId;
  const [columnNames, setColumnNames] = useState([]);
  const [memberNames, setMemberNames] = useState([]);

  const getColumnNames = async () => {
    const response = await getColumnNamesForDashboard(
      dashboardId,
      sessionStorage.getItem("jwtToken"),
    );
    setColumnNames(response.data.data);
  };

  const getMemberNames = async () => {
    const response = await getMemberNamesForDashboard(
      dashboardId,
      sessionStorage.getItem("jwtToken"),
    );
    setMemberNames(response.data.data);
  };

  const editTask = async () => {
    const data = {
      id: props.modalContent?.id,
      title: taskTitle,
      description: description,
      dueDate: dueDate,
      column: status,
      dashboard: dashboardId,
      assignedTo: assignedTo,
    };

    const response = await editTaskApi(
      sessionStorage.getItem("jwtToken"),
      data,
    );
    if (response.data.status === "SUCCESS") {
      toast.success("Task edited.");
      props.onHide();
    }
  };

  useEffect(() => {
    if (props.modalContent) {
      setTaskTitle(props.modalContent.title || "");
      setDueDate(props.modalContent.dueDate || "");
      setDescription(props.modalContent.description || "");
      setAssignedTo(props.modalContent.editTaskViewUser.id || "");
      setStatus(props.modalContent.editTaskViewColumn.id || "");
    }
  }, [props.show, props.modalContent]);

  useEffect(() => {
    if (props.show && dashboardId) {
      getColumnNames();
      getMemberNames();
    }
  }, [props.show, dashboardId]);

  return (
    <Modal
      {...props}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">Edit Task</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="2" htmlFor="title">
              Title
            </Form.Label>
            <Col sm="5">
              <Form.Control
                type="text"
                placeholder="Title"
                id="title"
                name="title"
                required
                pattern="[a-zA-Z ]{3,}"
                title="Please enter valid title."
                onChange={(e) => setTaskTitle(e.target.value)}
                value={taskTitle}
              />
            </Col>
          </Form.Group>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="2" htmlFor="dueDate">
              Due date
            </Form.Label>
            <Col sm="5">
              <Form.Control
                type="date"
                placeholder="Due date"
                id="dueDate"
                name="dueDate"
                required
                onChange={(e) => setDueDate(e.target.value)}
                value={dueDate}
              />
            </Col>
          </Form.Group>
          <Form.Group as={Row} className="mb-3">
            <Form.Label column sm="2" htmlFor="status">
              Status
            </Form.Label>
            <Col sm="5">
              <Form.Select
                aria-label="status"
                onChange={(e) => setStatus(e.target.value)}
                value={status}
              >
                {columnNames.map((e) => (
                  <option key={e.id} value={e.id} selected={e.id === status}>
                    {e.name}
                  </option>
                ))}
              </Form.Select>
            </Col>
          </Form.Group>
          {props.isPrivate ? (
            <></>
          ) : (
            <Form.Group as={Row} className="mb-3">
              <Form.Label column sm="2" htmlFor="assignedTo">
                Assigned To
              </Form.Label>
              <Col sm="5">
                <Form.Select
                  aria-label="assignedTo"
                  onChange={(e) => setAssignedTo(e.target.value)}
                  value={assignedTo}
                >
                  {memberNames.map((e) => (
                    <option
                      key={e.id}
                      value={e.id}
                      selected={e.id === assignedTo}
                    >
                      {e.name}
                    </option>
                  ))}
                </Form.Select>
              </Col>
            </Form.Group>
          )}
          <Form.Group as={Row} className="mb-3">
            <Form.Label htmlFor="description">Description</Form.Label>
            <Form.Control
              as="textarea"
              placeholder="Description"
              id="description"
              name="description"
              onChange={(e) => setDescription(e.target.value)}
              style={{ width: "75%", marginLeft: "5px" }}
              value={description}
            />
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="danger" onClick={props.onHide}>
          Cancel
        </Button>
        <Button variant="success" onClick={() => editTask()}>
          Edit
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
