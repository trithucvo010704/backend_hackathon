package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain;

public interface OrderExtractionGateway {
    OrderExtractionResult extract(OrderExtractionCommand command);
}
