package com.BE.controller;


import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import com.BE.service.interfaceServices.IPointsHistoryService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/points")
@SecurityRequirement(name = "api")
public class PointsController {

    @Autowired
    ResponseHandler responseHandler;

    @Autowired
    IPointsHistoryService iPointsHistoryService;
    @GetMapping()
    public ResponseEntity getUserPoints() {
        return responseHandler.response(200, "Get Points success!", iPointsHistoryService.getUserPoints());
    }

    @GetMapping("history")
    public ResponseEntity getPointsHistory(@RequestParam(required = false) BookingTypeEnum bookingTypeEnum,
                                           @RequestParam(required = false) PointChangeType pointChangeType) {
        return responseHandler.response(200, "Get Points History success!", iPointsHistoryService.getPointsHistory(bookingTypeEnum, pointChangeType));
    }

}
