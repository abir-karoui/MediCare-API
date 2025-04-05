package tn.exemple.medicare.exceptions;

import lombok.Getter;

@Getter
public class BusinessException  extends RuntimeException {
    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
