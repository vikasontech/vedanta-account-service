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

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vedanta.vidiyalay.account_service.services.CreateStudentAccountScheduledService;
import org.vedanta.vidiyalay.account_service.services.FeeDueRecalculateService;
import org.vedanta.vidiyalay.utils.BasicResponse;
import org.vedanta.vidiyalay.utils.ResponseUtils;

@RestController
@Slf4j
@RequestMapping("/api/fee-due-recalculater-resource")
public class FeeDueRecalculateResource {
    private final FeeDueRecalculateService feeDueRecalculatorService;
    private final CreateStudentAccountScheduledService createStudentAccountScheduledService;

    public FeeDueRecalculateResource(FeeDueRecalculateService feeDueRecalculatorService, CreateStudentAccountScheduledService createStudentAccountScheduledService) {
        this.feeDueRecalculatorService = feeDueRecalculatorService;
        this.createStudentAccountScheduledService = createStudentAccountScheduledService;
    }

    @GetMapping("/recalculate")
    public BasicResponse sync() {
        final long start = System.currentTimeMillis();
        createStudentAccountScheduledService.job();
        log.info("Total time [[[{}]]", System.currentTimeMillis()-start);

        feeDueRecalculatorService.job();
        return ResponseUtils.getBasicResponse("Success", HttpStatus.OK);
    }
}

