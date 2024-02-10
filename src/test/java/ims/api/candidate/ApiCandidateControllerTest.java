package ims.api.candidate;

import fa.training.fjb04.ims.api.candidates.ApiCandidateController;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.candidate.CandidateController;
import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.PositionService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;
import fa.training.fjb04.ims.util.dto.jobs.LevelListDTO;
import fa.training.fjb04.ims.util.page.Page;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ApiCandidateController.class)
public class ApiCandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidatesService candidatesService;

    @MockBean
    private RecruiterService recruiterService;

    @MockBean
    private PositionService positionService;

    @InjectMocks
    private CandidateController CandidateController;


    @Nested
    @DisplayName("Find Position by ID and Paging")
    class FindById {
        private final Integer POSITION_ID = 1;

        @Nested
        @DisplayName("when request position not found ")
        class WhenRequestedUserNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(candidatesService.findByCandidateId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/candidate/position/{id}", POSITION_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested user is found")
        class WhenRequestedUserFound {
            private static final Integer POSITION_ID = 1;
            private static final String POSITION_NAME = "admin";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                LevelListDTO levelListDTO = new LevelListDTO(POSITION_NAME);
                when(candidatesService.getPagePosition(anyInt())).thenReturn(new CandidatePositionDTO(POSITION_NAME));

                resultActions = mockMvc.perform(get("/api/candidates/position/{id}", POSITION_ID));
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
            @DisplayName("Should return the information of the found skill")
            void shouldReturnInformationOfFoundPosition() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.positionName",
                                equalTo(POSITION_NAME))
                        );
            }
        }
    }


    @Nested
    @DisplayName("Find Position by ID and Paging")
    public class FindLevelById {
        private final Integer POSITION_ID = 1;

        @Nested
        @DisplayName("when request Position not found ")
        class WhenRequestedPositionNotFound {

            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(candidatesService.findByCandidateId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/candidates/recruiter/{id}", POSITION_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested recruiter is found")
        class WhenRequestedRecruiterFound {
            private static final Integer RECRUITER_ID = 1;
            private static final String RECRUITER_NAME = "Fresher";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                LevelListDTO levelListDTO = new LevelListDTO(RECRUITER_NAME);

                when(candidatesService.getPageRecruiter(anyInt())).thenReturn(new CandidateRecruiterDTO(RECRUITER_NAME));

                resultActions = mockMvc.perform(get("/api/candidates/recruiter/{id}", RECRUITER_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found recruiter as JSON")
            void shouldReturnInformationOfFoundLevelAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found recruiter")
            void shouldReturnInformationOfFoundSkill() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.recruiterName",
                                equalTo(RECRUITER_NAME))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Page of candidate")
    public class PageOfCandidate {

        @Nested
        @DisplayName("When candidate list is empty")
        public class WhenCandidateListIsEmpty {

            @BeforeEach
            public void mockService() {

                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(candidatesService.findByCandidateId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return status 200 and empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {

                Integer totalPage = 5;
                Integer pageIndex = 1;
                Integer pageSize = 7;
                String role = "ADMIN";
                String search = "Open";
                String field = "Status";

                List<CandidatesListDTO> candidatesListDTOS = Collections.emptyList();
                Page<CandidatesListDTO> candidatesListDTOPage = new Page<>(totalPage, pageIndex, candidatesListDTOS, pageSize, role);

                when(candidatesService.getPageCandidates(pageIndex, pageSize, search, field)).thenReturn(candidatesListDTOPage);

                mockMvc.perform(get("/api/candidates")
                                .param("pageIndex", String.valueOf(pageIndex))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("search", search)
                                .param("field", field))
                        .andExpect(jsonPath("$.data", hasSize(0)));
            }
        }
    }
        @Nested
        @DisplayName("When Candidate found")
        public class WhenJobFound {

            @BeforeEach
            public void mockService() {

                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(candidatesService.findByCandidateId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return a page of candidate with search filter ")
            public void shouldReturnPageOfJob() throws Exception {

                Integer totalPage = 5;
                Integer pageIndex = 1;
                Integer pageSize = 7;
                String role = "ADMIN";
                String search = "Open";
                String field = "Status";

                List<CandidatesListDTO> candidatesListDTOS = Arrays.asList(new CandidatesListDTO(1,"Nguyen Van A","021821921012","duya@gmail.com",new CandidateStatus(1,"Open")),
                        new CandidatesListDTO(2, "Nguyen Thanh Thang", "012321821123", "thanhthang@gmail.com", new CandidateStatus(2,"Open")));
                Page<CandidatesListDTO> candidatesListDTOPage = new Page<>(totalPage, pageIndex, candidatesListDTOS, pageSize, role);

                when(candidatesService.getPageCandidates(pageIndex, pageSize, search, field)).thenReturn(candidatesListDTOPage);
                mockMvc.perform(get("/api/candidates")
                                .param("pageIndex", String.valueOf(pageIndex))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("search", search)
                                .param("field", field))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].candidateStatus.statusName", containsString(search)))
                        .andExpect(jsonPath("$.data[1].candidateStatus.statusName", containsString(search)));
            }


        }
    }
