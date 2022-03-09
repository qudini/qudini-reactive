package com.qudini.reactive.security.support.rsa;

public final class OpenSshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OpenSshException() {
        super();
    }

    public OpenSshException(String message) {
        super(message);
    }

    public OpenSshException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenSshException(Throwable cause) {
        super(cause);
    }

}
