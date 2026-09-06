import { Button } from "react-bootstrap";
import Modal from "react-bootstrap/Modal";

export function ConfirmationModal(props) {
  return (
    <Modal
      {...props}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">
          {props.modalContent?.title}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>{props.modalContent?.body}</Modal.Body>
      <Modal.Footer>
        <Button variant="success" onClick={props.onConfirm}>
          yes
        </Button>
        <Button variant="danger" onClick={props.onHide}>
          no
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
