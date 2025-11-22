package com.supplyboost.accounting.controller;

import com.supplyboost.accounting.service.RevenueRecognitionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/revenue")
@RequiredArgsConstructor
public class RevenueController {

  private final RevenueRecognitionService revenueRecognitionService;

  @GetMapping("/total")
  public ResponseEntity<BigDecimal> getTotalRecognizedRevenue(
      @RequestParam String startDate, @RequestParam String endDate) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime start = LocalDateTime.parse(startDate, formatter);
    LocalDateTime end = LocalDateTime.parse(endDate, formatter);

    log.info("Getting total recognized revenue from {} to {}", startDate, endDate);
    BigDecimal total = revenueRecognitionService.getTotalRecognizedRevenue(start, end);

    return ResponseEntity.ok(total);
  }
}
