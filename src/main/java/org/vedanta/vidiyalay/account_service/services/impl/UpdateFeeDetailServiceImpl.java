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

package org.vedanta.vidiyalay.account_service.services.impl;

import org.springframework.stereotype.Component;
import org.vedanta.vidiyalay.account_service.domain.mapper.FeeDetailsEntityMapper;
import org.vedanta.vidiyalay.account_service.message.borker.MessagePublisherHelper;
import org.vedanta.vidiyalay.account_service.message.borker.PatternTopics;
import org.vedanta.vidiyalay.account_service.repository.FeesDetailsRepository;
import org.vedanta.vidiyalay.account_service.services.AccountService;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.services.UpdateFeeDetailService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountTransactionVo;
import org.vedanta.vidiyalay.account_service.web.rest.vm.FeeDetailsVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.utils.CloneObjects;

import java.math.BigDecimal;

@Component
public class UpdateFeeDetailServiceImpl implements UpdateFeeDetailService {
    private final AccountService accountService;
    private final StudentServiceHelper studentServiceHelper;
    private final MessagePublisherHelper messagePublisherHelper;
    private final FeeDetailsEntityMapper mapper;
    private final FeesDetailsRepository feesDetailsRepository;


    public UpdateFeeDetailServiceImpl(AccountService accountService, StudentServiceHelper studentServiceHelper, MessagePublisherHelper messagePublisherHelper, FeeDetailsEntityMapper mapper, FeesDetailsRepository feesDetailsRepository) {
        this.accountService = accountService;
        this.studentServiceHelper = studentServiceHelper;
        this.messagePublisherHelper = messagePublisherHelper;
        this.mapper = mapper;
        this.feesDetailsRepository = feesDetailsRepository;
    }


    @Override
    public BigDecimal calculateAndUpdateFeeDueStatus(AccountTransactionVo accountTransactionVo, BigDecimal totalDueFee) throws IllegalAccessException, InstantiationException {

        final AccountMasterVM accountDetails = accountService.getAccountDetails(accountTransactionVo.getEnrolmentNo());
        // update new due amount in account master
        final BigDecimal balanceAmount = updateNewDueAmountInMaster(accountDetails,
                accountTransactionVo.getAmount(), totalDueFee);

        // if no fee due then update fee dew flag in student details
        updateFeeDueStatus(accountTransactionVo.getEnrolmentNo(), balanceAmount, accountDetails.getTotalFine());

        return balanceAmount;
    }


    private void updateFeeDueStatus(final Long enrolmentNo, final BigDecimal balanceAmount, final BigDecimal totalFineAmount) {
        final StudentNewAdmissionVM studentNewAdmissionVM = studentServiceHelper.getStudentDetails(enrolmentNo);

        studentNewAdmissionVM.setFeeDue(balanceAmount.compareTo(BigDecimal.ZERO) > 0 ||
                totalFineAmount.compareTo(BigDecimal.ZERO) > 0);

        // send message to update student details
        messagePublisherHelper.publishMessageToTopic(PatternTopics.UPDATE_STUDENT_DETAILS,
                studentNewAdmissionVM);

    }

    @Override
    public FeeDetailsVM createFeeDetail(FeeDetailsVM feeDetailsVM) {
//        create new fee details records
        return mapper.toVM(feesDetailsRepository.save(mapper.toEntity(feeDetailsVM)));
    }


    private BigDecimal updateNewDueAmountInMaster(final AccountMasterVM accountDetails, final BigDecimal depositAmount,
                                                  final BigDecimal totalDueFee) throws InstantiationException, IllegalAccessException {
        // calculate new due fee
        final BigDecimal balanceAmount = calculateBalanceAmount(depositAmount, totalDueFee);

        CloneObjects.clone(accountDetails, AccountMasterVM.class,
                new CloneObjects.ParameterMap().put("dueAmount", balanceAmount).build())
                .ifPresent(accountService::updateAccount);

        return balanceAmount;
    }

    private BigDecimal calculateBalanceAmount(final BigDecimal depositAmount, BigDecimal totalDueFee) {
        // calculate new due fee
        return totalDueFee.subtract(depositAmount);
    }

}
