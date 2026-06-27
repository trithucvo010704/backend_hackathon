package vn.ezisolutions.cloud.hackathon.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class IdSystemAuthExchangeRequest {
    @NotBlank(message = "code không được để trống")
    private String code;

    @NotBlank(message = "client_id không được để trống")
    @JsonProperty("client_id")
    private String clientId;

    @NotNull(message = "device_id không được để trống")
    @JsonProperty("device_id")
    private UUID deviceId;

}
