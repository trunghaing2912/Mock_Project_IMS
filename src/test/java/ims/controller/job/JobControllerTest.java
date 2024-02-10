package ims.controller.job;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.job.JobController;
import fa.training.fjb04.ims.entity.Breadcrumb.Breadcrumb;
import fa.training.fjb04.ims.entity.jobs.Benefit;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.entity.jobs.Levels;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.jobs.BenefitService;
import fa.training.fjb04.ims.service.jobs.JobService;
import fa.training.fjb04.ims.service.jobs.LevelService;
import fa.training.fjb04.ims.service.jobs.SkillService;
import fa.training.fjb04.ims.util.dto.jobs.EditJobDTO;
import fa.training.fjb04.ims.util.dto.jobs.JobImportDTO;
import fa.training.fjb04.ims.util.dto.jobs.JobsDTO;
import fa.training.fjb04.ims.util.imports.ImportFile;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(PowerMockRunner.class)
class JobControllerTest {


    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private LevelService levelService;
    @MockBean
    private BenefitService benefitService;
    @MockBean
    private SkillService skillService;
    @MockBean
    private JobService jobService;

    @MockBean
    private BreadcrumbService breadcrumbService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PlatformTransactionManager transactionManager;
    private MockMvc mockMvc;

    private UserDetails mockUser;

    private Model model;

    @InjectMocks
    private JobController jobController;

    @Nested
    @DisplayName("Already authenticate")
    public class haveRole {
        @BeforeEach
        void setUp() {

            mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
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
            ;
        }

        @Test
        void shouldReturnJobList() throws Exception {

            mockMvc.perform(
                            get("/job/list")
                                    .with(user(mockUser))
                    ).andExpect(status().is2xxSuccessful());
        }

    }

    @Nested
    @DisplayName("Don't have role")
    public class unAuthenticate {
        @BeforeEach
        void setUp() {

            mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                    .apply(springSecurity()).build();
            User user = new User();
            user.setUsername("CANDIDATE");
            user.setPassword("123");
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
            ;
        }

        @Test
        void shouldReturnStatusForbidden() throws Exception {

            mockMvc.perform(
                            get("/job/list")
                                    .with(user(mockUser))
                    ).andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Add Job")
    public class addJob {

        @Nested
        public class havePermissionToAdd {

            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
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
                ;
            }

            @Test
            public void shouldReturnAddJobPage() throws Exception {

                List<Levels> levels = List.of(new Levels());
                List<Skills> skills = List.of(new Skills());
                List<Benefit> benefits = List.of(new Benefit());

                when(levelService.findAll()).thenReturn(levels);
                when(skillService.findAll()).thenReturn(skills);
                when(benefitService.findAll()).thenReturn(benefits);

                JobsDTO jobsDTO = new JobsDTO();

                mockMvc.perform(get("/job/add")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("newJob", jobsDTO))
                        .andExpect(view().name("job/add-new-job"));
            }

            @Test
            void shouldAddSuccessANewJob() throws Exception {

                mockMvc.perform(
                                post("/job/add")
                                        .param("title", "Some title")
                                        .param("minSalary", "1000")
                                        .param("maxSalary", "2000")
                                        .param("workingAddress", "Some workingAddress")
                                        .param("startDate", String.valueOf(LocalDate.now()))
                                        .param("endDate", String.valueOf(LocalDate.now().plusDays(30)))
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/job/list"))
                ;

                verify(jobService).save(any(Jobs.class));
            }

            @Test
            void testAddFailedBindingResultHasError() throws Exception {

                mockMvc.perform(
                                post("/job/add")
                                        .param("title", "Some title")
                                        .param("workingAddress", "Some workingAddress")
                                        .param("startDate", "2024-01-15")
                                        .param("endDate", "2024-02-15")
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(view().name("job/add-new-job"))
                ;

            }

        }

        @Nested
        public class doNotHavePermissionToAccessAddForm {

            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("interviewer");
                user.setPassword("123");
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
                ;
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                List<Levels> levels = List.of(new Levels());
                List<Skills> skills = List.of(new Skills());
                List<Benefit> benefits = List.of(new Benefit());

                when(levelService.findAll()).thenReturn(levels);
                when(skillService.findAll()).thenReturn(skills);
                when(benefitService.findAll()).thenReturn(benefits);

                mockMvc.perform(get("/job/add")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isForbidden());
            }

        }

    }

    @Nested
    @DisplayName("View Job detail")
    public class viewJobDetail {

        @Nested
        public class havePermissionToView {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
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
                ;
            }

            @Test
            public void shouldReturnViewDetailJobPage() throws Exception {

                Jobs jobs = new Jobs();
                jobs.setId(1);

                Breadcrumb breadcrumb = new Breadcrumb("Job detail", "/job/view/");

                Breadcrumb breadcrumb1 = new Breadcrumb("Job list", "/job/list");

                when(breadcrumbService.getBreadcrumbJobDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbJobList()).thenReturn(breadcrumb1);

                when(jobService.findByJobId(1)).thenReturn(Optional.of(Optional.of(jobs).orElse(null)));

                mockMvc.perform(get("/job/view/1")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("view", jobs))
                        .andExpect(view().name("job/view-job-details"));

            }

            @Test
            public void shouldReturnErrorPage() throws Exception {

                Jobs jobs = new Jobs();
                jobs.setId(1);

                when(jobService.findByJobId(1)).thenReturn(Optional.ofNullable(Optional.of(jobs).orElseThrow(EntityNotFoundException::new)));

                Breadcrumb breadcrumb = new Breadcrumb("Job detail", "/job/view/");

                Breadcrumb breadcrumb1 = new Breadcrumb("Job list", "/job/list");

                when(breadcrumbService.getBreadcrumbJobDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbJobList()).thenReturn(breadcrumb1);


                mockMvc.perform(get("/job/view/2")
                                .with(user(mockUser))
                        )
                        .andExpect(view().name("error/404"));

            }

        }

    }

    @Nested
    @DisplayName("Edit Job")
    public class EditJob {

        @Nested
        public class DoNotHavePermissionToEditJob {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("interviewer");
                user.setPassword("123");
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
                ;
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                mockMvc.perform(get("/job/edit/1")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isForbidden());
            }

        }

        @Nested
        public class HavePermissionToEditJob {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
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
                ;
            }

            @Test
            public void shouldReturnStatusOkToAccessEditPage() throws Exception {

                Jobs jobs = new Jobs();

                EditJobDTO jobDTO = new EditJobDTO();

                when(jobService.findByJobId(anyInt())).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));

                Breadcrumb breadcrumbList = new Breadcrumb("Job list", "/job/list");

                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Job", "/job/edit/");

                when(breadcrumbService.getBreadcrumbJobList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbJobEdit()).thenReturn(breadcrumbEdit);

                mockMvc.perform(get("/job/edit/" + anyInt())
                                .param("prevPage", "list")
                                .with(user(mockUser)))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("breadcrumbList", breadcrumbList))
                        .andExpect(view().name("job/edit-job-details"))
                        .andExpect(model().attribute("job", jobDTO)

                        );

            }

            @Test
            public void shouldReturnStatusOkToAccessEditPageWithPrevPageNotEqualToList() throws Exception {

                Jobs jobs = new Jobs();

                EditJobDTO jobDTO = new EditJobDTO();

                when(jobService.findByJobId(anyInt())).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));

                Breadcrumb breadcrumbDetail = new Breadcrumb("Job detail", "/job/view");

                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Job", "/job/edit/");

                when(breadcrumbService.getBreadcrumbJobList()).thenReturn(breadcrumbDetail);
                when(breadcrumbService.getBreadcrumbJobEdit()).thenReturn(breadcrumbEdit);

                mockMvc.perform(get("/job/edit/" + anyInt())
                                .param("prevPage", "edit")
                                .with(user(mockUser)))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("breadcrumbEdit", breadcrumbEdit))
                        .andExpect(view().name("job/edit-job-details"))
                        .andExpect(model().attribute("job", jobDTO)

                        );

            }

            @Test
            public void testEditJobSuccess() throws Exception {

                Integer jobId = 1;
                Jobs jobs = new Jobs();
                jobs.setId(jobId);
                jobs.setStartDate(LocalDate.now().plusDays(1));

                EditJobDTO editJobDTO = new EditJobDTO();

                when(jobService.findByJobId(1)).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));

                mockMvc.perform(
                                post("/job/edit/1")
                                        .param("title", "Fresher Java")
                                        .param("minSalary", "1000")
                                        .param("maxSalary", "2000")
                                        .param("workingAddress", "Ha Noi")
                                        .param("startDate", LocalDate.now().plusDays(2).toString())
                                        .param("endDate", LocalDate.now().plusDays(30).toString())
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/job/list"))
                ;

                verify(jobService).changeStatus(jobId);
            }

            @Test
            public void testEditJobFailedBindingResultHasError() throws Exception {

                Integer jobId = 1;
                Jobs jobs = new Jobs();
                jobs.setId(jobId);
                jobs.setStartDate(LocalDate.now().minusDays(1));

                EditJobDTO editJobDTO = new EditJobDTO();

                when(jobService.findByJobId(1)).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));

                mockMvc.perform(
                                post("/job/edit/1")
                                        .param("minSalary", "1000")
                                        .param("maxSalary", "2000")
                                        .param("workingAddress", "Ha Noi")
                                        .param("startDate", LocalDate.now().plusDays(1).toString())
                                        .param("endDate", LocalDate.now().plusDays(30).toString())
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(model().attributeExists("createFailed"))
                        .andExpect(view().name("job/edit-job-details"));
                ;

            }

            @Test
            public void EditFailed_NewStartDateIsInThePastAndHasPrevPageEqualToList() throws Exception {

                Integer jobId = 1;
                Jobs jobs = new Jobs();
                jobs.setId(jobId);
                jobs.setStartDate(LocalDate.now().plusDays(1));

                EditJobDTO editJobDTO = new EditJobDTO();

                when(jobService.findByJobId(1)).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));

                Breadcrumb breadcrumb = new Breadcrumb("Job list", "/job/list");

                when(breadcrumbService.getBreadcrumbJobList()).thenReturn(breadcrumb);


                mockMvc.perform(
                                post("/job/edit/1")
                                        .param("prevPage", "list")
                                        .param("title", "Fresher Java")
                                        .param("minSalary", "1000")
                                        .param("maxSalary", "2000")
                                        .param("workingAddress", "Ha Noi")
                                        .param("startDate", LocalDate.now().minusDays(30).toString())
                                        .param("endDate", LocalDate.now().plusDays(30).toString())
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(model().attributeHasFieldErrors("job", "startDate"))
                        .andExpect(view().name("job/edit-job-details"))
                        .andExpect(model().attribute("breadcrumbList", breadcrumb))
                ;

            }

            @Test
            public void prevPageNotEqualToList() throws Exception {

                Integer jobId = 1;
                Jobs jobs = new Jobs();
                jobs.setId(jobId);
                jobs.setStartDate(LocalDate.now().plusDays(1));

                EditJobDTO editJobDTO = new EditJobDTO();

                when(jobService.findByJobId(1)).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));

                Breadcrumb breadcrumb = new Breadcrumb("Job detail", "/job/view/");

                when(breadcrumbService.getBreadcrumbJobDetail()).thenReturn(breadcrumb);


                mockMvc.perform(
                                post("/job/edit/1")
                                        .param("prevPage", "edit")
                                        .param("title", "Fresher Java")
                                        .param("minSalary", "1000")
                                        .param("maxSalary", "2000")
                                        .param("workingAddress", "Ha Noi")
                                        .param("startDate", LocalDate.now().minusDays(30).toString())
                                        .param("endDate", LocalDate.now().plusDays(30).toString())
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(model().attributeHasFieldErrors("job", "startDate"))
                        .andExpect(view().name("job/edit-job-details"))
                        .andExpect(model().attributeExists("invalidStartDate"))
                        .andExpect(model().attribute("breadcrumbDetail", breadcrumb))
                ;

            }

            @Test
            public void jobIsNull() throws Exception {

                Integer jobId = 1;
                Jobs jobs = new Jobs();
                jobs.setId(jobId);
                jobs.setStartDate(LocalDate.now().plusDays(1));

                EditJobDTO editJobDTO = new EditJobDTO();

                when(jobService.findByJobId(1)).thenReturn(Optional.of(jobs));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(benefitService.findAll()).thenReturn(List.of(new Benefit()));
                when(skillService.findAll()).thenReturn(List.of(new Skills()));

                Breadcrumb breadcrumb = new Breadcrumb("Job detail", "/job/view/");

                when(breadcrumbService.getBreadcrumbJobDetail()).thenReturn(breadcrumb);

                mockMvc.perform(
                                post("/job/edit/2")
                                        .param("prevPage", "edit")
                                        .param("title", "Fresher Java")
                                        .param("minSalary", "1000")
                                        .param("maxSalary", "2000")
                                        .param("workingAddress", "Ha Noi")
                                        .param("startDate", LocalDate.now().plusDays(10).toString())
                                        .param("endDate", LocalDate.now().plusDays(30).toString())
                                        .param("skillsSet", "1", "2", "3")
                                        .param("benefitSet", "1", "2", "3")
                                        .param("levelsSet", "1", "2", "3")
                                        .with(user(mockUser))
                        )
                        .andExpect(view().name("error/404"))
                        .andExpect(model().attribute("breadcrumbDetail", breadcrumb))
                ;

            }

        }

    }

    @Nested
    @DisplayName("Delete job")
    public class deleteJob {
        @Nested
        public class doNotHavePermissionToDelete {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("interviewer");
                user.setPassword("123");
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
                ;
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                mockMvc.perform(get("/job/delete/1")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isForbidden());
            }

        }

        @Nested
        public class HavePermissionToDelete {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("admin");
                user.setPassword("123");
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
                ;
            }

            @Test
            public void shouldDeleteSuccessAndRedirectToJobListPage() throws Exception {
                Jobs jobs = new Jobs();
                jobs.setId(1);
                mockMvc.perform(get("/job/delete/1")
                                .with(user(mockUser))
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/job/list"));
                verify(jobService).deleteJob(1);
            }

        }
    }

    @Nested
    @DisplayName("Import job")
    public class importJob {
        @Nested
        public class doNotHavePermissionToImport {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("interviewer");
                user.setPassword("123");
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
                ;
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                String filePath = "D:\\Mock\\mock_ims_team_02\\src\\main\\resources\\Import_Job_Template.xlsx";
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

                MockMultipartFile file = new MockMultipartFile("file", "Import_Job_Template.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileContent);
                List<JobImportDTO> jobImportDTOList = new ArrayList<>();

                when(ImportFile.processExelFile(file)).thenReturn(jobImportDTOList);
                when(skillService.findBySkillName(anyString())).thenReturn(new Skills());
                when(benefitService.findByBenefitName(anyString())).thenReturn(new Benefit());
                when(levelService.findByLevelName(anyString())).thenReturn(new Levels());
                when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.of("user"));
                List<Jobs> jobs = new ArrayList<>();
                doNothing().when(jobService).saveAllJob(jobs);

                mockMvc.perform(multipart("/job/import")
                        .file(file)
                        .with(user(mockUser))
                ).andExpect(status().isForbidden());

            }

        }

        @Nested
        public class HavePermissionToImport {
            @BeforeEach
            void setUp() {

                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                        .apply(springSecurity()).build();
                User user = new User();
                user.setUsername("admin");
                user.setPassword("123");
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
                ;
            }

            @Test
            public void testImportNewJob() throws Exception {
                String filePath = "D:\\Mock\\mock_ims_team_02\\src\\main\\resources\\Import_Job_Template.xlsx";
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

                MockMultipartFile file = new MockMultipartFile("file", "Import_Job_Template.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileContent);
                List<JobImportDTO> jobImportDTOList = new ArrayList<>();

                mockStatic(ImportFile.class);
                mockStatic(SecurityUtils.class);
                when(ImportFile.processExelFile(file)).thenReturn(jobImportDTOList);
                when(skillService.findBySkillName(anyString())).thenReturn(new Skills());
                when(benefitService.findByBenefitName(anyString())).thenReturn(new Benefit());
                when(levelService.findByLevelName(anyString())).thenReturn(new Levels());
                when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.of("user"));
                List<Jobs> jobsList = new ArrayList<>();
                doNothing().when(jobService).saveAllJob(jobsList);

                mockMvc.perform(multipart("/job/import")
                        .file(file)
                        .with(user(mockUser))
                ).andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/job/list"));

            }
            @Test
            public void testFileIsEmpty() throws Exception {

                mockMvc.perform(multipart("/job/import")
                        ).andExpect(status().is3xxRedirection());

            }

        }

    }
}

