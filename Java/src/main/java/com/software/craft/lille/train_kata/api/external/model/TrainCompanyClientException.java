package com.software.craft.lille.train_kata.api.external.model;

import org.springframework.http.HttpRequest;
import org.springframework.http.ProblemDetail;

public class TrainCompanyClientException extends RuntimeException {
  public TrainCompanyClientException(HttpRequest request, ProblemDetail problem) {
    super(
        "Request '%s %s' failed: %s"
            .formatted(request.getMethod(), request.getURI(), problem.toString()));
  }
}
