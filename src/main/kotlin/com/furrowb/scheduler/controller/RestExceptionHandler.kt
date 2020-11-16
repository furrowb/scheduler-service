package com.furrowb.scheduler.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.persistence.EntityExistsException
import javax.validation.ConstraintViolationException

data class ExceptionResponse(val status: Int, val errorMessage: String?)

@ControllerAdvice
class RestExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected fun validationErrors(ex: ConstraintViolationException): ResponseEntity<ExceptionResponse> {
        val message = ex.constraintViolations.map { "${it.propertyPath}: ${it.message}\n" }.toString()
        return ResponseEntity(ExceptionResponse(HttpStatus.BAD_REQUEST.value(), message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EntityExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected fun entityExists(ex: EntityExistsException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(ExceptionResponse(HttpStatus.CONFLICT.value(), ex.message), HttpStatus.CONFLICT)
    }
}