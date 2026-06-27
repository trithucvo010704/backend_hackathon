package vn.ezisolutions.cloud.hackathon.records;

import java.util.UUID;

public record DocumentRef(UUID refId, UUID projectId, String refKey) {
}
