package ims.service.user;

import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.user.Gender;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private User user;

    @BeforeEach
    public void setUp(){
        user=User.builder()
                .roles(new HashSet<>())
                .email("namdeptrai99@gmail.com")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.parse("2020-01-01"))
                .department(new Department())
                .phoneNumber("098149789")
                .status(UserStatus.valueOf("ACTIVE"))
                .address("HN")
                .fullName("VU NAM")
                .note("ok")
                .build();

    }
    @Nested
    @SpringBootTest
    class saveUser{
        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserServiceImpl userServiceImpl;

        @Test
        public void test_save_user_success(){
            when(userRepository.save(user)).thenReturn(user);

            User saveUser=userServiceImpl.saveUser(user);

            assertSame(user, saveUser);
        }
    }

    @Nested
    @SpringBootTest
    public class findAll{
        @Mock
        UserRepository userRepository;

        @InjectMocks
        UserServiceImpl userServiceImpl;

        @Test
        public void testFindAll(){

            when(userRepository.findAll()).thenReturn(List.of(user));
            List<User> list = userServiceImpl.findAll();
            assertEquals(1,list.size());
        }

    }
    @Nested
    @SpringBootTest
    public class saveAllUser {

        @Mock
        UserRepository userRepository;

        @InjectMocks
        UserServiceImpl userServiceImpl;

        @Test
        public void testFindAll(){

            List<User>userList= List.of(user);
            userServiceImpl.saveAllUser(userList);
            verify(userRepository).saveAll(userList);
        }

    }
}
