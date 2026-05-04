package com.portfolio.backend.controller;

import com.portfolio.backend.dto.request.CompareRequest;
import com.portfolio.backend.dto.response.CompareResponse;
import com.portfolio.backend.service.ComparisonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compare")
@CrossOrigin(origins = "*")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @PostMapping
    public CompareResponse compare(@RequestBody CompareRequest request) {
        return comparisonService.compare(request);
    }
}