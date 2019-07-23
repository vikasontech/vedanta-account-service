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
import org.springframework.stereotype.Service;
import org.vedanta.vidiyalay.account_service.facade.AccountFacade;
import org.vedanta.vidiyalay.account_service.services.CreateStudentAccountScheduledService;
import org.vedanta.vidiyalay.account_service.services.StudentServiceHelper;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CreateStudentAccountScheduledServiceImpl implements CreateStudentAccountScheduledService {

  private final StudentServiceHelper studentServiceHelper;

  private final AccountFacade accountFacade;

  public CreateStudentAccountScheduledServiceImpl(StudentServiceHelper studentServiceHelper, AccountFacade accountFacade) {
    this.studentServiceHelper = studentServiceHelper;
    this.accountFacade = accountFacade;
  }

  @Override
  public String getProcessName() {
    return this.getClass().getName();
  }

  @Override
  public CompletableFuture<String> job() {
    //steps
    // find class details
    // find all student in that class
    // check if the account exists or not?
    // create account if not exists
    Flux.range(1, 12)
        .subscribeOn(Schedulers.newElastic("p3"))
        .parallel(3)
        .flatMap(e -> studentServiceHelper.findByStandard(e))
        .map(this::createAccount)
        .sequential()
        .blockLast();

    return CompletableFuture.completedFuture("done!");
  }

  private Mono<AccountMasterVM> createAccount(StudentNewAdmissionVM e) {
    return Optional.ofNullable(accountFacade.getAccountDetailsV2(e.getId()))
        .map(Mono::just)
        .orElseGet(() ->  Mono.just(accountFacade.createAccountFromStudentAdmissionDetails(e)));
  }

}

