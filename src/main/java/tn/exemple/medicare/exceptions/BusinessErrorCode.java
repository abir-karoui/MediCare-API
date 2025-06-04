package tn.exemple.medicare.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
@Getter

public enum BusinessErrorCode {

    NO_CODE(0, NOT_IMPLEMENTED, "NO code" ),
    NOT_FOUND(3000, NOT_IMPLEMENTED, "User not found" ),

    INCORRECT_CURRENT_PASSWORD(1000, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOSES_NOT_MATCH(1001, BAD_REQUEST, "New password does not match"),
    INCORRECT_PASSWORD(1002, BAD_REQUEST, "Password is incorrect"),
    ACCOUNT_LOCKED(1002, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(1003, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(1004, FORBIDDEN, "Email or password is incorrect"),
    EMAIL_ALREADY_EXISTS(1005, CONFLICT, "Email address is already registered"),
    CODE_Expired(3001, BAD_REQUEST, "Code has expired. Please request a new one" ),
    CODE_INCORRECT(3002, BAD_REQUEST, "Incorrect code, please verify your code" ),
    INVITATION_ALREADY_SENT(4000, CONFLICT, "Invitation has already been sent to this user"),
    MEDICAL_CARD_NOT_VERIFIED(5000,BAD_REQUEST,"Your medical card has not yet been verified by the administrator."),
    MEDICAL_CARD_ALREADY_VERIFIED(5001,BAD_REQUEST,"The medical card has already been verified."),
    MEDICAL_RECORD_NOT_FOUND(6000, HttpStatus.NOT_FOUND, "You have not yet created your medical record"),
    DISEASE_NAME_ALREADY_EXISTS(7000, CONFLICT, "A disease with this name already exists"),
    PRESCRIPTION_ALREADY_EXISTS(8000, CONFLICT, "Vous avez deja ce medicament")



            ;
    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCode(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
