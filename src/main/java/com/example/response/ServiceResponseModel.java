package com.example.response;

import com.example.model.ErrorModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponseModel<T> {
    private boolean success = true;
    private T responseBody;
    private ErrorModel error;
    public static ServiceResponseModel<?> empty(){
        return new ServiceResponseModel<>();
    }
    public static <T> ServiceResponseModel<T> success(T responseBody){
        ServiceResponseModel<T> response = new ServiceResponseModel<T>();
        response.setResponseBody(responseBody);

        return response;
    }
    public static ServiceResponseModel<?> success(){
        return new ServiceResponseModel<>();
    }
    public static ServiceResponseModel<?> failure(String errorMessage, String errorCode){
        ServiceResponseModel<?> response = new ServiceResponseModel<>();
        ErrorModel errorModel = new ErrorModel(errorMessage, errorCode);
        response.setError(errorModel);

        return response;
    }


}
