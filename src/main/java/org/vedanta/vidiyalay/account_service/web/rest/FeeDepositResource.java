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

package org.vedanta.vidiyalay.account_service.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vedanta.vidiyalay.account_service.facade.FeeDepositFacade;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountTransactionVo;
import org.vedanta.vidiyalay.utils.ResponseUtils;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/fee-deposit")
public class FeeDepositResource {

    private final FeeDepositFacade feeDetailsFacade;

    public FeeDepositResource(FeeDepositFacade feeDetailsFacade) {
        this.feeDetailsFacade = feeDetailsFacade;
    }

    @PostMapping("/deposit")
    public ResponseEntity depositFee(@RequestBody @Valid final AccountTransactionVo accountTransactionVo) throws IllegalAccessException, InstantiationException {
        return Optional.ofNullable(feeDetailsFacade.depositFee(accountTransactionVo))
                .map(e -> ResponseEntity.ok().body(ResponseUtils.getBasicResponse(e, HttpStatus.OK)))
                .orElseThrow(() -> new RuntimeException("Cannot deposit fees now pls try again letter!"));
    }

}
