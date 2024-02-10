package ims.api.user;

import fa.training.fjb04.ims.api.user.ApiUserController;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.controller.user.UserController;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.user.RoleListDTO;
import fa.training.fjb04.ims.util.page.PageRoles;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiUserController.class)
public class ApiUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Nested
    @DisplayName("Find Role by ID and Paging")
    class FindById {
        private  final  Integer ROLES_ID=1;

        @Nested
        @DisplayName("when request role not found ")
        class WhenRequestedRoleNotFound {
            @BeforeEach
            public void mockService() {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                when(userService.findByUserId(any())).thenThrow(new RuntimeException());
            }

            @Test
            @DisplayName("Should return HTTP response which has an empty response body")
            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
                mockMvc.perform(get("/api/user/roles/{id}", ROLES_ID))
                        .andExpect(content().string(""));
            }
        }

        @Nested
        @DisplayName("When the requested role is found")
        class WhenRequestedRoleFound {
            private static final Integer ROLE_ID = 1;
            private static final String ROLE_NAME = "ADMIN";

            private static ResultActions resultActions;


            @BeforeEach
            public void mockService() throws Exception {
                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                RoleListDTO roleListDTO=new RoleListDTO(ROLE_NAME);
                List<RoleListDTO> lst=List.of(roleListDTO);
                when(userService.getPageRole(anyInt())).thenReturn(new PageRoles<>(lst));

                resultActions = mockMvc.perform(get("/api/user/roles/{id}", ROLES_ID));
            }

            @Test
            @DisplayName("Should return the HTTP status code ok (200)")
            void shouldReturnHttpStatusCodeOk() throws Exception {
                resultActions
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return the information of the found role as JSON")
            void shouldReturnInformationOfFoundLevelAsJSON() throws Exception {
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

        }

    }
//    @Nested
//    @DisplayName("Page of user")
//    public class PageOfUser {
//        @Nested
//        @DisplayName("When user list is empty")
//        public class WhenUserListIsEmpty{
//
//            @BeforeEach
//            public void mockService() {
//
//                UserDetails userDetails = new Users("admin", "admin", "HR", List.of(new SimpleGrantedAuthority("ADMIN")));
//                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(auth);
//
//                when(userService.findByUserId(any())).thenThrow(new RuntimeException());
//            }
//
//            @Test
//            @DisplayName("Should return status 200 and empty response body")
//            public void shouldReturnHttpResponseWhichHasEmptyResponseBody() throws Exception {
//                Integer totalPage = 5;
//                Integer pageIndex = 1;
//                Integer pageSize = 7;
//                String role = "ADMIN";
//                String search = "OPEN";
//                String field = "Status";
//
//                List<UserListDTO> userListDTOS= Collections.emptyList();
//                Page<UserListDTO> userListDTOPage=new Page<>(totalPage,pageIndex,userListDTOS,pageSize,role);
//
//                when(userService.getPageUser(pageIndex, pageSize, search, field)).thenReturn(userListDTOPage);
//
//                mockMvc.perform(get("/api/user")
//                                .param("pageIndex", String.valueOf(pageIndex))
//                                .param("pageSize", String.valueOf(pageSize))
//                                .param("search", search)
//                                .param("field", field))
//                        .andExpect(jsonPath("$.data", hasSize(0)));
//            }
//        }
//    }
}
