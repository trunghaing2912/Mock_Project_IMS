package ims.api.offer;

import fa.training.fjb04.ims.api.offer.ApiGetCandidateOfferAndInterviewNoteController;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.offer.OfferController;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.enums.offer.OfferStatus;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.offer.OfferService;
import fa.training.fjb04.ims.util.dto.offer.DepartmentListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferCandidateListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferUserListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageDepartment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiGetCandidateOfferAndInterviewNoteController.class)
public class ApiOfferControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfferService offerService;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private CandidatesService candidatesService;

    @InjectMocks
    private OfferController offerController;

    @Nested
    @DisplayName("Find User by ID and Paging")
    class FindById {
        private final Integer USER_ID = 1;

        @Nested
        @DisplayName("when request user not found ")
        class WhenRequestedUserNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.findById(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/user/{id}", USER_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested user is found")
        class WhenRequestedUserFound {
            private static final Integer USER_ID = 1;
            private static final String USER_NAME = "admin";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.getPageUser(anyInt())).thenReturn(new OfferUserListDTO(USER_NAME));

                resultActions = mockMvc.perform(get("/api/offer/user/{id}", USER_ID));
            }

            @Test

            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found user as JSON")
            void shouldReturnInformationOfFoundUserAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found user")
            void shouldReturnInformationOfFoundUser() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.name",
                                equalTo(USER_NAME))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Find Candidate by ID and Paging")
    public class FindCandidateById {
        private final Integer CANDIDATE_ID = 1;

        @Nested
        @DisplayName("when request candidate not found ")
        class WhenRequestedCandidateNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.findById(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/candidateDTO/{id}", CANDIDATE_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested candidate is found")
        class WhenRequestedCandidateFound {
            private static final Integer CANDIDATE_ID = 1;
            private static final String CANDIDATE_NAME = "ADMIN";
            private static final String CANDIDATE_EMAIL = "ABC@GMAIL.COM";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.getPageCandidate(anyInt())).thenReturn(new OfferCandidateListDTO(CANDIDATE_NAME, CANDIDATE_EMAIL));

                resultActions = mockMvc.perform(get("/api/offer/candidateDTO/{id}", CANDIDATE_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found user as JSON")
            void shouldReturnInformationOfFoundUserAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found candidate")
            void shouldReturnInformationOfFoundUser() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.name",
                                equalTo(CANDIDATE_NAME))
                        )
                        .andExpect(jsonPath("$.email",
                                equalTo(CANDIDATE_EMAIL)));
            }
        }
    }

    @Nested
    @DisplayName("Find Department by ID and Paging")
    public class FindDepartmentById {
        private final Integer DEPARTMENT_ID = 1;

        @Nested
        @DisplayName("when request department not found ")
        class WhenRequestedDepartmentNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.findById(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/department/{id}", DEPARTMENT_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested department is found")
        class WhenRequestedCandidateFound {
            private static final Integer DEPARTMENT_ID = 1;
            private static final String DEPARTMENT_NAME = "ADMIN";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                DepartmentListDTO departmentListDTO = new DepartmentListDTO(DEPARTMENT_NAME);
                List<DepartmentListDTO> list = List.of(departmentListDTO);
                when(offerService.getPageDepartment(anyInt())).thenReturn(new PageDepartment<>(list));

                resultActions = mockMvc.perform(get("/api/offer/department/{id}", DEPARTMENT_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found department as JSON")
            void shouldReturnInformationOfFoundDepartmentAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found department")
            void shouldReturnInformationOfFoundDepartment() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.department[0].name",
                                equalTo(DEPARTMENT_NAME))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Page of Offer")
    public class PageOfOffer {
        @Nested
        @DisplayName("When offer list is empty")
        public class WhenOfferListIsEmpty{
            @BeforeEach
            public void mockService() {

                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.findById(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return status 200 and empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {

                Integer totalPage = 5;
                Integer pageIndex = 1;
                Integer pageSize = 7;
                String role = "ADMIN";
                String search = "OPEN";
                String department = "DEPARTMENT";
                String status = "STATUS";

                List<OfferListDTO> offerListDTOS = Collections.emptyList();
                Page<OfferListDTO> offerListDTOPage = new Page<>(totalPage, pageIndex, offerListDTOS, pageSize, role);

                when(offerService.getPageOffer(pageIndex,pageSize,search,department,status)).thenReturn(offerListDTOPage);

                mockMvc.perform(get("/api/offer")
                                .param("pageIndex", String.valueOf(pageIndex))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("search", search)
                                .param("department", department)
                                .param("status", status))
                        .andExpect(jsonPath("$.data", hasSize(0)));
            }
        }

        @Nested
        @DisplayName("When Offer found")
        public class WhenOfferFound {
            @BeforeEach
            public void mockService() {

                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(offerService.findById(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return a page of offer with search filter ")
            public void shouldReturnPageOfOffer() throws Exception {

                Integer totalPage = 5;
                Integer pageIndex = 1;
                Integer pageSize = 7;
                String role = "ADMIN";
                String search = "WAITING_FOR_APPROVAL";
                String department = "DEPARTMENT";
                String status = "STATUS";

                List<OfferListDTO> offerListDTOS = Arrays.asList(
                        new OfferListDTO(1, "note", OfferStatus.WAITING_FOR_APPROVAL),
                        new OfferListDTO(2, "note2", OfferStatus.WAITING_FOR_APPROVAL));
                Page<OfferListDTO> offerListDTOPage = new Page<>(totalPage, pageIndex, offerListDTOS, pageSize, role);

                when(offerService.getPageOffer(pageIndex,pageSize,search,department,status)).thenReturn(offerListDTOPage);
                mockMvc.perform(get("/api/offer")
                                .param("pageIndex", String.valueOf(pageIndex))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("search", search)
                                .param("department", department)
                                .param("status", status))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].offerStatus", containsString(search)))
                        .andExpect(jsonPath("$.data[1].offerStatus", containsString(search)));
            }
        }
    }

    @Nested
    @DisplayName("Find Interview by Candidate ID")
    class FindInterviewByCandidateId {
        private final Integer CANDIDATE_ID = 1;

        @Nested
        @DisplayName("when request interview not found ")
        class WhenRequestedUserNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(interviewService.getListInterviewByCandidateId(any())).thenReturn(null);
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/candidate/{id}", CANDIDATE_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested interview is found")
        class WhenRequestedInterviewFound {
            private static final String SCHEDULE_TITLE = "TITLE";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                Interview interview = new Interview();
                interview.setScheduleTitle(SCHEDULE_TITLE);
                when(interviewService.getListInterviewByCandidateId(anyInt())).thenReturn(List.of(interview));

                resultActions = mockMvc.perform(get("/api/offer/candidate/{id}", CANDIDATE_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found user as JSON")
            void shouldReturnInformationOfFoundInterviewAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found interview")
            void shouldReturnInformationOfFoundInterview() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.[0].scheduleTitle",
                                equalTo(SCHEDULE_TITLE))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Find Candidate by Candidate ID")
    class FindCandidateByCandidateId {
        private final Integer CANDIDATE_ID = 1;

        @Nested
        @DisplayName("when request candidate not found ")
        class WhenRequestedUserNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(candidatesService.findByCandidateId(any())).thenReturn(null);
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/candidateEmail/{id}", CANDIDATE_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested candidate is found")
        class WhenRequestedInterviewFound {
            private static final String FULL_NAME = "ABC";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                Candidates candidates = new Candidates();
                candidates.setFullName("ABC");
                when(candidatesService.findByCandidateId(anyInt())).thenReturn(Optional.of(candidates));

                resultActions = mockMvc.perform(get("/api/offer/candidateEmail/{id}", CANDIDATE_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found candidate as JSON")
            void shouldReturnInformationOfFoundCandidateAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found candidate")
            void shouldReturnInformationOfFoundCandidate() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.fullName",
                                equalTo(FULL_NAME))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Find Interview by Interview ID")
    class FindInterviewByInterviewId {
        private final Integer INTERVIEW_ID = 1;

        @Nested
        @DisplayName("when request interview not found ")
        class WhenRequestedInterviewNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(interviewService.findById(any())).thenReturn(null);
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/interview/{id}", INTERVIEW_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested interview is found")
        class WhenRequestedInterviewFound {
            private static final String SCHEDULE_TITLE = "TITLE";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                Interview interview = new Interview();
                interview.setScheduleTitle("TITLE");
                when(interviewService.findById(anyInt())).thenReturn(Optional.of(interview));

                resultActions = mockMvc.perform(get("/api/offer/interview/{id}", INTERVIEW_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found candidate as JSON")
            void shouldReturnInformationOfFoundInterviewAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found candidate")
            void shouldReturnInformationOfFoundInterview() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.scheduleTitle",
                                equalTo(SCHEDULE_TITLE))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Find Interviewer by Interview ID")
    class FindInterviewerByInterviewId {
        private final Integer INTERVIEW_ID = 1;

        @Nested
        @DisplayName("when request interview not found ")
        class WhenRequestedInterviewerNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(interviewService.findListInterviewerByInterviewId(any())).thenReturn(null);
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/offer/interviewer/{id}", INTERVIEW_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested interviewer is found")
        class WhenRequestedInterviewFound {
            private static final String INTERVIEWER_NAME = "NAME";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                Interviewer interviewer = new Interviewer();
                interviewer.setInterviewerName("NAME");
                when(interviewService.findListInterviewerByInterviewId(anyInt())).thenReturn(List.of(interviewer));

                resultActions = mockMvc.perform(get("/api/offer/interviewer/{id}", INTERVIEW_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found user as JSON")
            void shouldReturnInformationOfFoundInterviewerAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found interview")
            void shouldReturnInformationOfFoundInterviewer() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.[0].interviewerName",
                                equalTo(INTERVIEWER_NAME))
                        );
            }
        }
    }
}
