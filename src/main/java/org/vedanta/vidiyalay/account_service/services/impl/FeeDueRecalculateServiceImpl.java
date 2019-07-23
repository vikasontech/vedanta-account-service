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

package org.vedanta.vidiyalay.account_service.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.vedanta.vidiyalay.account_service.domain.AccountMasterEntity;
import org.vedanta.vidiyalay.account_service.message.borker.MessagePublisherHelper;
import org.vedanta.vidiyalay.account_service.message.borker.PatternTopics;
import org.vedanta.vidiyalay.account_service.repository.AccountMasterRepository;
import org.vedanta.vidiyalay.account_service.repository.AccountTransactionRepository;
import org.vedanta.vidiyalay.account_service.services.FeeDueRecalculateService;
import org.vedanta.vidiyalay.account_service.services.FineServiceHelper;
import org.vedanta.vidiyalay.account_service.services.QueryFeeDetailService;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.enums.AdmissionStatus;
import org.vedanta.vidiyalay.utils.CloneObjects;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Component
@Slf4j
public class FeeDueRecalculateServiceImpl implements FeeDueRecalculateService {
  private final AccountMasterRepository accountMasterRepository;
  private final FeeDueRecalculateServiceImplHelper helper;
  private final MessagePublisherHelper messagePublisherHelper;

  public FeeDueRecalculateServiceImpl(AccountMasterRepository accountMasterRepository,
                                      FeeDueRecalculateServiceImplHelper helper,
                                      MessagePublisherHelper messagePublisherHelper) {
    this.accountMasterRepository = accountMasterRepository;
    this.helper = helper;
    this.messagePublisherHelper = messagePublisherHelper;
  }

  @Override
  public CompletableFuture<String> job() {
    // get total fees based on class.<FOR ALL THE CLASS 1-12>
    Map<Integer, BigDecimal> feeDetails = helper.getFeeDetails();

    // get total fee payed for this year for that student <FOR ALL THE ACCOUNTS>
    for (AccountMasterEntity e : accountMasterRepository.findAll()) {
      helper.calculateDueFeeForAStudent(feeDetails, e);
    }
    // create events to initiate terminate student process
    messagePublisherHelper.triggerEvents(PatternTopics.TRIGGER_PROCESS_TERMINATION_PROCESS);

    return CompletableFuture.completedFuture("Process Completed!");
  }


  @Override
  public String getProcessName() {
    return "FEE_RECALCULATOR";
  }

}

class StudentNotFoundException extends RuntimeException {

  public StudentNotFoundException(Long enrolmentNo) {
    super(getMessage(enrolmentNo));
  }

  public StudentNotFoundException(Long enrolmentNo, Throwable cause) {
    super(getMessage(enrolmentNo), cause);
  }

  private static String getMessage(Long enrolmentNo) {
    return "Student with enrolment no: " + enrolmentNo + " not found.";
  }
}

@Component
@Slf4j
class FeeDueRecalculateServiceImplHelper {
  private final QueryFeeDetailService queryFeeDetailService;
  private final AccountMasterRepository accountMasterRepository;
  private final StudentServiceHelper studentServiceHelper;
  private final AccountTransactionRepository accountTransactionRepository;
  private final FineServiceHelper fineServiceHelper;
  private final MessagePublisherHelper messagePublisherHelper;

  FeeDueRecalculateServiceImplHelper(QueryFeeDetailService queryFeeDetailService,
                                     AccountMasterRepository accountMasterRepository,
                                     StudentServiceHelper studentServiceHelper,
                                     AccountTransactionRepository accountTransactionRepository,
                                     FineServiceHelper fineServiceHelper, MessagePublisherHelper messagePublisherHelper) {
    this.queryFeeDetailService = queryFeeDetailService;
    this.accountMasterRepository = accountMasterRepository;
    this.studentServiceHelper = studentServiceHelper;
    this.accountTransactionRepository = accountTransactionRepository;
    this.fineServiceHelper = fineServiceHelper;
    this.messagePublisherHelper = messagePublisherHelper;
  }

  void calculateDueFeeForAStudent(Map<Integer, BigDecimal> feeDetails, AccountMasterEntity e) {
    Supplier<StudentNotFoundException> studentNotFoundExceptionSupplier = () -> new StudentNotFoundException(e.getEnrolmentNo());

    final StudentNewAdmissionVM studentNewAdmissionVM =
        Optional.ofNullable(studentServiceHelper.getStudentDetails(e.getEnrolmentNo()))
            .orElse(null);

    if (ObjectUtils.isEmpty(studentNewAdmissionVM) ||
        studentNewAdmissionVM.getAdmissionStatus().equals(AdmissionStatus.TERMINATED)) {
      return;
    }

    final Integer admissionClass
        = studentNewAdmissionVM.getAdmissionClass();

    Optional<BigDecimal> paidFee = accountTransactionRepository.getSumOfFeePaid(e.getEnrolmentNo());

    BigDecimal totalFee = feeDetails.get(admissionClass);
    BigDecimal feeDue = totalFee.subtract(paidFee.orElse(BigDecimal.ZERO));

    final @NotNull BigDecimal totalFine = fineServiceHelper.calculateTotalFineAmount(e.getEnrolmentNo());

    final Map<String, Object> params = new CloneObjects.ParameterMap()
        .put("dueAmount", feeDue)
        .put("totalFee", totalFee)
        .put("totalFine", totalFine).build();

    CloneObjects.clone(e, AccountMasterEntity.class, params)
        .ifPresent(accountMasterRepository::save);

    // update due status in student detail table.
    final StudentNewAdmissionVM updateStudentNewAdmissionVM = Optional.ofNullable(studentServiceHelper.findByEnrolmentNoAndValidateAdmissionStatus(e.getEnrolmentNo()))
        .orElseThrow(studentNotFoundExceptionSupplier);

    updateStudentNewAdmissionVM.setFeeDue(isFeeDue(e, feeDue));
    messagePublisherHelper.publishMessageToTopic(PatternTopics.UPDATE_STUDENT_DETAILS,
        updateStudentNewAdmissionVM);
  }

  private boolean isFeeDue(AccountMasterEntity e, BigDecimal feeDue) {
    return (!(feeDue.compareTo(BigDecimal.ZERO) <= 0 &&
        e.getTotalFine().compareTo(BigDecimal.ZERO) <= 0));
  }

  Map<Integer, BigDecimal> getFeeDetails() {
    Map<Integer, BigDecimal> feeDetails = new HashMap<>();
    IntStream.range(1, 13)
        .forEach(e -> feeDetails.put(e, getTotalFee(e)));
    return feeDetails;
  }

  private BigDecimal getTotalFee(int e) {
    BigDecimal value = BigDecimal.ZERO;
    try {
      value = queryFeeDetailService.getTotalFee(e, 2019).orElse(BigDecimal.ZERO);
    } catch (Exception exp) {
      log.error(exp.getMessage());
    }
    return value;
  }
}
