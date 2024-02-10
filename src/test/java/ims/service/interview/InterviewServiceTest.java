package ims.service.interview;

import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.repository.interview.InterviewRepository;
import fa.training.fjb04.ims.repository.interview.impl.InterviewRepositoryCustomImpl;
import fa.training.fjb04.ims.service.interview.impl.InterviewServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class InterviewServiceTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private InterviewRepository interviewRepository = Mockito.mock(InterviewRepository.class);

    @MockBean
    private InterviewRepositoryCustomImpl interviewRepositoryCustom;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private List<Interview> interviewList() {
        Interview interview = new Interview();
        interview.setId(1);
        return List.of(interview);
    }

    private Interview interview() {
        Interview interview = new Interview();
        interview.setId(1);
        return interview;
    }

    @Nested
    @DisplayName("Test find all")
    public class TestFindAll {
        @BeforeEach
        void init() {
            List<Interview> list = interviewList();
            when(interviewRepository.findAll()).thenReturn(list);
        }

        @Test
        public void testFindAll() {
            List<Interview> response = interviewService.findAll();
            Assertions.assertEquals(1, response.size());
        }
    }

    @Nested
    @DisplayName("Test find all by ID")
    public class TestFindById {
        @BeforeEach
        void init() {
            Interview interview = interview();
            when(interviewRepository.findById(interview.getId())).thenReturn(Optional.of(interview));
        }

        @Test
        public void testFindById() {
            Integer id = 1;

            Optional<Interview> response = interviewService.findById(id);

            Assertions.assertEquals(1, response.get().getId());
        }
    }

    @Nested
    @DisplayName("Test save")
    public class TestSave {
        @BeforeEach
        void init() {
            Interview interview = interview();
        }

        @Test
        public void testSave() {
            Interview interview = interview();

            Assertions.assertDoesNotThrow(() -> interviewService.save(interview));
        }
    }
}
