package vn.ezisolutions.cloud.hackathon.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizUser {
    protected String id;
    private String name;
    private String phone;
    private String email;
}


