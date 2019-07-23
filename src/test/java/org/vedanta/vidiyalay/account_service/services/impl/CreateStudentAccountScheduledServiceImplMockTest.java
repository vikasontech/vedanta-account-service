package org.vedanta.vidiyalay.account_service.services.impl;

import io.github.benas.randombeans.api.EnhancedRandom;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.vedanta.vidiyalay.account_service.facade.AccountFacade;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class CreateStudentAccountScheduledServiceImplMockTest {
  @Mock
  StudentServiceHelper studentServiceHelper;

//  @Mock
//  AccountService accountService;

  @Mock
  AccountFacade accountFacade;

  @Before
  public void prepareTest() {
    prepareMock();
  }

  @Test
  public void job() {
    //steps
    // find class details
    // find all student in that class
    // check if the account exists or not?
    // create account if not exists

    Flux.range(1, 2)
        .subscribeOn(Schedulers.newElastic("p3"))
        .parallel(3)
        .flatMap(e -> studentServiceHelper.findByStandard(e))
        .map(this::createAccount)
        .sequential()
        .blockLast();
  }

  private Object createAccount(StudentNewAdmissionVM e) {
    final AccountMasterVM accountMasterVM = Optional.ofNullable(accountFacade.getAccountDetails(e.getId()))
        .orElseGet(() -> accountFacade.createAccountFromStudentAdmissionDetails(e));
    log.info("Account Master: {}", accountMasterVM);
    return accountMasterVM;
  }


  private void prepareMock() {
    final List<StudentNewAdmissionVM> studentNewAdmissionVMS = EnhancedRandom.randomListOf(5, StudentNewAdmissionVM.class);
    Mockito.when(studentServiceHelper.findByStandard(1))
        .thenReturn(Flux.fromIterable(studentNewAdmissionVMS));
    Mockito.when(studentServiceHelper.findByStandard(2))
        .thenReturn(Flux.empty());


    Mockito.when(accountFacade.getAccountDetails(studentNewAdmissionVMS.get(1).getId()))
        .thenReturn(EnhancedRandom.random(AccountMasterVM.class));
    final AccountMasterVM newAccountMasterDAta = EnhancedRandom.random(AccountMasterVM.class);
    Mockito.when(accountFacade.createAccountFromStudentAdmissionDetails(ArgumentMatchers.any(StudentNewAdmissionVM.class)))
        .thenReturn(newAccountMasterDAta);

  }

}