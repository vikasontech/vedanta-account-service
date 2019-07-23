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

package org.vedanta.vidiyalay.account_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.vedanta.vidiyalay.account_service.domain.AccountMasterEntity;

import java.util.List;


public interface AccountMasterRepository extends CrudRepository<AccountMasterEntity, Long> {
    AccountMasterEntity findByEnrolmentNo(Long enrolmentNo);
    List<AccountMasterEntity> findAll();

}
