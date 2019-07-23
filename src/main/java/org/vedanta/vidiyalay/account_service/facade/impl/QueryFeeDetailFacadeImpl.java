/*
 * Copyright (C) 2019  Vikas Kumar Verma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.vedanta.vidiyalay.account_service.facade.impl;

import org.springframework.stereotype.Component;
import org.vedanta.vidiyalay.account_service.facade.QueryFeeDetailFacade;
import org.vedanta.vidiyalay.account_service.services.QueryFeeDetailService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.FeeDetailsVM;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class QueryFeeDetailFacadeImpl implements QueryFeeDetailFacade {

    private final QueryFeeDetailService queryFeeDetailService;

    public QueryFeeDetailFacadeImpl(QueryFeeDetailService queryFeeDetailService) {
        this.queryFeeDetailService = queryFeeDetailService;
    }

    @Override
    public BigDecimal getTotalDueFee(Long enrolmentNo) {
        return queryFeeDetailService.getTotalDueFee(enrolmentNo);
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
    public void calculateDueFee(FeeDetailsVM feeDetailsVM) {
        queryFeeDetailService.calculateDueFee(feeDetailsVM);
    }
}
