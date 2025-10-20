<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- Approval Modal -->
        <div class="modal fade" id="approvalModal" tabindex="-1" aria-labelledby="approvalModalLabel" aria-hidden="true"
            style="z-index: 9999;">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="approvalModalLabel">
                            <i class="fas fa-clipboard-check me-2"></i>Approve Request
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-3">
                            <strong>Request:</strong> <span id="modalRequestTitle"></span>
                        </p>
                        <p class="mb-3">
                            <strong>Employee:</strong> <span id="modalEmployeeName"></span>
                        </p>
                        <input type="hidden" id="modalRequestId">

                        <div class="mb-3">
                            <label class="form-label">Decision <span class="text-danger">*</span></label>
                            <div class="btn-group w-100" role="group">
                                <input type="radio" class="btn-check" name="decision" id="decisionAccept" value="accept"
                                    checked>
                                <label class="btn btn-success" for="decisionAccept">
                                    <i class="fas fa-check me-1"></i>Accept
                                </label>

                                <input type="radio" class="btn-check" name="decision" id="decisionReject"
                                    value="reject">
                                <label class="btn btn-danger" for="decisionReject">
                                    <i class="fas fa-times me-1"></i>Reject
                                </label>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="approvalReason" class="form-label">
                                Reason <span id="reasonRequired" class="text-danger" style="display:none;">*</span>
                            </label>
                            <textarea class="form-control" id="approvalReason" rows="3"
                                placeholder="Enter reason (required for rejection, optional for acceptance)"></textarea>
                            <div class="invalid-feedback" id="reasonError">
                                Rejection reason is required
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-1"></i>Cancel
                        </button>
                        <button type="button" class="btn btn-primary" onclick="submitApproval()">
                            <i class="fas fa-paper-plane me-1"></i>Submit
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Styles -->
        <style>
            .modal-backdrop {
                z-index: 9998 !important;
            }

            #approvalModal {
                z-index: 9999 !important;
            }

            /* Improve decision button visibility when selected */
            #approvalModal .btn-check:checked+.btn-success {
                background-color: #198754 !important;
                border-color: #198754 !important;
                color: white !important;
                font-weight: bold;
                box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.5) !important;
            }

            #approvalModal .btn-check:checked+.btn-danger {
                background-color: #dc3545 !important;
                border-color: #dc3545 !important;
                color: white !important;
                font-weight: bold;
                box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.5) !important;
            }

            #approvalModal .btn-check:not(:checked)+.btn-success {
                background-color: white !important;
                border: 2px solid #198754 !important;
                color: #198754 !important;
            }

            #approvalModal .btn-check:not(:checked)+.btn-danger {
                background-color: white !important;
                border: 2px solid #dc3545 !important;
                color: #dc3545 !important;
            }

            #approvalModal .btn-check+label {
                transition: all 0.2s ease-in-out;
            }
        </style>