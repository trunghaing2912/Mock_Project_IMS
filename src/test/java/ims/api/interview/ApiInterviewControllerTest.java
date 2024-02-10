package ims.api.interview;

import fa.training.fjb04.ims.api.interview.ApiInterviewController;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.interview.InterviewController;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import fa.training.fjb04.ims.service.jobs.JobService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.interview.InterviewCandidateDTO;
import fa.training.fjb04.ims.util.dto.interview.InterviewJobDTO;
import fa.training.fjb04.ims.util.dto.interview.InterviewerListDTO;
import fa.training.fjb04.ims.util.page.PageInterviewer;
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

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiInterviewController.class)
public class ApiInterviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private InterviewerService interviewerService;

    @MockBean
    private CandidatesService candidatesService;

    @MockBean
    private JobService jobService;

    @MockBean
    private RecruiterService recruiterService;

    @MockBean
    private CandidateStatusService candidateStatusService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private InterviewController interviewController;

    @Nested
    @DisplayName("Find Interviewer by ID And Paging")
    class FindByInterviewerId {

        @Nested
        @DisplayName("When request interviewer not found")
        class WhenRequestedInterviewerNotFound {

            private static final Integer INTERVIEWER_ID = 1;

            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                when(interviewerService.findById(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/interview/interviewer/{id}", INTERVIEWER_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested interviewer is found")
        class WhenRequestedInterviewerFound {

            private static final Integer INTERVIEWER_ID = 1;
            private static final String INTERVIEWER_NAME = "Nguyen Trung Hai";
            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                InterviewerListDTO interviewerListDTO = new InterviewerListDTO(INTERVIEWER_NAME);
                List<InterviewerListDTO> list = List.of(interviewerListDTO);
                when(interviewService.getPageInterviewer(anyInt())).thenReturn(new PageInterviewer<>(list));

                resultActions = mockMvc.perform(get("/api/interview/interviewer/{id}", INTERVIEWER_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions.andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found interviewer as JSON")
            void shouldReturnInformationOfFoundInterviewerAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found interviewer")
            void shouldReturnInformationOfFoundInterviewer() throws Exception {
                resultActions.andExpect(jsonPath("$.interviewers[0].name", equalTo(INTERVIEWER_NAME)));
            }
        }
    }

    @Nested
    @DisplayName("Find Candidate by ID And Paging")
    class FindByCandidateId {

        @Nested
        @DisplayName("When request candidate not found")
        class WhenRequestedCandidateNotFound {

            private static final Integer CANDIDATE_ID = 1;

            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                when(candidatesService.findByCandidateId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/interview/candidate/{id}", CANDIDATE_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested candidate is found")
        class WhenRequestedInterviewerFound {

            private static final Integer CANDIDATE_ID = 1;
            private static final String CANDIDATE_NAME = "Nguyen Trung Hai";
            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                InterviewCandidateDTO interviewCandidateDTO = new InterviewCandidateDTO(CANDIDATE_NAME);
                when(interviewService.getPageCandidate(CANDIDATE_ID)).thenReturn(interviewCandidateDTO);
                resultActions = mockMvc.perform(get("/api/interview/candidate/{id}", CANDIDATE_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions.andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found candidate as JSON")
            void shouldReturnInformationOfFoundInterviewerAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found candidate")
            void shouldReturnInformationOfFoundInterviewer() throws Exception {
                resultActions.andExpect(jsonPath("$.name", equalTo(CANDIDATE_NAME)));
            }
        }
    }


    @Nested
    @DisplayName("When request job not found")
    class WhenRequestJobNotFound {

        private static final Integer JOB_ID = 1;

        @BeforeEach
        public void mockService() {
            UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            when(jobService.findByJobId(any())).thenThrow(new RuntimeException());
        }

        @Test
        @DisplayName("Should return HTTP response which has an empty response body")
        public void shoudReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
            mockMvc.perform(get("/api/interview/job/{id}", JOB_ID))
                    .andExpect(content().string(""));
        }

    }

    @Nested
    @DisplayName("When the request candidate is found")
    class WhenRequestedJobFound {

        private static final Integer JOB_ID = 1;
        private static final String JOB_NAME = "Nguyen Trung Hai";
        private static ResultActions resultActions;

        @BeforeEach
        public void mockService() throws Exception {
            UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            InterviewJobDTO interviewJobDTO = new InterviewJobDTO(JOB_NAME);
            when(interviewService.getPageJob(JOB_ID)).thenReturn(interviewJobDTO);
            resultActions = mockMvc.perform(get("/api/interview/job/{id}", JOB_ID));
        }

        @Test
        @DisplayName("Should return the HTTP status code ok (200)")
        void shouldReturnHttpStatusCodeOk()  throws Exception {
            resultActions.andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return the information of the found job as JSON")
        void shouldReturnInformationFoundJobAsJSON() throws Exception {
            resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Shoud return the information of the found job")
        void shouldReturnInformationOfFoundJob() throws Exception {
            resultActions.andExpect(jsonPath("$.name", equalTo(JOB_NAME)));
        }

    }

}
