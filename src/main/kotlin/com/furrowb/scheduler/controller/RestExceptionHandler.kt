package com.furrowb.scheduler.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException

data class ExceptionResponse(val status: Int, val errorMessage: String?)

@ControllerAdvice
class RestExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(EntityExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected fun entityExists(ex: EntityExistsException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(ExceptionResponse(HttpStatus.CONFLICT.value(), ex.message), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected fun entityDoesNotExist(ex: EntityNotFoundException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(ExceptionResponse(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)
    }
}