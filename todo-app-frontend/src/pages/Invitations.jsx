import { useEffect, useState } from "react";
import { Header } from "../components/Header";
import { NavigationBar } from "../components/NavigationBar";
import styles from "../styles/invitations.module.css";
import { useAuth } from "../hooks/UseAuth";
import {
  acceptOrRejectInvitationApi,
  getInvitationsApi,
} from "../api/DashboardApi";
import Button from "react-bootstrap/Button";
import { toast } from "react-toastify";

export function Invitations() {
  const [invitations, setInvitations] = useState([]);
  const { jwtToken } = useAuth();
  const getInvitations = async () => {
    const response = await getInvitationsApi(jwtToken);
    setInvitations(response.data.data);
  };
  useEffect(() => {
    getInvitations();
  }, []);

  const acceptOrRejectInvitation = async (id, invitationStatus) => {
    const data = {
      id: id,
      invitationStatus: invitationStatus,
    };
    const response = await acceptOrRejectInvitationApi(jwtToken, data);
    if (response.data.status === "SUCCESS") {
      toast.success(response.data.message);
      getInvitations();
    }
  };
  return (
    <>
      <Header />
      <NavigationBar />
      <div className={styles.main_div}>
        <div className={styles.header}>Pending Invitations</div>
        {invitations.length > 0 ? (
          invitations.map((e) => (
            <div className={styles.invitation_item} key={e.id}>
              <div>
                <i>
                  <b>{e.masterName}</b> has invited you to join{" "}
                  <b>{e.dashboardName}</b> on Checklist.
                </i>
              </div>
              <div className={styles.invitation_item_buttons_div}>
                <Button
                  variant="success"
                  type="button"
                  onClick={() => acceptOrRejectInvitation(e.id, "ACCEPTED")}
                >
                  Accept
                </Button>
                <Button
                  variant="danger"
                  type="button"
                  onClick={() => acceptOrRejectInvitation(e.id, "REJECTED")}
                >
                  Reject
                </Button>
              </div>
            </div>
          ))
        ) : (
          <>
            <div className={styles.no_invitations_message}>
              No pending invitations.
            </div>
          </>
        )}
      </div>
    </>
  );
}
