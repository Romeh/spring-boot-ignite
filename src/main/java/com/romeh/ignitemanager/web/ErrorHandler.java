package com.romeh.ignitemanager.web;


import com.romeh.ignitemanager.entities.CustomErrorResponse;
import com.romeh.ignitemanager.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic error handling mechanism.
 *
 * @author romih
 */
@ControllerAdvice
public class ErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

	private enum ERROR_CODE {
		E0001
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)  // 404
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	public CustomErrorResponse handleNotFound(ResourceNotFoundException ex) {
		LOGGER.warn("Entity was not found", ex);
		return new CustomErrorResponse(ERROR_CODE.E0001.name(), ex.getMessage());
	}

}
