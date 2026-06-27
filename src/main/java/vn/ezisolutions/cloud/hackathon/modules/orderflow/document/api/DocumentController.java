package vn.ezisolutions.cloud.hackathon.modules.orderflow.document.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DocumentType;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.DraftOrderDocumentRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.order.application.DraftOrderWorkflowService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentController {
    private final DraftOrderWorkflowService workflowService;
    private final DraftOrderDocumentRepository documentRepository;

    @PostMapping("/draft-orders/{id}/documents/quote")
    public BaseResponse quote(@PathVariable UUID id) {
        return BaseResponse.success(workflowService.generateDocument(OrderFlowSecurity.currentUser(), id, DocumentType.QUOTE));
    }

    @PostMapping("/draft-orders/{id}/documents/pick-list")
    public BaseResponse pickList(@PathVariable UUID id) {
        return BaseResponse.success(workflowService.generateDocument(OrderFlowSecurity.currentUser(), id, DocumentType.PICK_LIST));
    }

    @GetMapping("/draft-orders/{id}/documents")
    public BaseResponse documents(@PathVariable UUID id) {
        return BaseResponse.success(documentRepository.findByDraftOrderIdOrderByCreatedAtDesc(id));
    }

    @GetMapping("/draft-order-documents/{id}")
    public BaseResponse document(@PathVariable UUID id) {
        return BaseResponse.success(documentRepository.findById(id).orElseThrow());
    }
}
