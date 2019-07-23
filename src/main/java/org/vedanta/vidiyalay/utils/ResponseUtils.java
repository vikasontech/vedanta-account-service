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

package org.vedanta.vidiyalay.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

public class ResponseUtils {

    private ResponseUtils() {
        //do nothing
    }

    public static BasicResponse getBasicResponse(Object o, HttpStatus status){
        return BasicResponse.builder()
                .status(status.value())
                .data(Arrays.asList(o))
                .message("success")
                .build();

    }

    public static BasicResponse getBasicResponse(List<Object> o, HttpStatus status){
        return BasicResponse.builder()
                .status(status.value())
                .message("success")
                .data(o)
                .build();

    }

    public static ResponseEntity<BasicResponse> commonResponse(Object o){

        return ResponseEntity.ok(ResponseUtils.getBasicResponse(o, HttpStatus.OK));
    }
}
