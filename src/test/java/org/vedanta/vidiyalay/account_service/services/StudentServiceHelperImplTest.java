package org.vedanta.vidiyalay.account_service.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.vedanta.vidiyalay.account_service.ApplicationConfig;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;
import org.vedanta.vidiyalay.account_service.web.rest.vm.enums.AdmissionStatus;
import org.vedanta.vidiyalay.utils.BasicResponse;
import org.vedanta.vidiyalay.utils.ResponseUtils;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringRunner.class)
public class StudentServiceHelperImplTest {
//
//  @InjectMocks
//  private StudentServiceHelperImpl studentServiceHelper;
//
//  @Mock
//  private ApplicationConfig applicationConfig;
//
//  @Mock
//  private RestTemplate restTemplate;
//
//  @Test
//  public void getStudentDetails() {
//
//    mockApplicationConfig();
//
//    final StudentNewAdmissionVM studentNewAdmissionVM = new StudentNewAdmissionVM();
//    studentNewAdmissionVM.setId(1L);
//    final BasicResponse basicResponse = ResponseUtils.getBasicResponse(studentNewAdmissionVM, HttpStatus.OK);
//    Mockito.when(restTemplate.exchange(
//        anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
//        .thenReturn(ResponseEntity.ok(basicResponse));
//
//    final StudentNewAdmissionVM studentDetails = studentServiceHelper.getStudentDetails(1L);
//    Assert.notNull(studentDetails, "Student details is null");
//    Assert.notNull(studentDetails.getId(), "ID is null");
//    Assert.isTrue(studentDetails.getId().equals(1L), "ID is not 1");
//
//  }
//
//  private void mockApplicationConfig() {
//    ApplicationConfig.Path path = new ApplicationConfig.Path();
//    path.setPath("http://dummy");
//    final HashMap<String, String> uris = new HashMap<>();
//    uris.put("QUERY_A_STUDENT_DATA", "/dummypath");
//    path.setUris(uris);
//
//    final HashMap<String, ApplicationConfig.Path> serviceMap = new HashMap<>();
//    serviceMap.put("student-service", path);
//    Mockito.when(applicationConfig.getServices())
//        .thenReturn(serviceMap);
//  }
//
//
//  @Test
//  public void findByParameters() {
//
//    Long enrolmentNo = 1L;
//    Integer standard = null;
//    Integer year = null;
//    String name = null;
//    AdmissionStatus admissionStatus = null;
//    String fatherName = null;
//    String motherName = null;
//
//    mockApplicationConfig();
//    final List<StudentNewAdmissionVM> response = Collections.singletonList(new StudentNewAdmissionVM());
//
//    Mockito.when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
//        .thenReturn(ResponseEntity.ok(response));
//
//    final List<StudentNewAdmissionVM> studentNewAdmissionVMS = studentServiceHelper.findByParameters(enrolmentNo,
//        standard, year, name, admissionStatus, fatherName, motherName);
//
//    Assert.notEmpty(studentNewAdmissionVMS, "No data found");
//
//  }

  @Test
  public void findByStandard() {

    final Flux<StudentNewAdmissionVM> studentNewAdmissionVMFlux = WebClient.create("https://vedanta-student-service.herokuapp.com")
        .get()
        .uri("/api/query-student/details/students?standard=1")
        .retrieve().bodyToFlux(StudentNewAdmissionVM.class);

    studentNewAdmissionVMFlux
        .log()
        .subscribe(e -> System.out.println("Data found: "+ e.toString()));
  }
}