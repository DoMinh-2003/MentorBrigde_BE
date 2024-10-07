package com.BE.service.implementServices;

import com.BE.model.request.ScheduleRequest;
import com.BE.repository.TimeFrameRepository;
import com.BE.service.interfaceServices.ITimeFrameService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TimeFrameImpl implements ITimeFrameService {

    @Autowired
    TimeFrameRepository timeFrameRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    DateNowUtils dateNowUtils;


    @Override
    public Object createSchedule(ScheduleRequest scheduleRequest) {
        return null;
    }
}
