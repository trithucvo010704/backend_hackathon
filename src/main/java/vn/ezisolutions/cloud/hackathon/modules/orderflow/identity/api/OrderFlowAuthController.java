package vn.ezisolutions.cloud.hackathon.modules.orderflow.identity.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.identity.application.OrderFlowAuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OrderFlowAuthController {
    private final OrderFlowAuthService authService;

    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody LoginRequest request) {
        return BaseResponse.success(authService.login(request.email(), request.password()));
    }

    @GetMapping("/me")
    public BaseResponse me() {
        return BaseResponse.success(authService.currentUserPayload(OrderFlowSecurity.currentUser()));
    }

    @PostMapping("/logout")
    public BaseResponse logout() {
        return BaseResponse.success("Đăng xuất thành công");
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {
    }
}
