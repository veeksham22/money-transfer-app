package com.progressive.banking.moneytransfer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progressive.banking.moneytransfer.domain.dto.RewardHistoryResponse;
import com.progressive.banking.moneytransfer.domain.dto.RewardSummaryResponse;
import com.progressive.banking.moneytransfer.service.RewardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/summary")
    public ResponseEntity<RewardSummaryResponse> getSummary(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(rewardService.getSummary(username));
    }

    @GetMapping("/history")
    public ResponseEntity<List<RewardHistoryResponse>> getHistory(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(rewardService.getHistory(username));
    }
}
