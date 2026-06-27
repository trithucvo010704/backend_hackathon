package vn.ezisolutions.cloud.hackathon.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppAuthResponse {
    @JsonProperty("access_token")
    private String accessToken;

    private String username;

    private String email;

    private String avatar;

    @JsonProperty("client_id")
    private String clientId;

}
