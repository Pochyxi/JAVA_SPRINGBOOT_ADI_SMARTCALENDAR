package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.smartMonthPlanDto.SmartMonthPlanDTOV2;
import com.adi.smartcalendar.web.service.service.SmartMonthPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/smartPlan")
public class SmartMonthPlanController {

    private final SmartMonthPlanService smartMonthPlanService;

    @GetMapping(value = "/month/{month}/year/{year}/v2")
    public ResponseEntity<SmartMonthPlanDTOV2>getSmartMonthPlanDTOV2( @PathVariable int month,
                                                                      @PathVariable int year){
        return new ResponseEntity<>(smartMonthPlanService.getSmartMonthPlanV2(month, year), HttpStatus.OK);
    }

}
