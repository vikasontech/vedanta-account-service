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
import org.vedanta.vidiyalay.account_service.domain.mapper.AccountEntityMapper;
import org.vedanta.vidiyalay.account_service.facade.AccountFacade;
import org.vedanta.vidiyalay.account_service.facade.QueryFeeDetailFacade;
import org.vedanta.vidiyalay.account_service.services.AccountService;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.utils.Utility;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountFacadeImpl implements AccountFacade {
    private final StudentServiceHelper studentServiceHelper;
    private final QueryFeeDetailFacade queryFeeDetailFacade;
    private final AccountService accountService;
    private final AccountEntityMapper accountEntityMapper;

    public AccountFacadeImpl(StudentServiceHelper studentServiceHelper, QueryFeeDetailFacade queryFeeDetailFacade, AccountService accountService, AccountEntityMapper accountEntityMapper) {
        this.studentServiceHelper = studentServiceHelper;
        this.queryFeeDetailFacade = queryFeeDetailFacade;
        this.accountService = accountService;
        this.accountEntityMapper = accountEntityMapper;
    }

    @Override
    public AccountMasterVM createNewAccountByAccountMasterData(@NotNull AccountMasterVM accountMasterVM) {
        final StudentNewAdmissionVM studentDetails = Optional.ofNullable(studentServiceHelper.getStudentDetails(accountMasterVM.getEnrolmentNo()))
                .orElseThrow(() -> new IllegalArgumentException("Student not found for enrolment : " + accountMasterVM.getEnrolmentNo()));
        return createAccountFromStudentAdmissionDetails(studentDetails);
    }

    @Override
    public AccountMasterVM createAccountFromStudentAdmissionDetails(@NotNull StudentNewAdmissionVM studentDetails) {
        final BigDecimal totalFeeForAdmissionClass = getTotalFeeForAdmissionClass(studentDetails.getAdmissionClass());
        return accountService.createNewAccount(studentDetails, totalFeeForAdmissionClass);
    }

    @Override
    public AccountMasterVM updateAccount(AccountMasterVM accountMasterVM) {
        return accountService.updateAccount(accountMasterVM);
    }

    @Override
    public AccountMasterVM getAccountDetails(Long enrolmentNo) {
        return accountService.getAccountDetails(enrolmentNo);
    }

    @Override
    public AccountMasterVM getAccountDetailsV2(Long enrolmentNo) {
        return accountService.getAccountDetailsV2(enrolmentNo);
    }

    @Override
    public void createAccountListener(StudentNewAdmissionVM studentNewAdmissionVM) {
        createAccountFromStudentAdmissionDetails(studentNewAdmissionVM);
    }

    @Override
    public List<AccountMasterVM> findAll() {
        return Optional.ofNullable(accountService.findAll())
                .map(e -> e.stream()
                        .map(accountEntityMapper::toVm)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private BigDecimal getTotalFeeForAdmissionClass(Integer admissionClass) {
        return queryFeeDetailFacade.getTotalFee(admissionClass, Utility.getSessionYear())
                .orElseThrow(() -> new IllegalArgumentException("Fee master record not found"));
    }


}

