package com.ceres.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebActionsService {

    private final AuthenticationService authService;

    public OperationReturnObject processAction(String service, String action, JSONObject payload) {
        return switch (service) {
            case "Authentication" -> authService.process(action, payload);
            default -> new OperationReturnObject(404, "UNKNOWN SERVICE", null);
        };
    }
}
