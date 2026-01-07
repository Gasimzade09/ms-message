package com.rockup.app.exception;

public class VersionMismatchException extends WebSocketException {
    public VersionMismatchException(Integer expected, Integer actual) {
        super("Version mismatch. Expected=" + expected + ", actual=" + actual);
    }

    @Override
    public String getCode() {
        return "version_mismatch";
    }
}
