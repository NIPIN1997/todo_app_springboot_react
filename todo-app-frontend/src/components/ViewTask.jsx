import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import styles from "../styles/viewtask.module.css";
import ProgressBar from "react-bootstrap/ProgressBar";
import { ConfirmationModal } from "./ConfirmationModal";
import { useAuth } from "../hooks/UseAuth";
import { toast } from "react-toastify";
import { deleteTaskById } from "../api/TaskApi";
import { useState } from "react";

export function ViewTaskModal(props) {
  const { jwtToken } = useAuth();
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [modalContent, setModalContent] = useState({});
  const deleteTask = async () => {
    setShowConfirmModal(false);
    await deleteTaskById(jwtToken, props.modalContent?.id);
    props.getDashboard(props.dashboard?.id);
    toast.success("Task deleted.");
  };
  const confirmTaskDelete = (title) => {
    props.onHide();
    setShowConfirmModal(true);
    setModalContent({
      title: "Delete Task",
      body: `Are you sure you want to delete ${title} task ?`,
    });
  };

  return (
    <>
      <Modal
        {...props}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            <div className={styles.taskTitle}>{props.modalContent?.title}</div>
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className={styles.first_div}>
            <div>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                fill="currentColor"
                class="bi bi-calendar-fill"
                viewBox="0 0 18 18"
              >
                <path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V5h16V4H0V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5" />
              </svg>
              <span
                style={{
                  marginLeft: "5px",
                  fontWeight: "bold",
                  fontSize: "18px",
                }}
              >
                Due Date
              </span>
              <div style={{ marginTop: "15px" }}>
                {props.modalContent?.dueDate}
              </div>
            </div>
            {props.modalContent?.isPrivateDashboard ? (
              <></>
            ) : (
              <div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  fill="currentColor"
                  class="bi bi-person-fill"
                  viewBox="0 0 18 18"
                >
                  <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6" />
                </svg>
                <span
                  style={{
                    marginLeft: "5px",
                    fontWeight: "bold",
                    fontSize: "18px",
                  }}
                >
                  Assigned To
                </span>
                <div style={{ marginTop: "15px" }}>
                  {props.modalContent?.assignedTo}
                </div>
              </div>
            )}
            <div>
              <span
                style={{
                  marginLeft: "5px",
                  fontWeight: "bold",
                  fontSize: "18px",
                }}
              >
                Status
              </span>
              <div style={{ marginTop: "15px" }}>
                {props.modalContent?.status}
              </div>
            </div>
          </div>
          <div className={styles.progress_div}>
            <h5>Progress</h5>
            <ProgressBar
              variant="primary"
              now={props.modalContent?.progress}
              label={`${props.modalContent?.progress}%`}
            />
          </div>
          <div>
            <h5 style={{ fontWeight: "bold" }}>Description</h5>
            <div className={styles.description_div}>
              {props.modalContent?.description}
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="success"
            onClick={() => {
              props.viewTaskDetails(props.modalContent?.id);
              props.setViewTaskModalShow(false);
            }}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-pencil-square"
              viewBox="0 0 16 16"
            >
              <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
              <path
                fill-rule="evenodd"
                d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"
              />
            </svg>
            <span style={{ marginLeft: "5px" }}>Edit</span>
          </Button>
          <Button
            variant="danger"
            onClick={() => confirmTaskDelete(props.modalContent?.title)}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-trash-fill"
              viewBox="0 0 16 16"
            >
              <path d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5M8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5m3 .5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 1 0" />
            </svg>
            <span style={{ marginLeft: "5px" }}>Delete</span>
          </Button>
          <Button variant="secondary" onClick={props.onHide}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
      <ConfirmationModal
        show={showConfirmModal}
        onHide={() => {
          setShowConfirmModal(false);
        }}
        modalContent={modalContent}
        onConfirm={deleteTask}
      />
    </>
  );
}
