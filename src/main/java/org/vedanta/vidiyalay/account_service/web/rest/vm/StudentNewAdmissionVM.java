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

package org.vedanta.vidiyalay.account_service.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.vedanta.vidiyalay.account_service.web.rest.vm.enums.AdmissionStatus;
import org.vedanta.vidiyalay.account_service.web.rest.vm.enums.Gender;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class StudentNewAdmissionVM implements Serializable {
    private Long id;
    @NotNull(message = "{error.field.mandatory}")
    private String name;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    @NotNull(message = "{error.field.mandatory}")
    private Date dateOfBirth;
    private Date dateOfAdmission;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private Date dateOfTermination;
    private String religion;
    @NotNull(message = "{error.field.mandatory}")
    private String caste;
    private String subcaste;
    @NotNull(message = "{error.field.mandatory}")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotNull(message = "{error.field.mandatory}")
    private String fatherName;
    private String motherName;
    private String occupation;
    private String relation;
    @NotNull(message = "{error.field.mandatory}")
    private String address1;
    @NotNull(message = "{error.field.mandatory}")
    private String pin1;
    private String address2;
    private String pin2;
    private String phone1;
    private String phone2;
    private String mobile;
    private String email;
    private String bloodGroup;
    private String chechak;
    private String nationality;
    private String motherTongue;
    private String areaOfInterest;
    private Integer numberOfSiblings;
    private String lastSchoolName;
    private Integer lastRank;
    private Integer lastClassYear;
    private Integer lastClass;
    private String board;
    private Integer scored;
    private Integer totalMarks;
    private String reasonForLeave;
    private String transferCertificate;
    private String lastSchoolMarkSheet;
    private String conduct;
    @NotNull(message = "{error.field.mandatory}")
    private Integer admissionClass;
    private Integer currentClass;
    private boolean isWithdrawal;
    private boolean isFeeDue;
    @NotNull(message = "{error.field.mandatory}")
    private String instrumentNo;
    private AdmissionStatus admissionStatus;
}

