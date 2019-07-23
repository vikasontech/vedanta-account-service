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
import org.springframework.transaction.annotation.Transactional;
import org.vedanta.vidiyalay.account_service.domain.AccountTransactionEntity;
import org.vedanta.vidiyalay.account_service.message.borker.MessagePublisherHelper;
import org.vedanta.vidiyalay.account_service.repository.AccountTransactionRepository;
import org.vedanta.vidiyalay.account_service.services.FeeDepositService;
import org.vedanta.vidiyalay.account_service.services.UpdateFeeDetailService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountTransactionVo;
import org.vedanta.vidiyalay.account_service.web.rest.vm.EmailVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.utils.Utility;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class FeeDepositServiceImpl implements FeeDepositService {

    private final AccountTransactionRepository accountTransactionRepository;
    private final UpdateFeeDetailService updateFeeDetailService;
    private final MessagePublisherHelper messagePublisherHelper;

    public FeeDepositServiceImpl(AccountTransactionRepository accountTransactionRepository,
                                 UpdateFeeDetailService updateFeeDetailService, MessagePublisherHelper messagePublisherHelper) {
        this.accountTransactionRepository = accountTransactionRepository;
        this.updateFeeDetailService = updateFeeDetailService;
        this.messagePublisherHelper = messagePublisherHelper;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public BigDecimal depositFee(@NotNull final AccountTransactionVo accountTransactionVo,
                          @NotNull StudentNewAdmissionVM studentDetailsVM,
                          @NotNull BigDecimal totalDueFee) throws InstantiationException, IllegalAccessException{


//        calculate balance amount and update fee due status in student details
        final BigDecimal balanceAmount = updateFeeDetailService.calculateAndUpdateFeeDueStatus(accountTransactionVo, totalDueFee);

        // log fee deposit transaction details in fee transaction
        accountTransactionRepository.save(
                AccountTransactionEntity.builder()
                        .amount(accountTransactionVo.getAmount())
                        .dateOfTransaction(Utility.getCurrentDateTime())
                        .enrolmentNo(accountTransactionVo.getEnrolmentNo())
                        .instrumentNo(accountTransactionVo.getInstrumentNo())
                        .transactionMode(accountTransactionVo.getTransactionMode())
                        .build());


        // send email notification to the parents.
        messagePublisherHelper.publishToMailTopic(EmailVM.builder()
                .to(studentDetailsVM.getEmail())
                .templateFile("fee-deposit-notification-mail.html")
                .params(getMailTemplateParams(studentDetailsVM, balanceAmount, accountTransactionVo.getAmount()))
                .subject("Fees is deposited!")
                .build());

        return balanceAmount;
    }

    private Map<String, Object> getMailTemplateParams(final StudentNewAdmissionVM studentNewAdmissionVM, final BigDecimal balanceAmount, final BigDecimal amount) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", studentNewAdmissionVM.getName());
        params.put("fatherName", studentNewAdmissionVM.getFatherName());
        params.put("enrolmentNo", studentNewAdmissionVM.getId());
        params.put("balanceAmount", balanceAmount);
        params.put("depositAmount", amount);
        return params;
    }

}
