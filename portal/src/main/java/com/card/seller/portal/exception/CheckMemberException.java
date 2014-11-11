package com.card.seller.portal.exception;

/**
 * Created by lumine on 13-12-30.
 */
public class CheckMemberException extends Exception {
    private int resultCode;

    public CheckMemberException(String message, int resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}