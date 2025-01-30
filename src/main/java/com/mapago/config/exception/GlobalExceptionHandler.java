package com.mapago.config.exception;

import java.sql.SQLException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // DataIntegrityViolationException 처리 (무결성 제약 위반)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // 로깅
        logger.error("Integrity constraint violation: {}", ex.getMessage(), ex);

        Throwable rootCause = ex.getMostSpecificCause();
        String rootMessage = rootCause != null ? rootCause.getMessage() : "";

        // CustomException으로 래핑하여 사용자에게 반환
        CustomException customException = new CustomException(rootMessage);

        // 공통 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),  // 409 Conflict 상태 코드 반환
                customException.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // SQLException 처리 (모든 SQL 관련 예외 처리)
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handleSQLException(SQLException ex) {
        // 로깅
        logger.error("SQL error occurred: {}", ex.getMessage(), ex);

        // CustomException으로 래핑하여 사용자에게 반환
        CustomException customException = new CustomException("데이터베이스 오류가 발생했습니다: " + ex.getMessage());

        // 공통 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // 기본으로 500 (Internal Server Error)
                customException.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // BadSqlGrammderException 처리 (SQL 문법 오류 처리)
    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<Object> handleBadSqlGrammderException(SQLException ex) {
        // 로깅
        logger.error("SQL error occurred: {}", ex.getMessage(), ex);

        // CustomException으로 래핑하여 사용자에게 반환
        CustomException customException = new CustomException("SQL 문법 오류가 발생했습니다: " + ex.getMessage());

        // 공통 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // 기본으로 500 (Internal Server Error)
                customException.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 유효성 검사 실패 예외 처리(MethodArgumentNotValidException)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // 로깅
        logger.error("Validation error: {}", ex.getMessage(), ex);

        // 공통 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Validation failed");

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex, WebRequest request) {
        // 로깅
        logger.error("Null pointer exception occurred: {}", ex.getMessage(), ex);

        // 공통 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "NullPointerException 발생: " + ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // 로깅
        logger.error("An error occurred: {}", ex.getMessage(), ex);

        // 공통 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred: " + ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}