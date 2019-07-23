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
import org.vedanta.vidiyalay.account_service.domain.mapper.FeeDetailsEntityMapper;
import org.vedanta.vidiyalay.account_service.repository.FeesDetailsRepository;
import org.vedanta.vidiyalay.account_service.services.QueryFeesService;
import org.vedanta.vidiyalay.account_service.web.rest.vm.FeeDetailsVM;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryFeesServiceImpl implements QueryFeesService {
    private final FeesDetailsRepository feesDetailsRepository;
    private final FeeDetailsEntityMapper mapper;

    public QueryFeesServiceImpl(FeesDetailsRepository feesDetailsRepository, FeeDetailsEntityMapper mapper) {
        this.feesDetailsRepository = feesDetailsRepository;
        this.mapper = mapper;
    }

    @Override
    public List<FeeDetailsVM> findAll() {
        return feesDetailsRepository.findAll()
                .stream()
                .map(mapper::toVM)
                .collect(Collectors.toList());
    }}
