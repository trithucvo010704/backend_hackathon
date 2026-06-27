package vn.ezisolutions.cloud.hackathon.controllers.app.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.services.auth.AuthService;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final AuthService authService;

    @GetMapping("/exchange")
    public BaseResponse auth(@RequestParam String code,
                             @RequestParam(name = "device_id") String deviceId,
                             @RequestParam(name = "client_id") String clientId) {
        return BaseResponse.success(authService.getExchange(code, clientId, deviceId));
    }
}
