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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.vedanta.vidiyalay.account_service.facade.FeeDepositFacade;
import org.vedanta.vidiyalay.account_service.facade.QueryFeeDetailFacade;
import org.vedanta.vidiyalay.account_service.services.FeeDepositService;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountTransactionVo;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Validated
public class FeeDepositFacadeImpl implements FeeDepositFacade {
    private final StudentServiceHelper studentServiceHelper;
    private final FeeDepositService feeDepositService;
    private final QueryFeeDetailFacade queryFeeDetailFacade;


    public FeeDepositFacadeImpl(StudentServiceHelper studentServiceHelper,
                                FeeDepositService feeDepositService,
                                QueryFeeDetailFacade queryFeeDetailFacade) {

        this.studentServiceHelper = studentServiceHelper;
        this.feeDepositService = feeDepositService;
        this.queryFeeDetailFacade = queryFeeDetailFacade;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public BigDecimal depositFee(AccountTransactionVo accountTransactionVo) throws InstantiationException, IllegalAccessException {

        return Optional.ofNullable(studentServiceHelper.getStudentDetails(accountTransactionVo.getEnrolmentNo()))
                .map(e -> depositFee(accountTransactionVo, e))
                .orElse(null);
    }

    private BigDecimal depositFee(AccountTransactionVo accountTransactionVo, StudentNewAdmissionVM studentNewAdmissionVM) {
        final BigDecimal totalDueFee = queryFeeDetailFacade.getTotalDueFee(accountTransactionVo.getEnrolmentNo());
        try {
            return feeDepositService.depositFee(accountTransactionVo, studentNewAdmissionVM, totalDueFee);
        } catch (InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
