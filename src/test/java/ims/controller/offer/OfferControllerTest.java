package ims.controller.offer;

import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.offer.OfferController;
import fa.training.fjb04.ims.entity.Breadcrumb.Breadcrumb;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.jobs.Levels;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.offer.ContractType;
import fa.training.fjb04.ims.enums.offer.OfferStatus;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.PositionService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.jobs.LevelService;
import fa.training.fjb04.ims.service.offer.OfferService;
import fa.training.fjb04.ims.service.user.DepartmentService;
import fa.training.fjb04.ims.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class OfferControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private CandidatesService candidatesService;
    @MockBean
    private PositionService positionService;
    @MockBean
    private UserService userService;
    @MockBean
    private InterviewService interviewService;
    @MockBean
    private LevelService levelService;
    @MockBean
    private DepartmentService departmentService;
    @MockBean
    private RecruiterService recruiterService;
    @MockBean
    private OfferService offerService;
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
    private OfferController offerController;

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
        }

        @Test
        void shouldReturnOfferList() throws Exception {
            mockMvc.perform(get("/offer/list").with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                    .andDo(print())
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
                            get("/offer/list")
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Add Offer")
    public class addOffer {
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
            }

            @Test
            public void shouldReturnAddOfferPage() throws Exception {

                List<Candidates> candidates = List.of(new Candidates());
                List<Positions> positions = List.of(new Positions());
                List<User> approvers = List.of(new User());
                List<Interview> interviews = List.of(new Interview());
                List<ContractType> contractTypes = List.of(ContractType.FULL_TIME,
                        ContractType.PART_TIME,
                        ContractType.CONTRACTOR,
                        ContractType.INTERNSHIP,
                        ContractType.TEMPORARY);
                List<Levels> levels = List.of(new Levels());
                List<Department> departments = List.of(new Department());
                List<Recruiters> recruiters = List.of(new Recruiters());

                when(candidatesService.getCandidatesHaveStatusPassedInterview()).thenReturn(candidates);
                when(positionService.findAll()).thenReturn(positions);
                when(userService.getUsersIsManager()).thenReturn(approvers);
                when(interviewService.getInterviewsPass()).thenReturn(interviews);
                when(levelService.findAll()).thenReturn(levels);
                when(departmentService.findAllDepartment()).thenReturn(departments);
                when(recruiterService.findAll()).thenReturn(recruiters);

                mockMvc.perform(get("/offer/add")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("candidatesList", candidates))
                        .andExpect(model().attribute("approverList", approvers))
                        .andExpect(view().name("offer/add-new-offer"));
            }

//            @Test
//            void shouldAddSuccessANewOffer() throws Exception {
//                List<Candidates> candidates = List.of(new Candidates());
//                List<Positions> positions = List.of(new Positions());
//                List<User> approvers = List.of(new User());
//                List<Interview> interviews = List.of(new Interview());
//                List<ContractType> contractTypes = List.of(ContractType.FULL_TIME,
//                        ContractType.PART_TIME,
//                        ContractType.CONTRACTOR,
//                        ContractType.INTERNSHIP,
//                        ContractType.TEMPORARY);
//                List<Levels> levels = List.of(new Levels());
//                List<Department> departments = List.of(new Department());
//                List<Recruiters> recruiters = List.of(new Recruiters());
//
//                when(candidatesService.getCandidatesHaveStatusPassedInterview()).thenReturn(candidates);
//                when(positionService.findAll()).thenReturn(positions);
//                when(userService.getUsersIsManager()).thenReturn(approvers);
//                when(interviewService.getInterviewsPass()).thenReturn(interviews);
//                when(levelService.findAll()).thenReturn(levels);
//                when(departmentService.findAllDepartment()).thenReturn(departments);
//                when(recruiterService.findAll()).thenReturn(recruiters);
//
//                mockMvc.perform(
//                                post("/offer/add")
//                                        .param("candidate", "1")
//                                        .param("position", "1")
//                                        .param("approvedBy", "1")
//                                        .param("interviewInfo", "1")
//                                        .param("contractPeriodFrom", "2024-01-12")
//                                        .param("contractPeriodTo", "2024-01-15")
//                                        .param("interviewNotes", "notes")
//                                        .param("contractType", "CONTRACTOR")
//                                        .param("level", "1")
//                                        .param("offerStatus", "WAITING_FOR_APPROVAL")
//                                        .param("department", "1")
//                                        .param("recruiterOwner", "1")
//                                        .param("dueDate", "2024-09-09")
//                                        .param("basicSalary", "12000")
//                                        .param("note", "note")
//                                        .param("offerToken", "offerToken")
//                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                        ).andDo(print())
//                        .andExpect(status().is3xxRedirection())
//                        .andExpect(redirectedUrl("/offer/list"));
//                verify(offerService).save(any(Offer.class));
//            }

            @Test
            void testAddFailedBindingResultHasError() throws Exception {
                mockMvc.perform(
                        post("/offer/add")
                                .param("candidate", "1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                ).andDo(print())
                        .andExpect(view().name("offer/add-new-offer"));
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
            }

            @Test
            public void shouldReturnStatusForbidden() throws Exception {

                List<Candidates> candidates = List.of(new Candidates());
                List<Positions> positions = List.of(new Positions());
                List<User> approvers = List.of(new User());
                List<Interview> interviews = List.of(new Interview());
                List<ContractType> contractTypes = List.of(ContractType.FULL_TIME,
                        ContractType.PART_TIME,
                        ContractType.CONTRACTOR,
                        ContractType.INTERNSHIP,
                        ContractType.TEMPORARY);
                List<Levels> levels = List.of(new Levels());
                List<Department> departments = List.of(new Department());
                List<Recruiters> recruiters = List.of(new Recruiters());

                when(candidatesService.getCandidatesHaveStatusPassedInterview()).thenReturn(candidates);
                when(positionService.findAll()).thenReturn(positions);
                when(userService.getUsersIsManager()).thenReturn(approvers);
                when(interviewService.getInterviewsPass()).thenReturn(interviews);
                when(levelService.findAll()).thenReturn(levels);
                when(departmentService.findAllDepartment()).thenReturn(departments);
                when(recruiterService.findAll()).thenReturn(recruiters);

                mockMvc.perform(get("/offer/add")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isForbidden());
            }
        }
    }

    @Nested
    @DisplayName("View Offer detail")
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
            }

            @Test
            public void shouldReturnViewDetailOfferPage() throws Exception {

                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));


                when(offerService.findById(1)).
                        thenReturn(Optional.of(Optional.of(offer).orElse(null)));


                User user = new User();
                user.setUsername("admin");
                user.setPassword("admin");
                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                String userLogin = "admin";

                when(userService.findByUserName(userLogin)).thenReturn(user);

                Breadcrumb breadcrumbList = new Breadcrumb("Offer list", "/offer/list");

                Breadcrumb breadcrumbDetail= new Breadcrumb("Offer detail", "/offer/view/");

                when(breadcrumbService.getBreadcrumbOfferList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbOfferDetail()).thenReturn(breadcrumbDetail);

                mockMvc.perform(get("/offer/view/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("getOffer", offer))
                        .andExpect(view().name("offer/view-offer-details"));

            }

            @Test
            public void shouldReturnErrorPage() throws Exception {
                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));


                when(offerService.findById(1)).thenReturn(Optional.ofNullable(Optional.of(offer).orElseThrow(EntityNotFoundException::new)));



                User user = new User();
                user.setUsername("admin");
                user.setPassword("admin");
                Department department = new Department();
                department.setDepartmentName("Test");
                user.setDepartment(department);
                Roles roles = new Roles();
                roles.setId(1);
                roles.setRoleName("ADMIN");
                user.setRoles(Set.of(roles));

                String userLogin = "admin";

                when(userService.findByUserName(userLogin)).thenReturn(user);

                mockMvc.perform(get("/offer/view/2")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(view().name("error/404"));

            }
        }
    }

    @Nested
    @DisplayName("Approve Offer")
    public class ApproveOffer {
        @Nested
        public class DoNotHavePermissionToApproveOffer {
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

                Offer offer = new Offer();
                offer.setId(1);

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/view/approve-offer/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToApproveOffer {
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
            }

            @Test
            public void shouldReturnStatusOkToApproveOffer() throws Exception {

                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/view/approve-offer/1")
                                .param("prevPage", "list")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/offer/view/1"));
                verify(offerService).save(any(Offer.class));
            }


        }
    }

    @Nested
    @DisplayName("Cancel Offer")
    public class CancelOffer {
        @Nested
        public class DoNotHavePermissionToApproveOffer {
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

                Offer offer = new Offer();
                offer.setId(1);

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/view/cancel-offer/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToCancelOffer {
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
            }

            @Test
            public void shouldReturnStatusOkToCancelOffer() throws Exception {

                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/view/cancel-offer/1")
                                .param("prevPage", "list")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/offer/view/1"));
                verify(offerService).save(any(Offer.class));
            }
        }
    }

    @Nested
    @DisplayName("Send Offer")
    public class SendOffer {
        @Nested
        public class DoNotHavePermissionToSendOffer {
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

                Offer offer = new Offer();
                offer.setId(1);

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/view/send-offer/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToSendOffer {
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
            }

            @Test
            public void shouldReturnStatusOkToSendOffer() throws Exception {

                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/view/send-offer/1")
                                .param("prevPage", "list")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/offer/view/1"));
                verify(offerService).save(any(Offer.class));
            }
        }
    }

    @Nested
    @DisplayName("Edit Offer")
    public class EditOffer {
        @Nested
        public class DoNotHavePermissionToEditOffer {
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

                Offer offer = new Offer();
                offer.setId(1);

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                mockMvc.perform(get("/offer/edit/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        public class HavePermissionToEditOffer {
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
            }

            @Test
            public void shouldReturnStatusOkToAccessEditPage() throws Exception {

                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                Candidates candidates = new Candidates();
                List<Positions> positions = List.of(new Positions());
                List<User> users = List.of(new User());
                List<Interview> interviews = List.of(new Interview());
                List<ContractType> contractTypes = List.of(ContractType.FULL_TIME,
                        ContractType.PART_TIME,
                        ContractType.CONTRACTOR,
                        ContractType.INTERNSHIP,
                        ContractType.TEMPORARY);
                List<Levels> levels = List.of(new Levels());
                List<Department> departments = List.of(new Department());
                List<Recruiters> recruiters = List.of(new Recruiters());

                when(candidatesService.findCandidatesByOfferId(1)).thenReturn(candidates);
                when(positionService.findAll()).thenReturn(positions);
                when(userService.getUsersIsManager()).thenReturn(users);
                when(interviewService.getInterviewsPass()).thenReturn(interviews);
                when(levelService.findAll()).thenReturn(levels);
                when(departmentService.findAllDepartment()).thenReturn(departments);
                when(recruiterService.findAll()).thenReturn(recruiters);

                mockMvc.perform(get("/offer/edit/1")
                                .param("prevPage", "list")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(view().name("offer/edit-offer-details"));

            }

            @Test
            public void shouldReturnStatusOkToAccessEditPageWithPrevPageNotEqualToList() throws Exception {

                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                when(candidatesService.findCandidatesByOfferId(anyInt())).thenReturn(new Candidates());
                when(positionService.findAll()).thenReturn(List.of(new Positions()));
                when(userService.getUsersIsManager()).thenReturn(List.of(new User()));
                when(interviewService.getInterviewsPass()).thenReturn(List.of(new Interview()));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));

                Breadcrumb breadcrumbDetail = new Breadcrumb("Offer detail", "/offer/view");

                Breadcrumb breadcrumbEdit = new Breadcrumb("Offer Job", "/offer/edit/");

                when(breadcrumbService.getBreadcrumbOfferList()).thenReturn(breadcrumbDetail);
                when(breadcrumbService.getBreadcrumbOfferEdit()).thenReturn(breadcrumbEdit);

                mockMvc.perform(get("/offer/edit/1")
                                .param("prevPage", "edit")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().isOk())
//                        .andExpect(model().attribute("breadcrumbEdit", breadcrumbEdit))
                        .andExpect(view().name("offer/edit-offer-details"));
            }

//            @Test
//            public void testEditOfferSuccess() throws Exception {
//                Offer offer = new Offer();
//                offer.setId(1);
//                offer.setCandidate(new Candidates());
//                offer.setPosition(new Positions());
//                offer.setApprovedBy(new User());
//                offer.setInterviewInfo(new Interview());
//                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
//                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
//                offer.setInterviewNotes("note");
//                offer.setContractType(ContractType.CONTRACTOR);
//                offer.setLevel(new Levels());
//                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
//                offer.setDepartment(new Department());
//                offer.setRecruiterOwner(new Recruiters());
//                offer.setDueDate(LocalDate.of(2024, 12, 12));
//                offer.setBasicSalary(new BigDecimal(2.3));
//
//                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));
//
//                when(candidatesService.findCandidatesByOfferId(anyInt())).thenReturn(new Candidates());
//                when(positionService.findAll()).thenReturn(List.of(new Positions()));
//                when(userService.getUsersIsManager()).thenReturn(List.of(new User()));
//                when(interviewService.getInterviewsPass()).thenReturn(List.of(new Interview()));
//                when(levelService.findAll()).thenReturn(List.of(new Levels()));
//                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));
//                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));
//
//                mockMvc.perform(
//
//                        post("/offer/edit/1")
//                                .param("candidate", "1")
//                                .param("position", "1")
//                                .param("approvedBy", "1")
//                                .param("interviewInfo", "1")
//                                .param("contractPeriodFrom", "2024-01-12")
//                                .param("contractPeriodTo", "2024-01-15")
//                                .param("interviewNotes", "notes")
//                                .param("contractType", "CONTRACTOR")
//                                .param("level", "1")
//                                .param("offerStatus", "WAITING_FOR_APPROVAL")
//                                .param("department", "1")
//                                .param("recruiterOwner", "1")
//                                .param("dueDate", "2024-09-09")
//                                .param("basicSalary", "12000")
//                                .param("note", "note")
//                                .param("offerToken", "offerToken")
//                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                ).andDo(print())
////                        .andExpect(status().is3xxRedirection())
//                        .andExpect(redirectedUrl("/offer/list"));
//            }

            @Test
            public void testEditOfferFailedBindingResultHasError() throws Exception {
                Offer offer = new Offer();
                offer.setId(1);
                offer.setCandidate(new Candidates());
                offer.setPosition(new Positions());
                offer.setApprovedBy(new User());
                offer.setInterviewInfo(new Interview());
                offer.setContractPeriodFrom(LocalDate.of(2024, 10, 10));
                offer.setContractPeriodTo(LocalDate.of(2024, 12, 12));
                offer.setInterviewNotes("note");
                offer.setContractType(ContractType.CONTRACTOR);
                offer.setLevel(new Levels());
                offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
                offer.setDepartment(new Department());
                offer.setRecruiterOwner(new Recruiters());
                offer.setDueDate(LocalDate.of(2024, 12, 12));
                offer.setBasicSalary(new BigDecimal(2.3));

                when(offerService.findById(1)).thenReturn(Optional.of(Optional.of(offer).orElse(null)));

                when(candidatesService.findCandidatesByOfferId(anyInt())).thenReturn(new Candidates());
                when(positionService.findAll()).thenReturn(List.of(new Positions()));
                when(userService.getUsersIsManager()).thenReturn(List.of(new User()));
                when(interviewService.getInterviewsPass()).thenReturn(List.of(new Interview()));
                when(levelService.findAll()).thenReturn(List.of(new Levels()));
                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));
                when(recruiterService.findAll()).thenReturn(List.of(new Recruiters()));

                mockMvc.perform(

                                post("/offer/edit/1")
                                        .param("candidate", "1")
                                        .param("position", "1")
                                        .param("interviewInfo", "1")
                                        .param("contractPeriodFrom", "2024-01-12")
                                        .param("contractPeriodTo", "2024-01-15")
                                        .param("interviewNotes", "notes")
                                        .param("contractType", "CONTRACTOR")
                                        .param("level", "1")
                                        .param("offerStatus", "WAITING_FOR_APPROVAL")
                                        .param("department", "1")
                                        .param("recruiterOwner", "1")
                                        .param("dueDate", "2024-09-09")
                                        .param("basicSalary", "12000")
                                        .param("note", "note")
                                        .param("offerToken", "offerToken")
                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(model().attributeExists("updateFailed"))
                        .andExpect(view().name("offer/edit-offer-details"));
            }
        }
    }

    @Nested
    @DisplayName("Export Excel")
    public class ExportExcel {
        @Nested
        public class DoNotHavePermissionToExportExcelOffer {
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
        }
    }
}
