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

package org.vedanta.vidiyalay.account_service.services;

import org.vedanta.vidiyalay.account_service.web.rest.vm.AccountTransactionVo;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public interface FeeDepositService {

    BigDecimal depositFee(@NotNull final AccountTransactionVo accountTransactionVo,
                          @NotNull StudentNewAdmissionVM studentDetailsVM,
                          @NotNull BigDecimal totalDueFee) throws InstantiationException, IllegalAccessException;

}
