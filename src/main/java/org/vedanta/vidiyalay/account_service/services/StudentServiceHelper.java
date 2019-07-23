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

package org.vedanta.vidiyalay.account_service.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.vedanta.vidiyalay.account_service.ApplicationConfig;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.enums.AdmissionStatus;
import org.vedanta.vidiyalay.utils.BasicResponse;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public interface StudentServiceHelper {
    StudentNewAdmissionVM getStudentDetails(Long enrolmentNo);

    StudentNewAdmissionVM findByEnrolmentNoAndValidateAdmissionStatus(Long enrolmentNo);

    List<StudentNewAdmissionVM> findByParameters(Long enrolmentNo, Integer standard, Integer year, String name, AdmissionStatus admissionStatus, String fatherName, String motherName);

    @NotNull
    Flux<StudentNewAdmissionVM> findByStandard(int standard);

}

@Component
@Slf4j
class StudentServiceHelperImpl implements StudentServiceHelper {

    private final ApplicationConfig applicationConfig;
    private final RestTemplate restTemplate;

    StudentServiceHelperImpl(ApplicationConfig applicationConfig, RestTemplate restTemplate) {
        this.applicationConfig = applicationConfig;
        System.out.println(this.applicationConfig);
        this.restTemplate = restTemplate;
    }

    @Override
    public StudentNewAdmissionVM getStudentDetails(Long enrolmentNo) {

        StudentNewAdmissionVM studentNewAdmissionVM = null;
        try {
            final ApplicationConfig.Path path = applicationConfig.getServices().get("student-service");
            final String queryAStudentData = path.getPath() + path.getUris().get("QUERY_A_STUDENT_DATA")+ "/" + enrolmentNo;

            ParameterizedTypeReference<BasicResponse<StudentNewAdmissionVM>> typeRef
                    = new ParameterizedTypeReference<BasicResponse<StudentNewAdmissionVM>>() {
            };
            ResponseEntity<BasicResponse<StudentNewAdmissionVM>> responseEntity = restTemplate.exchange(
                    queryAStudentData, HttpMethod.GET, null, typeRef);

            if(responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
                log.error("Cannot get student data : {}",responseEntity.getStatusCode() +":"+
                        responseEntity.getBody().getMessage());
                return null;
            }

            studentNewAdmissionVM = responseEntity.getBody().getData().get(0);

        } catch (HttpClientErrorException e) {
            log.error("Cannot get data from student service.");
            log.error("Status: {}, Error: {}, Message: {}", e.getRawStatusCode(), e.getStatusText(), e.getCause());
        }

        return studentNewAdmissionVM;
    }

    @Override
    public StudentNewAdmissionVM findByEnrolmentNoAndValidateAdmissionStatus(Long enrolmentNo) {
        return Optional.ofNullable(getStudentDetails(enrolmentNo))
                .filter(e -> e.getAdmissionStatus() != AdmissionStatus.TERMINATED)
                .orElse(null);
    }

    @Override
    public List<StudentNewAdmissionVM> findByParameters(Long enrolmentNo,
                                                        Integer standard,
                                                        Integer year,
                                                        String name,
                                                        AdmissionStatus admissionStatus,
                                                        String fatherName,
                                                        String motherName) {
        final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.add("enrolmentNo", sanitizeLong(enrolmentNo));
        multiValueMap.add("standard", sanitizeInteger(standard));
        multiValueMap.add("year", sanitizeInteger(year));
        multiValueMap.add("name", sanitizeString(name));
        multiValueMap.add("status", sanitizeAdmissionStatus(admissionStatus));
        multiValueMap.add("fatherName", sanitizeString(fatherName));
        multiValueMap.add("motherName", sanitizeString(motherName));

        List<StudentNewAdmissionVM> studentNewAdmissionVMS = Collections.emptyList();

        try {

            final ApplicationConfig.Path path = applicationConfig.getServices().get("student-service");
            final String query_a_student_data = path.getPath() + path.getUris().get("QUERY_ALL_STUDENT_DATA");

            final URI uri = UriComponentsBuilder.fromHttpUrl(query_a_student_data)
                    .queryParams(multiValueMap).build().toUri();

            studentNewAdmissionVMS = Optional.ofNullable(
                    restTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<StudentNewAdmissionVM>>() {}))
                    .map(HttpEntity::getBody)
                    .orElse(Collections.emptyList());

        } catch (HttpClientErrorException e) {
            log.error("Cannot get data from student service.");
            log.error("Cannot find repository data: {}", e.getLocalizedMessage());
        }
        return studentNewAdmissionVMS;
    }

    @Override
    public  Flux<StudentNewAdmissionVM> findByStandard(int standard) {
        return WebClient.create("https://vedanta-student-service.herokuapp.com")
            .get()
            .uri("/api/query-student/details/students?standard=1")
            .retrieve().bodyToFlux(StudentNewAdmissionVM.class);
    }

    private String sanitizeAdmissionStatus(AdmissionStatus admissionStatus) {
        return Optional.ofNullable(admissionStatus).map(Enum::name).orElse(Strings.EMPTY);
    }

    private String sanitizeString(String stringParam) {
        return Optional.ofNullable(stringParam)
                .orElse(Strings.EMPTY);
    }

    private String sanitizeInteger(Integer intParam) {
        return Optional.ofNullable(intParam)
                .map(String::valueOf)
                .orElse(Strings.EMPTY);
    }

    private String sanitizeLong(Long longParam) {
        return Optional.ofNullable(longParam)
                .map(String::valueOf)
                .orElse(Strings.EMPTY);
    }
}

@Configuration
class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}