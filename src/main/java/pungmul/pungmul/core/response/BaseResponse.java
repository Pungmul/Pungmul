package pungmul.pungmul.core.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseResponse<E> {
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    private final String code;
    @JsonProperty(value = "isSuccess")
    private final boolean success;
    private final String message;
    private final E response;

    @Builder
    private BaseResponse(String code, String message, boolean success, E response){
        this.code = code;
        this.success = success;
        this.message = message;
        this.response = response;
    }

    // 반환 데이터가 없는 성공 메세지 템플릿
    public static <E> BaseResponse<E> ofSuccess(ResponseCode responseCode){
        return BaseResponse.<E>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .success(true)
                .response(null)
                .build();
    }

    // 반환 데이터가 있는 성공 메세지 템플릿
    public static <E> BaseResponse<E> ofSuccess(E response){
        return BaseResponse.<E>builder()
                .code(ResponseCode.OK.getCode())
                .message(ResponseCode.OK.getMessage())
                .success(true)
                .response(response)
                .build();
    }

    // 다양한 응답 코드를 설정할 수 있는 성공 메세지 템플릿
    public static <E> BaseResponse<E> ofSuccess(ResponseCode responseCode, E response){
        return BaseResponse.<E>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .success(true)
                .response(response)
                .build();
    }

    public static <E> BaseResponse<E> ofFail(ResponseCode responseCode){
        return BaseResponse.<E>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .success(false)
                .response(null)
                .build();
    }
}
