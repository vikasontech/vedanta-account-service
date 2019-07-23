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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vedanta.vidiyalay.account_service.domain.AccountMasterEntity;
import org.vedanta.vidiyalay.account_service.domain.FeeDetailsEntity;
import org.vedanta.vidiyalay.account_service.domain.mapper.FeeDetailsEntityMapper;
import org.vedanta.vidiyalay.account_service.message.borker.MessagePublisherHelper;
import org.vedanta.vidiyalay.account_service.message.borker.PatternTopics;
import org.vedanta.vidiyalay.account_service.repository.AccountMasterRepository;
import org.vedanta.vidiyalay.account_service.repository.FeesDetailsRepository;
import org.vedanta.vidiyalay.account_service.services.AccountService;
import org.vedanta.vidiyalay.account_service.services.QueryFeeDetailService;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.FeeDetailsVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.enums.AdmissionStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QueryFeeDetailServiceImpl implements QueryFeeDetailService {

    private final StudentServiceHelper studentServiceHelper;
    private final AccountService accountService;
    private final FeesDetailsRepository feesDetailsRepository;
    private final FeeDetailsEntityMapper mapper;
    private final AccountMasterRepository accountMasterRepository;
    private final MessagePublisherHelper messagePublisherHelper;

    public QueryFeeDetailServiceImpl(StudentServiceHelper studentServiceHelper,
                                     AccountService accountService,
                                     FeesDetailsRepository feesDetailsRepository,
                                     FeeDetailsEntityMapper mapper,
                                     AccountMasterRepository accountMasterRepository,
                                     MessagePublisherHelper messagePublisherHelper) {
        this.studentServiceHelper = studentServiceHelper;
        this.accountService = accountService;
        this.feesDetailsRepository = feesDetailsRepository;
        this.mapper = mapper;
        this.accountMasterRepository = accountMasterRepository;
        this.messagePublisherHelper = messagePublisherHelper;
    }

    @Override
    public BigDecimal getTotalDueFee(Long enrolmentNo) {
        return Optional.ofNullable(accountMasterRepository.findByEnrolmentNo(enrolmentNo))
                .map(AccountMasterEntity::getDueAmount)
                .orElseThrow(() -> new RuntimeException("Account not found for enrolment no:" + enrolmentNo + " !"));
    }

    @Override
    public List<FeeDetailsVM> getFeeDetails(int standard, int year) {

        return Optional.ofNullable(
                feesDetailsRepository.findAllByStandardAndYear(standard, year))
                .map(e -> e.stream()
                        .filter(FeeDetailsEntity::getIsActive)
                        .map(mapper::toVM)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<BigDecimal> getTotalFee(int standard, int year) {
        List<FeeDetailsVM> feeDetails = getFeeDetails(standard, year);
        if (feeDetails.isEmpty()) {
            throw new IllegalArgumentException("Fee details not configured for standard " + standard + " year " + year);
        }

        BigDecimal totalAmount = feeDetails.stream().map(FeeDetailsVM::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Optional.of(totalAmount);
    }

    @Override
    public void calculateDueFee(FeeDetailsVM feeDetailsVM) {

        //get all the students for that class year from student details
        final List<StudentNewAdmissionVM> studentNewAdmissionVMS =
                studentServiceHelper.findByParameters(null, feeDetailsVM.getStandard(), feeDetailsVM.getYear(),
                        null, null, null, null);

        final BigDecimal amount = getNewFeeAmount(feeDetailsVM);

        final Predicate<StudentNewAdmissionVM> studentNewAdmissionVMPredicate =
                e -> e.getAdmissionStatus() != AdmissionStatus.TERMINATED &&
                        e.getAdmissionStatus() != AdmissionStatus.TERMINATION_INITIATED;

        studentNewAdmissionVMS
                .stream()
                .filter(studentNewAdmissionVMPredicate)
                .forEach(e -> {
                    //get due fee from account master
                    AccountMasterVM accountDetails = accountService.getAccountDetailsNoError(e.getId());

                    if(accountDetails != null){
                        //if new fee is active then add amount to due fee and update account master
                        //if new fee is not active then subtract amount to due fee and update account master

                        BigDecimal newDueAmount = accountDetails.getDueAmount().add(amount);
                        accountService.updateAccount(accountDetails);

                        //if due amount is 0 then update student fee due status to false in student details
                        //else update student fee due status to true
                        updateFeeDueStatus(accountDetails.getEnrolmentNo(), newDueAmount, accountDetails.getTotalFine());

                    } else {
                        log.warn("Account details not found for enrolment: {}", e.getId());
                    }


                });
    }


    private BigDecimal getNewFeeAmount(FeeDetailsVM feeDetailsVM) {
        return feeDetailsVM.getIsActive() ? feeDetailsVM.getAmount() :
                feeDetailsVM.getAmount().multiply(BigDecimal.valueOf(-1L));
    }

    private void updateFeeDueStatus(final Long enrolmentNo, final BigDecimal balanceAmount, final BigDecimal totalFineAmount) {
        final StudentNewAdmissionVM studentNewAdmissionVM = studentServiceHelper.getStudentDetails(enrolmentNo);

        studentNewAdmissionVM.setFeeDue(balanceAmount.compareTo(BigDecimal.ZERO) > 0 ||
                totalFineAmount.compareTo(BigDecimal.ZERO) > 0);

        // send message to update student details
        messagePublisherHelper.publishMessageToTopic(PatternTopics.UPDATE_STUDENT_DETAILS,
                studentNewAdmissionVM);
    }
}
