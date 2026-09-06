import { useEffect, useState } from "react";
import styles from "../styles/tasks.module.css";
import Button from "react-bootstrap/Button";
import { useAuth } from "../hooks/UseAuth";
import { getDashboardById, getDashboardsByUserId } from "../api/DashboardApi";
import { AddDashboardModal } from "./AddDashboard";
import { AddTaskModal } from "./AddTask";
import { ViewTaskModal } from "./ViewTask";
import { EditTaskModal } from "./EditTask";
import Card from "react-bootstrap/Card";
import { dragTaskApi, getTaskById, getTaskDetailsEdit } from "../api/TaskApi";
import { useLocation } from "react-router-dom";

export function Tasks() {
  const [dashboards, setDashboards] = useState([]);
  const { jwtToken } = useAuth();
  const [modalShow, setModalShow] = useState(false);
  const [dashboard, setDashboard] = useState(null);
  const [addTaskModalShow, setAddTaskModalShow] = useState(false);
  const [viewTaskModalShow, setViewTaskModalShow] = useState(false);
  const [editTaskModalShow, setEditTaskModalShow] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [taskToBeEdited, setTaskToBeEdited] = useState(null);
  const location = useLocation();
  const passedDashboardId = location.state?.selectedDashboardId;
  const iconMap = {
    HAVE_TIME: {
      icon: <i class="bi bi-clock-fill"></i>,
      color: "#0d6efd",
      bgColor: "#cfe2ff",
      label: "On Track",
    },
    DUE_TODAY: {
      icon: <i class="bi bi-exclamation-circle-fill"></i>,
      color: "#ffc107",
      bgColor: "#fff3cd",
      label: "Due Today",
    },
    DUE: {
      icon: <i class="bi bi-exclamation-triangle-fill"></i>,
      color: "#dc3545",
      bgColor: "#f8d7da",
      label: "Overdue",
    },
    COMPLETED_ON_TIME: {
      icon: <i class="bi bi-check-circle-fill"></i>,
      color: "#198754",
      bgColor: "#d1e7dd",
      label: "Completed",
    },
    COMPLETED_LATE: {
      icon: <i class="bi bi-check2-all"></i>,
      color: "#6c757d",
      bgColor: "#e2e3e5",
      label: "Completed Late",
    },
  };

  const getDashboards = async () => {
    const response = await getDashboardsByUserId(jwtToken);
    setDashboards(response.data.data);
    const dashboards = response.data.data;
    let targetDashboardId;
    if (passedDashboardId) {
      targetDashboardId = passedDashboardId;
    } else {
      const dashboard = dashboards.filter(
        (e) => e.name === "Personal Dashboard",
      )[0];
      targetDashboardId = dashboard.id;
    }
    const dashboardResponse = await getDashboardById(
      targetDashboardId,
      jwtToken,
    );
    setDashboard(dashboardResponse.data.data);
  };

  useEffect(() => {
    getDashboards();
  }, []);

  const getDashboard = async (dashboardId) => {
    const dashboardResponse = await getDashboardById(dashboardId, jwtToken);
    setDashboard(dashboardResponse.data.data);
  };

  const handleDragStart = (event, taskId, sourceColumnId) => {
    event.dataTransfer.setData("taskId", taskId);
    event.dataTransfer.setData("sourceColumnId", sourceColumnId);
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleDrop = async (event, targetColumnId, dashboardId) => {
    event.preventDefault();
    const taskId = event.dataTransfer.getData("taskId");
    const sourceColumnId = event.dataTransfer.getData("sourceColumnId");
    if (sourceColumnId === targetColumnId) return;
    const data = {
      taskId: taskId,
      columnId: targetColumnId,
    };
    await dragTaskApi(jwtToken, data);
    getDashboard(dashboardId);
  };

  const viewTask = async (taskId) => {
    const response = await getTaskById(jwtToken, taskId);
    setSelectedTask(response.data.data);
    setViewTaskModalShow(true);
  };

  const viewTaskDetails = async (taskId) => {
    const response = await getTaskDetailsEdit(jwtToken, taskId);
    setTaskToBeEdited(response.data.data);
    setEditTaskModalShow(true);
  };

  return (
    <div className={styles.main_division}>
      <div className={styles.dashboard_division}>
        <h6>Dashboards</h6>
        <div className={styles.button_div}>
          <Button variant="light" onClick={() => setModalShow(true)}>
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
            <span style={{ fontWeight: "bold", marginLeft: "5px" }}>
              Add Dashboard
            </span>
          </Button>
        </div>
        <div className={styles.dashboard_list_division}>
          <div className={styles.dashboard_list}>
            {dashboards.map((e) => (
              <div
                key={e.id}
                className={
                  dashboard?.id === e.id
                    ? styles.dashboard_list_item_selected
                    : styles.dashboard_list_item
                }
                onClick={() => getDashboard(e.id)}
              >
                {e.name}
                {e.isPrivate ? (
                  <>
                    <span style={{ marginLeft: "10px" }}>
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        fill="currentColor"
                        class="bi bi-person-fill-lock"
                        viewBox="0 0 16 16"
                      >
                        <path d="M11 5a3 3 0 1 1-6 0 3 3 0 0 1 6 0m-9 8c0 1 1 1 1 1h5v-1a2 2 0 0 1 .01-.2 4.49 4.49 0 0 1 1.534-3.693Q8.844 9.002 8 9c-5 0-6 3-6 4m7 0a1 1 0 0 1 1-1v-1a2 2 0 1 1 4 0v1a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1h-4a1 1 0 0 1-1-1zm3-3a1 1 0 0 0-1 1v1h2v-1a1 1 0 0 0-1-1" />
                      </svg>
                    </span>
                  </>
                ) : (
                  <>
                    <span style={{ marginLeft: "10px" }}>
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        fill="currentColor"
                        class="bi bi-people-fill"
                        viewBox="0 0 16 16"
                      >
                        <path d="M7 14s-1 0-1-1 1-4 5-4 5 3 5 4-1 1-1 1zm4-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6m-5.784 6A2.24 2.24 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.3 6.3 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1zM4.5 8a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5" />
                      </svg>
                    </span>
                  </>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className={styles.task_division}>
        <div className={styles.dashboard_header_division}>
          <span>{dashboard?.name}</span>
          <span>
            <button
              onClick={() => setAddTaskModalShow(true)}
              className={styles.task_add_button}
            >
              + Add Task
            </button>
          </span>
        </div>
        <div className={styles.columns_division}>
          {dashboard?.columnWithTasksDtos?.map((e) => (
            <div
              className={styles.each_column}
              key={e.id}
              onDragOver={handleDragOver}
              onDrop={(event) => handleDrop(event, e.id, dashboard.id)}
            >
              <div className={styles.column_heading}>{e.name}</div>
              <div className={styles.column_task_area}>
                {e.tasksDtos.map((i) => (
                  <div
                    key={i.id}
                    draggable={true}
                    onDragStart={(event) => handleDragStart(event, i.id, e.id)}
                    style={{
                      cursor: "grab",
                      marginBottom: "2px",
                      width: "100%",
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
                    <Card className={styles.task_card_glass}>
                      <Card.Body className={styles.card_body_glass}>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                          }}
                        >
                          <div>
                            <h5 className={styles.task_title_glass}>
                              {i.title}
                            </h5>
                          </div>
                          <div style={{ marginBottom: "8px" }}>
                            <span
                              style={{
                                backgroundColor: iconMap[i.taskStatus].bgColor,
                                color: iconMap[i.taskStatus].color,
                                display: "inline-flex",
                                alignItems: "center",
                                gap: "6px",
                                padding: "2px 8px",
                                borderRadius: "12px",
                                fontSize: "12px",
                                fontWeight: "500",
                              }}
                            >
                              {iconMap[i.taskStatus].icon}
                              {iconMap[i.taskStatus].label}
                            </span>
                          </div>
                        </div>
                        <div className={styles.footer_row_glass}>
                          <span className={styles.date_badge_glass}>
                            {i.dueDate}
                          </span>
                          {dashboard?.isPrivate ? (
                            <></>
                          ) : (
                            <span className={styles.assignee_name_glass}>
                              {i.assignedTo}
                            </span>
                          )}
                        </div>
                        <Button
                          variant="link"
                          className={styles.task_view_link}
                          onClick={() => viewTask(i.id)}
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            fill="currentColor"
                            class="bi bi-eye-fill"
                            viewBox="0 0 16 16"
                          >
                            <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0" />
                            <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8m8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7" />
                          </svg>
                          <span style={{ marginLeft: "5px" }}>view</span>
                        </Button>
                      </Card.Body>
                    </Card>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
      <AddDashboardModal
        show={modalShow}
        onHide={() => {
          setModalShow(false);
          getDashboards();
        }}
      />
      <AddTaskModal
        show={addTaskModalShow}
        onHide={() => {
          setAddTaskModalShow(false);
          getDashboard(dashboard?.id);
        }}
        dashboardId={dashboard?.id}
        isPrivate={dashboard?.isPrivate}
      />
      <ViewTaskModal
        show={viewTaskModalShow}
        onHide={() => {
          setViewTaskModalShow(false);
        }}
        modalContent={selectedTask}
        dashboard={dashboard}
        getDashboard={getDashboard}
        setEditTaskModalShow={setEditTaskModalShow}
        setViewTaskModalShow={setViewTaskModalShow}
        viewTaskDetails={viewTaskDetails}
      />
      <EditTaskModal
        show={editTaskModalShow}
        onHide={() => {
          setEditTaskModalShow(false);
          getDashboard(dashboard?.id);
        }}
        dashboardId={dashboard?.id}
        isPrivate={dashboard?.isPrivate}
        modalContent={taskToBeEdited}
      />
    </div>
  );
}
