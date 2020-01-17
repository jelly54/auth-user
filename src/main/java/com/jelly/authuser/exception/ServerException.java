package com.jelly.authuser.exception;

/**
 * @author guodongzhang
 */
public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 8224074865591050440L;
    private Integer errno;

    public ServerException() {
        super(ErrorEnum.INTERNAL_EXCEPT.message());
        this.errno = ErrorEnum.INTERNAL_EXCEPT.code();
    }

    public ServerException(ErrorEnum errorEnum) {
        super(errorEnum != null ? errorEnum.message() : ErrorEnum.INTERNAL_EXCEPT.message());
        this.errno = errorEnum != null ? errorEnum.code() : ErrorEnum.INTERNAL_EXCEPT.code();
    }

    public ServerException(ErrorEnum errorEnum, String message) {
        super((errorEnum != null ? errorEnum.message() + ";" : "") + message);
        this.errno = errorEnum != null ? errorEnum.code() : ErrorEnum.INTERNAL_EXCEPT.code();
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

}
