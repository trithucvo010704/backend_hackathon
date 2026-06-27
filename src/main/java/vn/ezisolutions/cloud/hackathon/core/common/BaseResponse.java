package vn.ezisolutions.cloud.hackathon.core.common;

import lombok.*;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BaseResponse implements Serializable {
    private Integer status;
    private String message;

    private Object data;

    public BaseResponse() {
        this.status = 1;
        this.message = "success";
        this.data = null;
    }

    public <T> BaseResponse(T data) {
        this.status = 1;
        this.message = "success";
        this.data = data;
    }

    public <T> BaseResponse(List<T> data) {
        this.status = 1;
        this.message = "success";
        this.data = data;
    }

    public <T> BaseResponse(Page<T> data) {
        this.status = 1;
        this.message = "success";
        this.data = new BasePagination<T>(data);
    }

    public static <T> BaseResponse success(String message, T data) {
        return BaseResponse.builder().status(1).message(message).data(data).build();
    }

    public static <T> BaseResponse success(T data) {
        return BaseResponse.builder().status(1).message("success").data(data).build();
    }

    public static <T> BaseResponse success(String message) {
        return BaseResponse.builder().status(1).message(message).data(null).build();
    }


    public static <T> BaseResponse fail(String message, T data) {
        return BaseResponse.builder().status(0).message(message).data(data).build();
    }

    public static <T> BaseResponse fail(T data) {
        return BaseResponse.builder().status(0).message("failed").data(data).build();
    }

    public static <T> BaseResponse fail(String message) {
        return BaseResponse.builder().status(0).message(message).data(null).build();
    }

}
