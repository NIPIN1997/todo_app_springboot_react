import Nav from "react-bootstrap/Nav";
import { Header } from "../components/Header";
import { NavigationBar } from "../components/NavigationBar";
import styles from "../styles/dashboards.module.css";
import Card from "react-bootstrap/Card";
import { useAuth } from "../hooks/UseAuth";
import { useEffect, useState } from "react";
import {
  archiveDashboardById,
  deleteDashboardById,
  getAllDashboardsApi,
  unarchiveDashboardById,
} from "../api/DashboardApi";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import { DonutChart } from "../components/DonutChart";
import Button from "react-bootstrap/Button";
import { ConfirmationModal } from "../components/ConfirmationModal";
import { EditDashboard } from "../components/EditDashboard";

export function Dashboards() {
  const { jwtToken } = useAuth();
  const [dashboards, setDashboards] = useState([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [modalContent, setModalContent] = useState({});
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  const [dashboardToDelete, setDasboardToDelete] = useState(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [dashboardIdToEdit, setDashboardIdToEdit] = useState(null);
  const getDashboards = async (category) => {
    setSelectedCategory(category);
    const response = await getAllDashboardsApi(category, jwtToken);
    setDashboards(response.data.data);
  };
  const deleteDashboard = async () => {
    setShowConfirmModal(false);
    await deleteDashboardById(dashboardToDelete, jwtToken);
    getDashboards(selectedCategory);
    toast.success("Dashboard deleted.");
  };
  const confirmDeleteDashboard = (dashboard) => {
    setDasboardToDelete(dashboard.id);
    setShowConfirmModal(true);
    setModalContent({
      title: "Delete Dashboard",
      body: `Are you sure you want to delete ${dashboard.name} dashboard ?`,
    });
  };
  const archiveDashboard = async (dashboardId) => {
    await archiveDashboardById(dashboardId, jwtToken);
    getDashboards(selectedCategory);
    toast.success("Dashboard archived.");
  };
  const unarchiveDashboard = async (dashboardId) => {
    await unarchiveDashboardById(dashboardId, jwtToken);
    getDashboards(selectedCategory);
    toast.success("Dashboard unarchived.");
  };
  const editDashboard = (dashboardId) => {
    setDashboardIdToEdit(dashboardId);
    setShowEditModal(true);
  };
  useEffect(() => {
    getDashboards("ALL");
  }, []);
  return (
    <>
      <Header />
      <NavigationBar />
      <div className={styles.navigation_bar}>
        <Nav justify variant="tabs" defaultActiveKey="all-dashboards">
          <Nav.Item>
            <Nav.Link
              eventKey="all-dashboards"
              onClick={() => getDashboards("ALL")}
            >
              <span className={styles.navigation_item}>All Dashboards</span>
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link
              eventKey="my-dashboards"
              onClick={() => getDashboards("OWNED")}
            >
              <span className={styles.navigation_item}>My Dashboards</span>
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link
              eventKey="shared-dashboards"
              onClick={() => getDashboards("SHARED")}
            >
              <span className={styles.navigation_item}>Shared Dashboards</span>
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link
              eventKey="archived-dashboards"
              onClick={() => getDashboards("ARCHIVED")}
            >
              <span className={styles.navigation_item}>
                Archived Dashboards
              </span>
            </Nav.Link>
          </Nav.Item>
        </Nav>
      </div>
      <div className={styles.content_div}>
        {dashboards.map((dashboard) => (
          <Card style={{ width: "20rem" }} key={dashboard.id}>
            <Card.Body>
              <Card.Title>
                <b>{dashboard.name}</b>
              </Card.Title>
              <Card.Subtitle className="mb-2 text-muted">
                <div className={styles.dashboard_card_div}>
                  <div>
                    <i>{dashboard.isMaster ? <>Master</> : <>Member</>}</i>
                  </div>
                  <div>
                    {dashboard.isPrivate ? (
                      <>
                        <i>Private</i>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="16"
                          height="16"
                          fill="currentColor"
                          class="bi bi-person-fill-lock ms-2"
                          viewBox="0 0 16 16"
                        >
                          <path d="M11 5a3 3 0 1 1-6 0 3 3 0 0 1 6 0m-9 8c0 1 1 1 1 1h5v-1a2 2 0 0 1 .01-.2 4.49 4.49 0 0 1 1.534-3.693Q8.844 9.002 8 9c-5 0-6 3-6 4m7 0a1 1 0 0 1 1-1v-1a2 2 0 1 1 4 0v1a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1h-4a1 1 0 0 1-1-1zm3-3a1 1 0 0 0-1 1v1h2v-1a1 1 0 0 0-1-1" />
                        </svg>
                      </>
                    ) : (
                      <>
                        <i>Public</i>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="16"
                          height="16"
                          fill="currentColor"
                          class="bi bi-people-fill ms-2"
                          viewBox="0 0 16 16"
                        >
                          <path d="M7 14s-1 0-1-1 1-4 5-4 5 3 5 4-1 1-1 1zm4-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6m-5.784 6A2.24 2.24 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.3 6.3 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1zM4.5 8a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5" />
                        </svg>
                      </>
                    )}
                  </div>
                </div>
                <div className={styles.dashboard_card_div}>
                  <div>
                    <i>
                      <b>Master:</b> {dashboard.masterName}
                    </i>
                  </div>
                  {!dashboard.isPrivate ? (
                    <div>
                      <i>
                        <b>Members:</b> {dashboard.numberOfMembers}
                      </i>
                    </div>
                  ) : (
                    <></>
                  )}
                </div>
              </Card.Subtitle>
              <div className={styles.chart_div}>
                <DonutChart statusMap={dashboard.statusMap} />
              </div>
              <Link
                to="/home"
                state={{ selectedDashboardId: dashboard.id }}
                className="btn btn-primary btn-sm"
                style={{
                  textDecoration: "none",
                  marginTop: "25px",
                  width: "100%",
                }}
              >
                <b>Go to Dashboard</b>
              </Link>
              {dashboard.name === "Personal Dashboard" ? (
                <></>
              ) : dashboard.isMaster ? (
                <div className={styles.button_div}>
                  <Button
                    variant="success"
                    onClick={() => editDashboard(dashboard.id)}
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
                    onClick={() => confirmDeleteDashboard(dashboard)}
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      fill="currentColor"
                      class="bi bi-trash3-fill"
                      viewBox="0 0 16 16"
                    >
                      <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5" />
                    </svg>
                    <span style={{ marginLeft: "5px" }}>Delete</span>
                  </Button>
                  {dashboard.isArchived ? (
                    <Button
                      variant="secondary"
                      onClick={() => unarchiveDashboard(dashboard.id)}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        fill="currentColor"
                        class="bi bi-arrow-counterclockwise"
                        viewBox="0 0 16 16"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M8 3a5 5 0 1 1-4.546 2.914.5.5 0 0 0-.908-.417A6 6 0 1 0 8 2z"
                        />
                        <path d="M8 4.466V.534a.25.25 0 0 0-.41-.192L5.23 2.308a.25.25 0 0 0 0 .384l2.36 1.966A.25.25 0 0 0 8 4.466" />
                      </svg>
                      <span style={{ marginLeft: "5px" }}>Unarchive</span>
                    </Button>
                  ) : (
                    <Button
                      variant="secondary"
                      onClick={() => archiveDashboard(dashboard.id)}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        fill="currentColor"
                        class="bi bi-archive-fill"
                        viewBox="0 0 16 16"
                      >
                        <path d="M12.643 15C13.979 15 15 13.845 15 12.5V5H1v7.5C1 13.845 2.021 15 3.357 15zM5.5 7h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1M.8 1a.8.8 0 0 0-.8.8V3a.8.8 0 0 0 .8.8h14.4A.8.8 0 0 0 16 3V1.8a.8.8 0 0 0-.8-.8z" />
                      </svg>
                      <span style={{ marginLeft: "5px" }}>Archive</span>
                    </Button>
                  )}
                </div>
              ) : (
                <></>
              )}
            </Card.Body>
          </Card>
        ))}
      </div>
      <ConfirmationModal
        show={showConfirmModal}
        onHide={() => {
          setShowConfirmModal(false);
        }}
        modalContent={modalContent}
        onConfirm={deleteDashboard}
      />
      <EditDashboard
        show={showEditModal}
        onHide={() => {
          setShowEditModal(false);
          getDashboards(selectedCategory);
        }}
        dashboardId={dashboardIdToEdit}
      />
    </>
  );
}
