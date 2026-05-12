package com.software.craft.lille.train_kata.api.resource.advice;

import com.software.craft.lille.train_kata.api.resource.TicketOfficeResource;
import com.software.craft.lille.train_kata.ticket_office.exception.UnreachableData;
import com.software.craft.lille.train_kata.ticket_office.exception.UnusableData;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = TicketOfficeResource.class)
public class TicketOfficeResourceAdvice {
  private static final Logger logger = LoggerFactory.getLogger(TicketOfficeResourceAdvice.class);

  @ExceptionHandler(UnusableData.class)
  public ProblemDetail handleUnusableData(UnusableData problem, HttpServletRequest request) {
    return toProblemDetail(problem, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnreachableData.class)
  public ProblemDetail handleUnreachableData(UnreachableData problem, HttpServletRequest request) {
    return toProblemDetail(problem, request, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private <T extends RuntimeException> ProblemDetail toProblemDetail(
      T problem, HttpServletRequest request, HttpStatus httpStatus) {
    final String requestURI = request.getRequestURI();
    final String message = problem.getMessage();
    final String cause =
        Optional.ofNullable(problem.getCause()).map(Throwable::getMessage).orElse("");
    logger.error(
        "Error resolving {}.{}{}{}{}",
        requestURI,
        System.lineSeparator(),
        message,
        System.lineSeparator(),
        cause);

    final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message);
    problemDetail.setTitle(problem.getClass().getSimpleName());
    problemDetail.setDetail(problem.getMessage());
    problemDetail.setType(URI.create(requestURI));
    return problemDetail;
  }
}
