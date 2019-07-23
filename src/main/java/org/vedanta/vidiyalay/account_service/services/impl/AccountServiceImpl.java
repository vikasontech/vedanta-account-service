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
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.vedanta.vidiyalay.account_service.domain.AccountMasterEntity;
import org.vedanta.vidiyalay.account_service.domain.mapper.AccountEntityMapper;
import org.vedanta.vidiyalay.account_service.enums.AccountType;
import org.vedanta.vidiyalay.account_service.repository.AccountMasterRepository;
import org.vedanta.vidiyalay.account_service.services.AccountService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.utils.Utility;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@Validated
public class AccountServiceImpl implements AccountService {

    private final AccountMasterRepository accountMasterRepository;
    private final AccountEntityMapper accountEntityMapper;

    public AccountServiceImpl(AccountMasterRepository accountMasterRepository,
                              AccountEntityMapper accountEntityMapper) {
        this.accountMasterRepository = accountMasterRepository;
        this.accountEntityMapper = accountEntityMapper;
    }

    @Override
    public AccountMasterVM createNewAccount(StudentNewAdmissionVM studentDetails,
                                            BigDecimal totalFeeForAdmissionClass) {
        return createAccountEngine(studentDetails, totalFeeForAdmissionClass);
    }

    @Override
    public AccountMasterVM updateAccount(AccountMasterVM accountMasterVM) {
        Assert.notNull(accountMasterVM.getId(),
                String.format("Account Id is blank for enrolment: %s cannot update record", accountMasterVM.getEnrolmentNo()));
        return saveNewAccountMasterData(accountMasterVM, "Cannot process, try again letter.");
    }

    @Override
    public AccountMasterVM getAccountDetails(Long enrolmentNo)  {
        return Optional.ofNullable(accountMasterRepository.findByEnrolmentNo(enrolmentNo))
                .map(accountEntityMapper::toVm)
                .orElseThrow(() -> new RuntimeException("Account not found for enrolment: " + enrolmentNo));

    }


    @Override
    public AccountMasterVM getAccountDetailsNoError(Long enrolmentNo) {
        return Optional.ofNullable(accountMasterRepository.findByEnrolmentNo(enrolmentNo))
                .map(accountEntityMapper::toVm)
                .orElse(null);
    }

    @Override
    public List<AccountMasterEntity> findAll() {
        return accountMasterRepository.findAll();
    }

  @Override
  public AccountMasterVM getAccountDetailsV2(Long enrolmentNo) {
    return Optional.ofNullable(accountMasterRepository.findByEnrolmentNo(enrolmentNo))
        .map(accountEntityMapper::toVm)
        .orElse(null);

  }

  private AccountMasterVM createAccountEngine(final StudentNewAdmissionVM studentNewAdmissionVM,
                                                final BigDecimal totalFeeForAdmissionClass) {

        // check if account already exists
        throwErrorIfAccountAlreadyExists(studentNewAdmissionVM.getId());

        final AccountMasterVM newAccountMasterVM = getNewAccountMasterVM(studentNewAdmissionVM, totalFeeForAdmissionClass);

        return saveNewAccountMasterData(newAccountMasterVM, "cannot create account");
    }


    private void throwErrorIfAccountAlreadyExists(Long enrolmentNo) {
        if (!(ObjectUtils.isEmpty(accountMasterRepository.findByEnrolmentNo(enrolmentNo)))) {
            throw new IllegalArgumentException("Account already exists");
        }
    }

    private AccountMasterVM getNewAccountMasterVM(StudentNewAdmissionVM studentNewAdmissionVM, BigDecimal totalDueFee) {
        return AccountMasterVM.builder()
                .totalFee(totalDueFee)
                .dueAmount(totalDueFee)
                .accountType(AccountType.STUDENT)
                .dateOfOpening(Utility.getCurrentDateTime()).enrolmentNo(studentNewAdmissionVM.getId())
                .name(studentNewAdmissionVM.getName())
                .totalFine(BigDecimal.ZERO).build();
    }

    private AccountMasterVM saveNewAccountMasterData(AccountMasterVM newAccountMasterVM, String s) {
        return Optional.of(accountMasterRepository
                .save(accountEntityMapper.toEntity(newAccountMasterVM)))
                .map(accountEntityMapper::toVm)
                .orElseThrow(() -> new RuntimeException(s));
    }

}
