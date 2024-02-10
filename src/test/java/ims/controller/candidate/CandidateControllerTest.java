package ims.controller.candidate;

import fa.training.fjb04.ims.api.download.DownloadController;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.candidate.CandidateController;
import fa.training.fjb04.ims.entity.Breadcrumb.Breadcrumb;
import fa.training.fjb04.ims.entity.candidates.*;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.*;
import fa.training.fjb04.ims.service.file.FileStorageService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.jobs.SkillService;
import fa.training.fjb04.ims.service.offer.OfferService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.candidates.CandidateDTO;
import fa.training.fjb04.ims.util.dto.candidates.EditCandidateDTO;
import fa.training.fjb04.ims.util.dto.jobs.EditJobDTO;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(PowerMockRunner.class)
public class CandidateControllerTest {
    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private FileStorageService fileStorageService;
    @MockBean
    private DownloadController downloadController;
    @MockBean
    private CandidatesService candidateService;
    @MockBean
    private CandidateStatusService candidateStatusService;
    @MockBean
    private HighestLevelService highestLevelService;
    @MockBean
    private PositionService positionService;
    @MockBean
    private RecruiterService recruiterService;
    @MockBean
    private SkillService skillService;
    @MockBean
    private UserService userService;
    @MockBean
    private BreadcrumbService breadcrumbService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private InterviewService interviewService;
    @MockBean
    private OfferService offerService;



    @MockBean
    private PlatformTransactionManager transactionManager;
    private MockMvc mockMvc;

    private UserDetails mockUser;

    private Model model;
    @InjectMocks
    private CandidateController candidateController;



    @Nested
    @DisplayName("Already authenticate")
    public class haveRole {
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
        }

        @Test
        void shouldReturnCandidateList() throws Exception {
            mockMvc.perform(get("/candidate/list").with(SecurityMockMvcRequestPostProcessors.user(mockUser)))

                    .andExpect(status().is2xxSuccessful());
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
                            get("/candidate/list")
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Add Candidate")
    public class addCandidate {

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
            public void shouldReturnAddCandidatePage() throws Exception {
                List<HighestLevel> highestLevelList = List.of(new HighestLevel());
                List<Positions> positionsList = List.of(new Positions());
                List<Recruiters> recruitersList = List.of(new Recruiters());
                List<Skills> skillsList = List.of(new Skills());

                when(highestLevelService.findAll()).thenReturn(highestLevelList);
                when(positionService.findAll()).thenReturn(positionsList);
                when(recruiterService.findAll()).thenReturn(recruitersList);
                when(skillService.findAll()).thenReturn(skillsList);

                CandidateDTO candidateDTO = new CandidateDTO();

                mockMvc.perform(get("/candidate/add")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("newCandidate", candidateDTO))
                        .andExpect(view().name("candidate/add-new-candidate"));
            }


            @Test
            void shouldAddSuccessANewCandidate() throws Exception {
//                MockMultipartFile file = new MockMultipartFile("cvAttachment", "de mau test.docx".getBytes());
                String filePath = "D:/BatchGit/mock_ims_team_02/src/main/resources/test.docx";
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));


                Breadcrumb breadcrumb = new Breadcrumb("Candidate Add", "/candidate/add/");

                Breadcrumb breadcrumbAdd = new Breadcrumb("Candidate list", "/candidate/list");

                when(breadcrumbService.getBreadcrumbCandidateDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbCandidateList()).thenReturn(breadcrumbAdd);

                MockMultipartFile file = new MockMultipartFile("cvAttachment", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",fileContent);
                                    mockMvc.perform(multipart("/candidate/add")
                                                .file(file)
                                                .param("name", "Nguyễn Đăng Vinh")
                                                .param("address", "Đống Đa, Hà Nội")
                                                .param("cvAttachment", "test.docx")
                                                .param("dateOfBirth", "1990-12-28")
                                                .param("email", "abc@gmail.com")
                                                .param("gender", "MALE")
                                                .param("phone", "0987123654")
                                                .param("note", "note")
                                                .param("yearOfExperience", "1")
                                                .param("skillsSet", "1", "2", "3")
                                                .param("recruiter", "1")
                                                .param("position", "1")
                                                .param("candidateStatus", "2")
                                                .param("highestLevel", "3")

                                )
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(redirectedUrl("/candidate/list"));

                verify(candidateService).save(any(Candidates.class));
            }



        }
        @Test
        void testAddFailedBindingResultHasError() throws Exception {
            mockMvc.perform(
                            post("/candidate/add")
                                    .param("name", "")
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
                    .andExpect(view().name("candidate/add-new-candidate"));
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
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                List<HighestLevel> highestLevelList = List.of(new HighestLevel());
                List<Positions> positionsList = List.of(new Positions());
                List<Recruiters> recruitersList = List.of(new Recruiters());
                List<Skills> skillsList = List.of(new Skills());
                List<CandidateStatus> candidateStatusList = List.of(new CandidateStatus());

                when(highestLevelService.findAll()).thenReturn(highestLevelList);
                when(positionService.findAll()).thenReturn(positionsList);
                when(recruiterService.findAll()).thenReturn(recruitersList);
                when(skillService.findAll()).thenReturn(skillsList);


                mockMvc.perform(get("/candidate/add")
                                .with(user(mockUser))
                        )
                        .andExpect(status().isForbidden());
            }
        }

//        edit


        @Nested
        @DisplayName("Edit Candidate")
        public class EditCandidate {

            @Nested
            public class DoNotHavePermissionToEditCandidate {
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

                    mockMvc.perform(get("/candidate/edit/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(status().isForbidden());
                }


            }


            @Nested
            public class HavePermissionToEditCandidate {
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

                    Candidates candidates = new Candidates();

                    EditCandidateDTO candidateDTO = new EditCandidateDTO();

                    when(candidateService.findByCandidateId(anyInt())).thenReturn(Optional.of(candidates));
                    when(highestLevelService.findAll()).thenReturn(List.of(new HighestLevel()));
                    when(skillService.findAll()).thenReturn(List.of(new Skills()));
                    when(positionService.findAll()).thenReturn(List.of(new Positions()));
                    when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
                    when(candidateStatusService.findAll()).thenReturn(List.of(new CandidateStatus()));


                    Breadcrumb breadcrumbList = new Breadcrumb("Candidate list", "/candidate/list");

                    Breadcrumb breadcrumbEdit = new Breadcrumb("Edit Candidate", "/candidate/edit/");

                    when(breadcrumbService.getBreadcrumbCandidateList()).thenReturn(breadcrumbList);
                    when(breadcrumbService.getBreadcrumbCandidateEdit()).thenReturn(breadcrumbEdit);

                    mockMvc.perform(get("/candidate/edit/" + anyInt())
                                    .param("prevPage", "list")
                                    .with(user(mockUser)))
                            .andExpect(status().isOk())
                            .andExpect(model().attribute("breadcrumbList", breadcrumbList))
                            .andExpect(view().name("candidate/edit-candidate-details"))
                            .andExpect(model().attribute("candidate", candidateDTO)

                            );

                }

            }


        }

    @Nested
        @DisplayName("View Candidate detail")
        public class viewCandidateDetail {

            @Nested
            public class havePermissionToView {
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

                }

                @Test
                public void shouldReturnViewDetailCandidatePage() throws Exception {
                    Candidates candidate = new Candidates();
                    candidate.setId(1);

                    Breadcrumb breadcrumb = new Breadcrumb("Candidate detail", "/candidate/view/");

                    Breadcrumb breadcrumbList = new Breadcrumb("Candidate list", "/candidate/list");

                    when(breadcrumbService.getBreadcrumbCandidateDetail()).thenReturn(breadcrumb);
                    when(breadcrumbService.getBreadcrumbCandidateList()).thenReturn(breadcrumbList);

                    when(candidateService.findByCandidateId(1)).thenReturn(Optional.of(candidate));

                    mockMvc.perform(get("/candidate/view/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(status().isOk())
                            .andExpect(model().attribute("candidate", candidate))
                            .andExpect(view().name("candidate/view-candidate-details"));
                }

                @Test
                public void testEditCandidateSuccess() throws Exception {

                    Integer candidateId = 1;
                    Candidates candidates = new Candidates();
                    candidates.setId(candidateId);
                    EditCandidateDTO editCandidateDTO = new EditCandidateDTO();

                    EditJobDTO editJobDTO = new EditJobDTO();

                    when(candidateService.findByCandidateId(1)).thenReturn(Optional.of(candidates));
                    when(positionService.findAll()).thenReturn(List.of(new Positions()));
                    when(highestLevelService.findAll()).thenReturn(List.of(new HighestLevel()));
                    when(skillService.findAll()).thenReturn(List.of(new Skills()));
                    when(candidateStatusService.findAll()).thenReturn(List.of(new CandidateStatus()));

                    mockMvc.perform(
                                    post("/candidate/edit/1")
                                            .param("name", "Nguyễn Đăng Thái")
                                            .param("address", "Đống Đa, Hà Nội")
                                            .param("skillsSet", "1", "2", "3")
                                            .param("recruiter", "1")
                                            .param("position", "1")
                                            .param("candidateStatus", "2")
                                            .param("highestLevel", "3")

                            )
                            .andExpect(status().is2xxSuccessful())
                            .andExpect(redirectedUrl("/candidate/list"));

                    verify(candidateService).save(any(Candidates.class));
                }

                @Test
                public void shouldReturnErrorPageForNonexistentCandidate() throws Exception {
                    when(candidateService.findByCandidateId(2)).thenReturn(Optional.empty());

                    Breadcrumb breadcrumb = new Breadcrumb("Candidate detail", "/candidate/view/");

                    Breadcrumb breadcrumbList = new Breadcrumb("Candidate list", "/candidate/list");

                    when(breadcrumbService.getBreadcrumbCandidateDetail()).thenReturn(breadcrumb);
                    when(breadcrumbService.getBreadcrumbCandidateList()).thenReturn(breadcrumbList);

                    mockMvc.perform(get("/candidate/view/2")
                                    .with(user(mockUser))
                            )
                            .andExpect(view().name("error/404"));
                }

            }
        }

        @Nested
        @DisplayName("Delete candidate")
        public class deleteCandidate {

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
                    mockMvc.perform(get("/candidate/delete/1")
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
                public void shouldDeleteSuccessAndRedirectToCandidateListPage() throws Exception {
                    Candidates candidate = new Candidates();
                    candidate.setId(1);

                    mockMvc.perform(get("/candidate/delete/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl("/candidate/list"))
                            .andExpect(flash().attribute("deleteSuccessMessage", "Candidate has been deleted successfully!"));

                    verify(candidateService).deleteCandidate(1);
                }

                @Test
                public void shouldHandleExceptionAndRedirectWithError() throws Exception {
                    // Mock the behavior of your service to throw an exception
                    doThrow(new RuntimeException("Failed to delete candidate")).when(candidateService).deleteCandidate(1);

                    mockMvc.perform(get("/candidate/delete/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl("/candidate/list"))
                            .andExpect(flash().attribute("deleteErrorMessage", "Failed to delete candidate"));

                    verify(candidateService).deleteCandidate(1);
                }
            }
        }

        @Nested
        @DisplayName("Ban candidate")
        public class banCandidate {

            @Nested
            public class doNotHavePermissionToBan {
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
                    mockMvc.perform(get("/candidate/ban/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(status().isForbidden());
                }
            }

            @Nested
            public class HavePermissionToBan {
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
                public void shouldBanSuccessAndRedirectToCandidateViewPage() throws Exception {
                    Candidates candidate = new Candidates();
                    candidate.setId(1);

                    when(candidateService.findByCandidateId(1)).thenReturn(Optional.of(candidate));

                    mockMvc.perform(get("/candidate/ban/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl("/candidate/view/1"));

                    verify(candidateService).save(candidate);
                }

                @Test
                public void shouldReturnErrorPageForNonExistingCandidate() throws Exception {
                    when(candidateService.findByCandidateId(1)).thenReturn(Optional.empty());

                    mockMvc.perform(get("/candidate/ban/1")
                                    .with(user(mockUser))
                            )
                            .andExpect(view().name("error/404"));
                }
            }
        }

    }
}
