package searchengine.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import searchengine.dto.statistics.IndexingErrorResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<IndexingErrorResult> handle(Exception ex) {
        IndexingErrorResult indexingErrorResult = new IndexingErrorResult();
        indexingErrorResult.setError(ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(indexingErrorResult);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<IndexingErrorResult> handle1() {
        IndexingErrorResult indexingErrorResult = new IndexingErrorResult();
        indexingErrorResult.setError("неверный запрос");
        return ResponseEntity.internalServerError()
                .body(indexingErrorResult);
    }

}