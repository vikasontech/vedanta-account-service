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

package org.vedanta.vidiyalay.account_service.facade.impl;

import org.springframework.stereotype.Component;
import org.vedanta.vidiyalay.account_service.facade.QueryFeesFacade;
import org.vedanta.vidiyalay.account_service.services.QueryFeesService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.FeeDetailsVM;

import java.util.List;

@Component
public class QueryFeesFacadeImpl implements QueryFeesFacade {
    private final QueryFeesService queryFeesService;

    public QueryFeesFacadeImpl(QueryFeesService queryFeesService) {
        this.queryFeesService = queryFeesService;
    }

    @Override
    public List<FeeDetailsVM> findAll() {
        return queryFeesService.findAll();
    }
}
