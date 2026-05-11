package com.software.craft.lille.train_kata.api.resource.advice;

import com.software.craft.lille.train_kata.api.resource.TicketOfficeResource;
import com.software.craft.lille.train_kata.ticket_office.exception.UnusableData;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = TicketOfficeResource.class)
public class TicketOfficeResourceAdvice {
  private static final Logger logger = LoggerFactory.getLogger(TicketOfficeResourceAdvice.class);

  private static <T extends RuntimeException> ProblemDetail toProblemDetail(
      T problem, HttpServletRequest request, HttpStatus httpStatus) {
    final String requestURI = request.getRequestURI();
    final String message = problem.getMessage();
    logger.error("Error resolving {}.{}{}", requestURI, System.lineSeparator(), message);

    final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message);
    problemDetail.setTitle(problem.getClass().getSimpleName());
    problemDetail.setDetail(problem.getMessage());
    problemDetail.setType(URI.create(requestURI));
    return problemDetail;
  }

  @ExceptionHandler(UnusableData.class)
  public ProblemDetail handleReportingProblem(UnusableData problem, HttpServletRequest request) {
    return toProblemDetail(problem, request, HttpStatus.BAD_REQUEST);
  }
}
