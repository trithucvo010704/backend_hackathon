package vn.ezisolutions.cloud.hackathon.core.utils;

import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;

public class HttpResponseUtils {
    public static BaseResponse errorClient(String message) {
        return BaseResponse.builder()
                .message(message)
                .status(400)
                .build();
    }
}
