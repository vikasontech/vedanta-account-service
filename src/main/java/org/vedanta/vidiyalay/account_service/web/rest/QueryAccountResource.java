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

package org.vedanta.vidiyalay.account_service.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vedanta.vidiyalay.account_service.facade.AccountFacade;
import org.vedanta.vidiyalay.utils.BasicResponse;
import org.vedanta.vidiyalay.utils.ResponseUtils;

import java.util.Optional;

@RequestMapping("/api/query-account")
@RestController
public class QueryAccountResource {

    private final AccountFacade accountFacade;

    public QueryAccountResource(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @GetMapping("/details")
    public ResponseEntity<BasicResponse> findAll() {
        return Optional.ofNullable(accountFacade.findAll())
                .map(e -> ResponseEntity.ok().body(ResponseUtils.getBasicResponse(e, HttpStatus.OK)))
                .orElse(ResponseEntity.notFound().build());
    }
}
