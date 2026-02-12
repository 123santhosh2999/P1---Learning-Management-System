package com.example.lms.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "System", description = "System endpoints")
public class HealthController {

  @GetMapping("/health")
  @Operation(summary = "Health", description = "Simple health check")
  public Map<String, Object> health() {
    return Map.of("ok", true);
  }
}
