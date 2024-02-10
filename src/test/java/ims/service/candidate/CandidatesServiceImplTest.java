package ims.service.candidate;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.repository.candidates.CandidatesRepository;
import fa.training.fjb04.ims.repository.interview.InterviewRepository;
import fa.training.fjb04.ims.repository.offer.OfferRepository;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import fa.training.fjb04.ims.service.candidates.impl.CandidatesServiceImpl;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;
import fa.training.fjb04.ims.util.page.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
@SpringBootTest

public class CandidatesServiceImplTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private CandidatesRepository candidatesRepository = Mockito.mock(CandidatesRepository.class);;
    private MockMvc mockMvc;
    private UserDetails mockUser;
    private Model model;
    @Mock
    private CandidateStatusService candidateStatusService;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private CandidatesServiceImpl candidatesService;

    private List<Candidates> candidatesList;
    private Candidates candidate;

    @BeforeEach
    void init() {
        candidatesList = new ArrayList<>();
        candidate = new Candidates();
        candidate.setId(1);
        candidatesList.add(candidate);
    }

    @Nested
    class TestGetPageCandidates {


        @Test
        void testGetPageCandidates() {

            Integer pageIndex = 1;
            Integer pageSize = 5;
            String search = "John";
            String field = "fullName";


            when(candidatesRepository.getCandidatesPaging(pageIndex, pageSize, search, field)).thenReturn(new ArrayList<>());
            when(candidatesRepository.getCandidatesSearch(search, field)).thenReturn(new ArrayList<>());


            Page<CandidatesListDTO> result = candidatesService.getPageCandidates(pageIndex, pageSize, search, field);


            Assertions.assertNotNull(result);
            Assertions.assertEquals(0, result.getData().size());
        }
    }

    @Nested
    class TestGetPagePosition {

        @Test
        void testGetPagePosition() {

            Integer candidateId = 1;


            CandidatePositionDTO positionDTO = new CandidatePositionDTO();
            when(candidatesRepository.getPositionById(candidateId)).thenReturn(positionDTO);


            CandidatePositionDTO result = candidatesService.getPagePosition(candidateId);


            Assertions.assertNotNull(result);
        }
    }

    @Nested
    class TestGetPageRecruiter {

        @Test
        void testGetPageRecruiter() {

            Integer candidateId = 1;

            CandidateRecruiterDTO recruiterDTO = new CandidateRecruiterDTO();
            when(candidatesRepository.getRecruiterById(candidateId)).thenReturn(recruiterDTO);


            CandidateRecruiterDTO result = candidatesService.getPageRecruiter(candidateId);

            Assertions.assertNotNull(result);
        }
    }

    @Nested
    class TestFindByCandidateId {

        @Test
        void testFindByCandidateId() {

            Integer candidateId = 1;

            when(candidatesRepository.findById(candidateId)).thenReturn(Optional.of(candidate));


            Optional<Candidates> result = candidatesService.findByCandidateId(candidateId);


            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals(candidateId, result.get().getId());
        }
    }

    @Nested
    class TestSave {

        @Test
        void testSave() {
            Candidates newCandidate = new Candidates();


            when(candidatesRepository.save(newCandidate)).thenReturn(newCandidate);


            Candidates result = candidatesService.save(newCandidate);

            Assertions.assertNotNull(result);
        }
    }

    @Test
    void testExistsByPhone() {
        // Arrange
        String phoneNumber = "0123456789";

        // Mock the behavior of CandidatesRepository
        when(candidatesRepository.existsByPhone(phoneNumber)).thenReturn(true);

        // Act
        Boolean result = candidatesService.existsByPhone(phoneNumber);

        // Assert
        assertTrue(result);

        verify(candidatesRepository, times(1)).existsByPhone(phoneNumber);
    }

    @Test
    void testExistsByEmail() {
        // Arrange
        String email = "quyet@gmail.com";


        when(candidatesRepository.existsByEmail(email)).thenReturn(true);

        // Act
        Boolean result = candidatesService.existsByEmail(email);

        // Assert
        assertTrue(result);

        verify(candidatesRepository, times(1)).existsByEmail(email);
    }
}

