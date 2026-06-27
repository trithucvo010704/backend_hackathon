package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "draft_order_documents")
public class DraftOrderDocumentEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "draft_order_id", nullable = false)
    private UUID draftOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(name = "html_snapshot", columnDefinition = "TEXT")
    private String htmlSnapshot;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "generated_by_user_id")
    private UUID generatedByUserId;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
