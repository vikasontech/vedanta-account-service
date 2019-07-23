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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vedanta.vidiyalay.account_service.facade.AccountFacade;
import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountMasterVM;
import org.vedanta.vidiyalay.utils.BasicResponse;
import org.vedanta.vidiyalay.utils.ResponseUtils;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/create-account")
@Validated
public class CreateAccountResource {

    private final AccountFacade accountFacade;

    public CreateAccountResource(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @PostMapping("/account")
    public ResponseEntity<BasicResponse> create(@RequestBody @Valid AccountMasterVM accountMasterVM) {
        return Optional.ofNullable(ResponseUtils.getBasicResponse(
                accountFacade.createNewAccountByAccountMasterData(accountMasterVM), HttpStatus.OK))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.unprocessableEntity().build());

    }
}
