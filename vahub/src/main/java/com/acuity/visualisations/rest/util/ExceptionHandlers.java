package com.acuity.visualisations.rest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author ksnd199
 */
@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionHandlers  {

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleAccessDeniedException() {
        // Nothing to do
    }
    
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public String handleIllegalArgumentException(HttpServletRequest req, Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Invalid argument to REST method", ex);
        final Class<?> returnType = ex.getParameter().getMethod().getReturnType();
        if (returnType == null) {
            return null;
        }
        if (returnType.isArray()) {
            return new Object[0];
        }
        if (List.class.isAssignableFrom(returnType) || Set.class.isAssignableFrom(returnType)) {
            return Collections.emptyList();
        }
        if (Map.class.isAssignableFrom(returnType)) {
            return Collections.emptyMap();
        }
        return new Object();
    }
}
