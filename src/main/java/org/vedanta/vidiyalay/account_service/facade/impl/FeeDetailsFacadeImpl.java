/*
 *     Copyright (C) 2019  Vikas Kumar Verma
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.vedanta.vidiyalay.account_service.facade.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.vedanta.vidiyalay.account_service.events.CustomApplicationEvents;
import org.vedanta.vidiyalay.account_service.events.EventType;
import org.vedanta.vidiyalay.account_service.facade.FeeDetailsFacade;
import org.vedanta.vidiyalay.account_service.services.QueryFeeDetailService;
import org.vedanta.vidiyalay.account_service.services.UpdateFeeDetailService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.FeeDetailsVM;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class FeeDetailsFacadeImpl implements FeeDetailsFacade {

    private final UpdateFeeDetailService updateFeeDetailService;
    private final QueryFeeDetailService queryFeeDetailService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public FeeDetailsFacadeImpl(UpdateFeeDetailService updateFeeDetailService, QueryFeeDetailService queryFeeDetailService, ApplicationEventPublisher applicationEventPublisher) {
        this.updateFeeDetailService = updateFeeDetailService;
        this.queryFeeDetailService = queryFeeDetailService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public List<FeeDetailsVM> getFeeDetails(int standard, int year) {
        return queryFeeDetailService.getFeeDetails(standard, year);
    }

    @Override
    public Optional<BigDecimal> getTotalFee(int standard, int year) {
        return queryFeeDetailService.getTotalFee(standard, year);
    }

    @Override
    public FeeDetailsVM createFeeDetail(final FeeDetailsVM feeDetailsVM) {
        final FeeDetailsVM feeDetail = updateFeeDetailService.createFeeDetail(feeDetailsVM);
        // create new event
        applicationEventPublisher.publishEvent(new CustomApplicationEvents(feeDetailsVM, EventType.FEE_MASTER_RECORD_CREATED));
        return feeDetail;
    }

    @EventListener(CustomApplicationEvents.class)
    @Async
    @Override
    public void onFeeMasterRecordCreated(final CustomApplicationEvents customApplicationEvents){
        final FeeDetailsVM feeDetailsVM = customApplicationEvents.getSourceFromEvent(EventType.FEE_MASTER_RECORD_CREATED, FeeDetailsVM.class);
//        // send message to sync the due fee status
        if(feeDetailsVM == null){
            return;
        }
        queryFeeDetailService.calculateDueFee(feeDetailsVM);
    }
}


@Configuration
class AsynchronousSpringEventsConfig {
    @Bean
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}