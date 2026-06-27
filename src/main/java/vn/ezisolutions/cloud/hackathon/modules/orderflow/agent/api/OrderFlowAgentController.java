package vn.ezisolutions.cloud.hackathon.modules.orderflow.agent.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.agent.application.OrderFlowAgentInterpretService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class OrderFlowAgentController {
    private final OrderFlowAgentInterpretService interpretService;

    @PostMapping("/interpret")
    public BaseResponse interpret(@Valid @RequestBody OrderFlowAgentInterpretService.AgentInterpretRequest request) {
        return BaseResponse.success(interpretService.interpret(OrderFlowSecurity.currentUser(), request));
    }
}
