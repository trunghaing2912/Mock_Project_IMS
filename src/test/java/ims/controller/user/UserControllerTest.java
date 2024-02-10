package ims.controller.user;

import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.user.UserController;
import fa.training.fjb04.ims.entity.Breadcrumb.Breadcrumb;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.user.Gender;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.repository.user.RolesRepository;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.user.DepartmentService;
import fa.training.fjb04.ims.service.user.RolesService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.user.AddUserDTO;
import fa.training.fjb04.ims.util.dto.user.EditUserDTO;
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
import org.springframework.web.context.WebApplicationContext;

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
public class UserControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RolesRepository repository;

    @MockBean
    private RolesService rolesService;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private UserService userService;

    @MockBean
    private BreadcrumbService breadcrumbService;

    @InjectMocks
    private UserController userController;

    private UserDetails mockUser;

    private MockMvc mockMvc;

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
        void shouldReturnUserList() throws Exception {
            mockMvc.perform(
                            get("/user/list")
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
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
                            get("/user/list")
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Add User")
    public class addUser {
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
        public void shouldReturnAddUserPage() throws Exception {
            List<Roles> roles = List.of(new Roles());
            List<Department> departments = List.of(new Department());

            when(rolesService.findAllRoles()).thenReturn(roles);
            when(departmentService.findAllDepartment()).thenReturn(departments);

            AddUserDTO userDTO = new AddUserDTO();
            mockMvc.perform(get("/user/add")
                            .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("addNewUser", userDTO))
                    .andExpect(view().name("user/add-new-user"));
        }

//        @Test
//        void shouldAddSuccessANewUser() throws Exception {
//            Roles roles = new Roles();
//            roles.setRoleName("RECRUITER");
//            when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);
//            mockMvc.perform(
//                            post("/user/add")
//                                    .param("fullName", "Nguyen Van B")
//                                    .param("email", "namdeptrai2@gmail.com")
//                                    .param("dateOfBirth", "1980-02-02")
//                                    .param("address", "HN")
//                                    .param("phoneNumber", "0981149509")
//                                    .param("gender", "MALE")
//                                    .param("status", "ACTIVE")
//                                    .param("note", "deptrai")
//                                    .param("department", "1")
//                                    .param("rolesSet", roles.getRoleName())
//                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                    ).andDo(print())
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl("/user/list"));
//
//            verify(userService).save(any(User.class));
//
//        }

        @Test
        void testAddFailedBindingResultHasError() throws Exception {
            Roles roles = new Roles();
            roles.setRoleName("RECRUITER");
            when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);
            mockMvc.perform(
                            post("/user/add")
                                    .param("fullName", "Nguyen Van B")
//                                    .param("email", "namdeptrai2@gmail.com")
                                    .param("dateOfBirth", "1980-02-02")
                                    .param("address", "HN")
                                    .param("phoneNumber", "0981149509")
                                    .param("gender", "MALE")
                                    .param("status", "ACTIVE")
                                    .param("note", "deptrai")
                                    .param("department", "1")
                                    .param("rolesSet", roles.getRoleName())
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                    ).andDo(print())
                    .andExpect(view().name("user/add-new-user"));


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

//            @Test
//            public void shouldReturnStatusForbidden() throws Exception {
//                List<Roles> roles = List.of(new Roles());
//                List<Department> departments = List.of(new Department());
//
//                when(rolesService.findAllRoles()).thenReturn(roles);
//
//                when(departmentService.findAllDepartment()).thenReturn(departments);
//
//                mockMvc.perform(get("/user/add")
//                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                        ).andDo(print())
//                        .andExpect(status().isForbidden());
//            }

        }


    }

    @Nested
    @DisplayName("View User detail")
    public class viewUserDetail {

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
            public void shouldReturnViewDetailUserPage() throws Exception {
                Roles role = new Roles();
                role.setRoleName("ADMIN");
                Department department = new Department();
                department.setDepartmentName("IT");


                User user = User.builder().id(1)
                        .roles(Set.of(role))
                        .status(UserStatus.ACTIVE)
                        .address("HN")
                        .email("namdeptrai01@gmail.com")
                        .department(department)
                        .dateOfBirth(LocalDate.of(2000, 10, 10))
                        .fullName("vu truong nam")
                        .phoneNumber("0914655577")
                        .gender(Gender.MALE)
                        .note("oke con dê")
                        .build();

                Breadcrumb breadcrumb = new Breadcrumb("User detail", "/user/view/");
                Breadcrumb breadcrumb1 = new Breadcrumb("User list", "/user/list");

                when(breadcrumbService.getBreadcrumbUserDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbUserList()).thenReturn(breadcrumb1);

                when(userService.findByUserId(1)).thenReturn(Optional.of(Optional.of(user).orElse(null)));

                mockMvc.perform(get("/user/view/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("userDetail", user))
                        .andExpect(view().name("user/view-user-details"));
            }

            @Test
            public void shouldReturnErrorPage() throws Exception {

                User user = new User();
                user.setId(1);

                when(userService.findByUserId(1)).thenReturn(Optional.ofNullable(Optional.of(user).orElseThrow(EntityNotFoundException::new)));

                Breadcrumb breadcrumb = new Breadcrumb("User detail", "/user/view/");

                Breadcrumb breadcrumb1 = new Breadcrumb("User list", "/user/list");

                when(breadcrumbService.getBreadcrumbUserDetail()).thenReturn(breadcrumb);
                when(breadcrumbService.getBreadcrumbUserList()).thenReturn(breadcrumb1);


                mockMvc.perform(get("/user/view/2")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(view().name("error/404"));

            }

        }
    }

    @Nested
    @DisplayName("Edit User")
    public class EditUser {
//        @Nested
//        public class DoNotHavePermissionToEditJob {
//            @BeforeEach
//            void setUp() {
//
//                mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
//                        .apply(springSecurity()).build();
//                User user = new User();
//                user.setUsername("interviewer");
//                user.setPassword("123");
//                Roles roles = new Roles();
//                roles.setId(1);
//                roles.setRoleName("INTERVIEWER");
//                user.setRoles(Set.of(roles));
//
//                Department department = new Department();
//                department.setDepartmentName("Test");
//                user.setDepartment(department);
//
//                when(userRepository.findByUsernameIgnoreCaseAndStatus(anyString(), any(UserStatus.class)))
//                        .thenReturn(Optional.of(user));
//
//                var authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));
//
//                mockUser = new Users(user.getUsername(), user.getPassword(), user.getDepartment().getDepartmentName(), authorityList);
//                ;
//            }
//
//            @Test
//            public void shouldReturnStatusForbidden() throws Exception {
//
//                mockMvc.perform(get("/user/edit/1")
//                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                        ).andDo(print())
//                        .andExpect(status().isForbidden());
//            }
//
//        }

        @Nested
        public class HavePermissionToEditUser {

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
                User user = new User();

                EditUserDTO userDTO = new EditUserDTO();

                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
                when(rolesService.findAllRoles()).thenReturn(List.of(new Roles()));
                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));

                Breadcrumb breadcrumbList = new Breadcrumb("User list", "/user/list");

                Breadcrumb breadcrumbEdit = new Breadcrumb("Edit User", "/user/edit/");

                when(breadcrumbService.getBreadcrumbUserList()).thenReturn(breadcrumbList);
                when(breadcrumbService.getBreadcrumbUserDetail()).thenReturn(breadcrumbEdit);

                mockMvc.perform(get("/user/edit/" + anyInt())
                                .param("prevPage", "list")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("breadCrumbList", breadcrumbList))
                        .andExpect(view().name("user/edit-user-details"))
                        .andExpect(model().attribute("getEditUser", userDTO)

                        );
            }

            @Test
            public void shouldReturnStatusOkToAccessEditPageWithPrevPageNotEqualToList() throws Exception {
                User user = new User();

                EditUserDTO userDTO = new EditUserDTO();

                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
                when(rolesService.findAllRoles()).thenReturn(List.of(new Roles()));
                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));


                mockMvc.perform(get("/user/edit/" + anyInt())
                                .param("prevPage", "edit")
                                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(view().name("user/edit-user-details"))
                        .andExpect(model().attribute("getEditUser", userDTO)

                        );
            }

//            @Test
//            public void testEditUserSuccess() throws Exception {
//                Integer userId = 1;
//                User user = new User();
//                user.setId(userId);
//
//                Roles roles = new Roles();
//                roles.setRoleName("RECRUITER");
//                when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);
//
//                EditUserDTO editUserDTO = new EditUserDTO();
//
//                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
////                when(rolesService.findAllRoles()).thenReturn(List.of(new Roles()));
//                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));
//
//                mockMvc.perform(
//                                post("/user/edit/1")
//                                        .param("fullName", "Nguyen Van B")
//                                        .param("email", "namdeptrai2@gmail.com")
//                                        .param("dateOfBirth", "1980-02-02")
//                                        .param("address", "HN")
//                                        .param("phoneNumber", "0981149509")
//                                        .param("gender", "FEMALE")
//                                        .param("status", "ACTIVE")
//                                        .param("note", "deptrai")
//                                        .param("department", "1")
//                                        .param("rolesSet", roles.getRoleName())
//                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                        ).andDo(print())
//                        .andExpect(status().is3xxRedirection())
//                        .andExpect(redirectedUrl("/user/list"));
//
////                verify(userService).save(any(User.class));
//            }
//
//            @Test
//            public void testEditUserFailedBindingResultHasError() throws Exception {
//                Integer userId = 1;
//                User user = new User();
//                user.setId(userId);
//
//                Roles roles = new Roles();
//                roles.setRoleName("RECRUITER");
//                when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);
//
//                EditUserDTO editUserDTO = new EditUserDTO();
//
//                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
////                when(rolesService.findAllRoles()).thenReturn(List.of(new Roles()));
//                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));
//
//                mockMvc.perform(
//                                post("/user/edit/1")
//                                        .param("email", "namdeptrai2@gmail.com")
//                                        .param("dateOfBirth", "1980-02-02")
//                                        .param("address", "HN")
//                                        .param("phoneNumber", "0981149509")
//                                        .param("gender", "FEMALE")
//                                        .param("status", "ACTIVE")
//                                        .param("note", "deptrai")
//                                        .param("department", "1")
//                                        .param("rolesSet", roles.getRoleName())
//                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                        ).andDo(print())
//                        .andExpect(model().attributeExists("createFailed"))
//                        .andExpect(view().name("user/edit-user-details"));
//            }

            @Test
            public void EditFailed_DateOfBirthIsLessThanAndHasPrevPageEqualToList() throws Exception {
                Integer userId = 1;
                User user = new User();
                user.setId(userId);

                Roles roles = new Roles();
                roles.setRoleName("RECRUITER");
                when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);

                EditUserDTO editUserDTO = new EditUserDTO();
                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));

                mockMvc.perform(
                                post("/user/edit/1")
                                        .param("prevPage", "list")
                                        .param("fullName", "Nguyen Van B")
                                        .param("email", "namdeptrai2@gmail.com")
                                        .param("dateOfBirth", "2020-02-02")
                                        .param("address", "HN")
                                        .param("phoneNumber", "0981149509")
                                        .param("gender", "FEMALE")
                                        .param("status", "ACTIVE")
                                        .param("note", "deptrai")
                                        .param("department", "1")
                                        .param("rolesSet", roles.getRoleName())
                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(model().attributeHasFieldErrors("getEditUser", "dateOfBirth"))
                        .andExpect(view().name("user/edit-user-details"));
//

            }


            @Test
            public void prevPageNotEqualToList() throws Exception {
                Integer userId = 1;
                User user = new User();
                user.setId(userId);

                Roles roles = new Roles();
                roles.setRoleName("RECRUITER");
                when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);

                EditUserDTO editUserDTO = new EditUserDTO();

                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));

                mockMvc.perform(
                                post("/user/edit/1")
                                        .param("prevPage", "edit")
                                        .param("fullName", "Nguyen Van B")
                                        .param("email", "namdeptrai2@gmail.com")
                                        .param("dateOfBirth", "2020-02-02")
                                        .param("address", "HN")
                                        .param("phoneNumber", "0981149509")
                                        .param("gender", "FEMALE")
                                        .param("status", "ACTIVE")
                                        .param("note", "deptrai")
                                        .param("department", "1")
                                        .param("rolesSet", roles.getRoleName())
                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        ).andDo(print())
                        .andExpect(model().attributeHasFieldErrors("getEditUser", "dateOfBirth"))
                        .andExpect(view().name("user/edit-user-details"));

            }

//            @Test
//            public void userIsNull() throws Exception {
//                Integer userId = 1;
//                User user = new User();
//                user.setId(userId);
//
//                Roles roles = new Roles();
//                roles.setRoleName("RECRUITER");
//                when(rolesService.findByRoleName("RECRUITER")).thenReturn(roles);
//
//                EditUserDTO editUserDTO = new EditUserDTO();
//                when(userService.findByUserId(anyInt())).thenReturn(Optional.of(user));
//                when(departmentService.findAllDepartment()).thenReturn(List.of(new Department()));
//
//                mockMvc.perform(
//                                post("/user/edit/2")
//                                        .param("prevPage", "edit")
//                                        .param("fullName", "Nguyen Van B")
//                                        .param("email", "namdeptrai2@gmail.com")
//                                        .param("dateOfBirth", "1999-02-02")
//                                        .param("address", "HN")
//                                        .param("phoneNumber", "0981149509")
//                                        .param("gender", "FEMALE")
//                                        .param("status", "ACTIVE")
//                                        .param("note", "deptrai")
//                                        .param("department", "1")
//                                        .param("rolesSet", roles.getRoleName())
//                                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                        ).andDo(print())
//                        .andExpect(view().name("error/404"))
//                ;
//
//            }

        }

        @Nested
        @DisplayName("Delete job")
        public class deleteJob {

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
                }

                @Test
                public void shouldDeleteSuccessAndRedirectToJobListPage() throws Exception {
                    User user=new User();
                    user.setId(1);
                    mockMvc.perform(get("/user/delete/1")
                                    .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                            ).andDo(print())
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl("/user/list"));
                    verify(userService).deleteUser(1);
                }


            }

        }
    }
}