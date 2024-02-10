package ims.controller.interview;

import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.interview.InterviewController;
import fa.training.fjb04.ims.entity.Breadcrumb.Breadcrumb;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.enums.interview.ScheduleStatus;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import fa.training.fjb04.ims.service.jobs.JobService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.interview.EditInterviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class InterviewControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

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

    @MockBean
    private BreadcrumbService breadcrumbService;

    @MockBean
    private UserRepository userRepository;

    private MockMvc mockMVC;

    private UserDetails mockUser;

    @InjectMocks
    private InterviewController interviewController;

    @Nested
    @DisplayName("Already authenticate")
    public class haveRole {

        @BeforeEach
        void setup() {
            mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                    .apply(springSecurity()).build();
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            Roles roles = new Roles();
            roles.setId(1);
            roles.setRoleName("ADMIN");
            user.setRoles(Set.of(roles));

            Department department = new Department();
            department.setDepartmentName("Test");
            user.setDepartment(department);

            when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                    .thenReturn(Optional.of(user));

            var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

            mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
        }

        @Test
        void shouldReturnOfferList() throws Exception {
            mockMVC.perform(get("/interview/list")
                            .with(user(mockUser)))
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Have no role")
    public class unAuthenticate {
        @BeforeEach
        void setup() {
            mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                    .apply(springSecurity()).build();
            User user = new User();
            user.setUsername("Candidate");
            user.setPassword("candidate");
            Roles roles = new Roles();
            roles.setId(1);
            roles.setRoleName("CANDIDATE");
            user.setRoles(Set.of(roles));

            Department department = new Department();
            department.setDepartmentName("Test");
            user.setDepartment(department);

            when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                    .thenReturn(Optional.of(user));

            var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

            mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
        }

        @Test
        void shouldReturnStatusForbidden() throws Exception {
            mockMVC.perform(get("/interview/list")
                            .with(user(mockUser)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Add Interview")
    public class addInterview {
        @Nested
        public class havePermissionToAdd {
            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("admin");
                user.setPassword("admin");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnAddInterview() throws Exception {
                List<Interviewer> interviewers = List.of(new Interviewer());
                List<Candidates> candidates = List.of(new Candidates());
                List<Jobs> jobs = List.of(new Jobs());
                List<Recruiters> recruiters = List.of(new Recruiters());

                when(interviewerService.findAll()).thenReturn(interviewers);
                when(candidatesService.findAll()).thenReturn(candidates);
                when(jobService.findAll()).thenReturn(jobs);
                when(recruiterService.findAll()).thenReturn(recruiters);

                mockMVC.perform(get("/interview/add")
                                .with(user(mockUser)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("candidatesList", candidates))
                        .andExpect(model().attribute("interviewerList", interviewers))
                        .andExpect(view().name("interview/add-new-interview"));
            }

            @Test
            void testAddFailedBindingResultHasError() throws Exception {
                mockMVC.perform(
                                post("/interview/add")
                                        .param("interviewer", "1")
                                        .with(user(mockUser)))
                        .andDo(print())
                        .andExpect(view().name("interview/add-new-interview"));
            }
        }

        @Nested
        @DisplayName("noPermission")
        public class doNotHavePermissionToAccessAddForm {
            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Interviewer");
                user.setPassword("interviewer");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("INTERVIEWER");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {
                List<Interviewer> interviewers = List.of(new Interviewer());
                List<Candidates> candidates = List.of(new Candidates());
                List<Jobs> jobs = List.of(new Jobs());
                List<Recruiters> recruiters = List.of(new Recruiters());

                when(interviewerService.findAll()).thenReturn(interviewers);
                when(candidatesService.findAll()).thenReturn(candidates);
                when(jobService.findAll()).thenReturn(jobs);
                when(recruiterService.findAll()).thenReturn(recruiters);

                mockMVC.perform(get("/interview/add")
                                .with(user(mockUser)))
                        .andDo(print())
                        .andExpect(status().isForbidden());
            }
        }
    }

    @Nested
    @DisplayName("View Interview Details")
    public class viewInterviewDetail {

        @Nested
        public class havePermissionToView {

            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();

                User user = new User();
                user.setUsername("Admin");
                user.setPassword("admin");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnViewDetailInterview() throws Exception {

                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                Breadcrumb breadcrumb = new Breadcrumb("Interview detail", "/interview/view/");
                Breadcrumb breadcrumb1 = new Breadcrumb("Interview list", "/interview/list");

                when(breadcrumbService.getBreadcrumbInterviewDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumb1);

                when(interviewService.findById(1)).thenReturn(Optional.of(Optional.of(interview).orElse(null)));

                mockMVC.perform(get("/interview/view/1")
                                .with(user(mockUser)))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("interview", interview))
                        .andExpect(view().name("interview/view-interview-details"));
            }

            @Test
            public void shouldReturnErrorPage() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                Breadcrumb breadcrumb = new Breadcrumb("Interview detail", "/interview/view/");
                Breadcrumb breadcrumb1 = new Breadcrumb("Interview list", "/interview/list");

                when(breadcrumbService.getBreadcrumbInterviewDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumb1);

                when(interviewService.findById(1)).thenReturn(Optional.of(Optional.of(interview).orElse(null)));

                mockMVC.perform(get("/interview/view/2")
                                .with(user(mockUser)))
                        .andExpect(view().name("error/404"));
            }
        }
    }

    @Nested
    @DisplayName("Edit Interview")
    public class EditInterview {

        @Nested
        public class NoPermissionToEditInterview {

            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Interviewer");
                user.setPassword("interviewer");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("INTERVIEWER");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                mockMVC.perform(get("/interview/edit/1")
                                .with(user(mockUser)))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToEditJob {

            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Admin");
                user.setPassword("admin");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("HR");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusOkToAccessEditInterview() throws Exception {

                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                EditInterviewDTO editInterviewDTO = new EditInterviewDTO();

                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Interview", "/interview/edit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewEdit()).thenReturn(breadcrumbEdit);

                mockMVC.perform(get("/interview/edit/1")
                                .param("prevPage", "list")
                                .with(user(mockUser)))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("breadCrumbList", breadcrumbList))
                        .andExpect(view().name("interview/edit-interview-details"));
            }

            @Test
            public void shouldReturnStatusOkToAccessEditInterviewWithPrevPageNotEqualToList() throws Exception {

                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                EditInterviewDTO editInterviewDTO = new EditInterviewDTO();

                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Interview", "/interview/edit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewEdit()).thenReturn(breadcrumbEdit);

                mockMVC.perform(get("/interview/edit/1")
                                .param("prevPage", "list")
                                .with(user(mockUser)))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("breadCrumbEdit", breadcrumbEdit))
                        .andExpect(view().name("interview/edit-interview-details"));
            }

            @Test
            public void testEditInterviewSuccess() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                EditInterviewDTO editInterviewDTO = new EditInterviewDTO();

                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Interview", "/interview/edit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewEdit()).thenReturn(breadcrumbEdit);

                mockMVC.perform(post("/interview/edit/1")
                                .param("scheduleTitle", "New Test")
                                .param("candidates", "1")
                                .param("interviewDate", "2024-02-02")
                                .param("startTime", LocalTime.of(9, 0, 0).toString())
                                .param("endTime", LocalTime.of(11, 0, 0).toString())
                                .param("notes", "New Note")
                                .param("jobs", "1")
                                .param("interviewers", "1")
                                .param("location", "New Location")
                                .param("recruiters", "1")
                                .param("meetingId", "New Meeting")
                                .param("result", "Passed")
                                .param("status", "New")
                                .param("prevPage", "list")
                                .with(user(mockUser)))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/interview/list"));
            }

            @Test
            public void testEditInterviewWithEndTimeEarlierThanStartTime() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                EditInterviewDTO editInterviewDTO = new EditInterviewDTO();

                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Interview", "/interview/edit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewEdit()).thenReturn(breadcrumbEdit);

                mockMVC.perform(post("/interview/edit/1")
                                .param("scheduleTitle", "New Test")
                                .param("candidates", "1")
                                .param("interviewDate", "2024-02-02")
                                .param("startTime", LocalTime.of(11, 0, 0).toString())
                                .param("endTime", LocalTime.of(9, 0, 0).toString())
                                .param("notes", "New Note")
                                .param("jobs", "1")
                                .param("interviewers", "1")
                                .param("location", "New Location")
                                .param("recruiters", "1")
                                .param("meetingId", "New Meeting")
                                .param("result", "Passed")
                                .param("status", "New")
                                .param("prevPage", "list")
                                .with(user(mockUser)))
                        .andExpect(view().name("interview/edit-interview-details"));
            }

            @Test
            public void testEditInterviewIsNull() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());

                EditInterviewDTO editInterviewDTO = new EditInterviewDTO();

                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Interview", "/interview/edit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewEdit()).thenReturn(breadcrumbEdit);

                mockMVC.perform(post("/interview/edit/2")
                                .param("scheduleTitle", "New Test")
                                .param("candidates", "1")
                                .param("interviewDate", "2024-02-02")
                                .param("startTime", LocalTime.of(11, 0, 0).toString())
                                .param("endTime", LocalTime.of(9, 0, 0).toString())
                                .param("notes", "New Note")
                                .param("jobs", "1")
                                .param("interviewers", "1")
                                .param("location", "New Location")
                                .param("recruiters", "1")
                                .param("meetingId", "New Meeting")
                                .param("result", "Passed")
                                .param("prevPage", "list")
                                .with(user(mockUser)))
                        .andExpect(view().name("error/404"));
            }

        }
    }

    @Nested
    @DisplayName("Cancel Schedule")
    public class cancelSchedule {

        @Nested
        public class NoPermissionToCancel {

            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Interviewer");
                user.setPassword("interviewer");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("INTERVIEWER");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                mockMVC.perform(get("/interview/cancelSchedule/1")
                        .with(user(mockUser)))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToCancel {

            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Admin");
                user.setPassword("admin");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusSuccess() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                mockMVC.perform(get("/interview/cancelSchedule/1")
                                .with(user(mockUser)))
                        .andExpect(status().isOk());
            }

            @Test
            public void testCancelIsNull() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                mockMVC.perform(get("/interview/cancelSchedule/2")
                                .with(user(mockUser)))
                        .andExpect(view().name("error/404"));
            }
        }
    }

    @Nested
    @DisplayName("Submit Interview")
    public class submitInterview {

        @Nested
        public class NoPermissionToSubmit {
            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Admin");
                user.setPassword("Admin");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                mockMVC.perform(get("/interview/submit/1")
                                .with(user(mockUser)))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToSubmit {
            @BeforeEach
            void setup() {
                mockMVC = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("Interviewer");
                user.setPassword("interviewer");
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("INTERVIEWER");
                user.setRoles(Set.of(roles));

                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);

                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
                        .thenReturn(Optional.of(user));

                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));

                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
            }

            @Test
            public void shouldReturnStatusOkToAccessSubmitResult() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadCrumbSubmit = new Breadcrumb("Submit Interview", "/interview/submit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewSubmit()).thenReturn(breadCrumbSubmit);

                mockMVC.perform(get("/interview/submit/1")
                                .with(user(mockUser)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("interview/submit-interview-result"));
            }

            @Test
            public void testSubmitIsNull() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadCrumbSubmit = new Breadcrumb("Submit Interview", "/interview/submit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewSubmit()).thenReturn(breadCrumbSubmit);

                mockMVC.perform(get("/interview/submit/2")
                                .with(user(mockUser)))
                        .andExpect(view().name("error/404"));
            }

            @Test
            public void shouldReturnStatusOkToPostSubmitResult() throws Exception {
                Interview interview = new Interview();
                interview.setId(1);
                interview.setScheduleTitle("Test");
                interview.setInterviewDate(LocalDate.parse("2024-12-31"));
                interview.setStartTime(LocalTime.of(9, 0, 0));
                interview.setEndTime(LocalTime.of(11, 0, 0));
                interview.setNotes("Note Test");
                interview.setLocation("Hanoi");
                interview.setMeetingId("google meeting");
                interview.setResult(InterviewResult.Passed);
                interview.setStatus(ScheduleStatus.Interviewed);
                interview.setCandidates(new Candidates());
                interview.setJobs(new Jobs());
                interview.setRecruiters(new Recruiters());
                interview.setInterviewers(Set.of(new Interviewer()));
                interview.setOffer(new Offer());


                when(interviewService.findById(1)).thenReturn(Optional.of(interview));
                when(candidatesService.findAll()).thenReturn(List.of(new Candidates()));
                when(jobService.findAll()).thenReturn(List.of(new Jobs()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                when(interviewerService.findAll()).thenReturn(List.of(new Interviewer()));

                Breadcrumb breadcrumbList = new Breadcrumb("Interview List", "/interview/list/");
                Breadcrumb breadCrumbSubmit = new Breadcrumb("Submit Interview", "/interview/submit");

                when(breadcrumbService.getBreadcrumbInterviewList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbInterviewSubmit()).thenReturn(breadCrumbSubmit);

                mockMVC.perform(post("/interview/submit/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andExpect(status().isOk());
            }
        }
    }
}

