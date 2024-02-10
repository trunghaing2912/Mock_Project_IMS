package ims.api.jobs;

import fa.training.fjb04.ims.api.jobs.ApiJobsController;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.job.JobController;
import fa.training.fjb04.ims.enums.job.JobStatus;
import fa.training.fjb04.ims.service.jobs.JobService;
import fa.training.fjb04.ims.util.dto.jobs.JobsListDTO;
import fa.training.fjb04.ims.util.dto.jobs.LevelListDTO;
import fa.training.fjb04.ims.util.dto.jobs.SkillListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageLevel;
import fa.training.fjb04.ims.util.page.PageSkill;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiJobsController.class)
public class ApiJobControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    @Nested
    @DisplayName("Find Skill by ID and Paging")
    class FindById {
        private final Integer SKILL_ID = 1;

        @Nested
        @DisplayName("when request skill not found ")
        class WhenRequestedSkillNotFound {

            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(jobService.findByJobId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/jobs/skills/{id}", SKILL_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested skill is found")
        class WhenRequestedSkillFound {
            private static final Integer SKILL_ID = 1;
            private static final String SKILL_NAME = "Java";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                SkillListDTO skillListDTO = new SkillListDTO(SKILL_NAME);
                List<SkillListDTO> lst = List.of(skillListDTO);
                when(jobService.getPageSkill(anyInt())).thenReturn(new PageSkill<>(lst));

                resultActions = mockMvc.perform(get("/api/jobs/skills/{id}", SKILL_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found skill as JSON")
            void shouldReturnInformationOfFoundSkillAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found skill")
            void shouldReturnInformationOfFoundSkill() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.skills[0].name",
                                equalTo(SKILL_NAME))
                        );
            }
        }
    }

    @Nested
    @DisplayName("Find Level by ID and Paging")
    public class FindLevelById {
        private final Integer SKILL_ID = 1;

        @Nested
        @DisplayName("when request level not found ")
        class WhenRequestedSkillNotFound {

            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(jobService.findByJobId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/jobs/levels/{id}", SKILL_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested level is found")
        class WhenRequestedSkillFound {
            private static final Integer LEVEL_ID = 1;
            private static final String LEVEL_NAME = "Fresher";

            private static ResultActions resultActions;

            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                LevelListDTO levelListDTO = new LevelListDTO(LEVEL_NAME);
                List<LevelListDTO> lst = List.of(levelListDTO);
                when(jobService.getPageLevel(anyInt())).thenReturn(new PageLevel<>(lst));

                resultActions = mockMvc.perform(get("/api/jobs/levels/{id}", LEVEL_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found level as JSON")
            void shouldReturnInformationOfFoundLevelAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            @Test
            @DisplayName("Should return the information of the found level")
            void shouldReturnInformationOfFoundSkill() throws Exception {
                resultActions
                        .andExpect(jsonPath("$.levels[0].name",
                                equalTo(LEVEL_NAME))
                        );
            }
        }

    }

    @Nested
    @DisplayName("Page of jobs")
    public class PageOfJob {

        @Nested
        @DisplayName("When jobs list is empty")
        public class WhenJobListIsEmpty{

            @BeforeEach
            public void mockService() {

                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(jobService.findByJobId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return status 200 and empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {

                Integer totalPage = 5;
                Integer pageIndex = 1;
                Integer pageSize = 7;
                String role = "ADMIN";
                String search = "OPEN";
                String field = "Status";

                List<JobsListDTO> jobsListDTOS = Collections.emptyList();
                Page<JobsListDTO> jobsListDTOPage = new Page<>(totalPage, pageIndex, jobsListDTOS, pageSize, role);

                when(jobService.getPageJob(pageIndex, pageSize, search, field)).thenReturn(jobsListDTOPage);

                mockMvc.perform(get("/api/jobs")
                                .param("pageIndex", String.valueOf(pageIndex))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("search", search)
                                .param("field", field))
                        .andExpect(jsonPath("$.data", hasSize(0)));
            }
        }

        @Nested
        @DisplayName("When Jobs found")
        public class WhenJobFound {

            @BeforeEach
            public void mockService() {

                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(jobService.findByJobId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return a page of jobs with search filter ")
            public void shouldReturnPageOfJob() throws Exception {

                Integer totalPage = 5;
                Integer pageIndex = 1;
                Integer pageSize = 7;
                String role = "ADMIN";
                String search = "OPEN";
                String field = "Status";

                List<JobsListDTO> jobsListDTOS = Arrays.asList(new JobsListDTO(1,"Fresher Java", LocalDate.of(2024,01,03), JobStatus.OPEN,LocalDate.of(2024,01,05)),
                        new JobsListDTO(2,"Junior Java", LocalDate.of(2024,01,03), JobStatus.OPEN,LocalDate.of(2024,01,05)));
                Page<JobsListDTO> jobsListDTOPage = new Page<>(totalPage, pageIndex, jobsListDTOS, pageSize, role);

                when(jobService.getPageJob(pageIndex, pageSize, search, field)).thenReturn(jobsListDTOPage);
                mockMvc.perform(get("/api/jobs")
                                .param("pageIndex", String.valueOf(pageIndex))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("search", search)
                                .param("field", field))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].status", containsString(search)))
                        .andExpect(jsonPath("$.data[1].status", containsString(search)));
            }

        }

    }

}

