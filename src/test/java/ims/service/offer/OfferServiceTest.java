package ims.service.offer;

import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.repository.offer.Impl.OfferRepositoryCustomImpl;
import fa.training.fjb04.ims.repository.offer.OfferRepository;
import fa.training.fjb04.ims.service.file.FileStorageService;
import fa.training.fjb04.ims.service.offer.impl.OfferServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class OfferServiceTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private OfferRepository offerRepository = Mockito.mock(OfferRepository.class);
    @MockBean
    private FileStorageService fileStorageService;
    @MockBean
    private OfferRepositoryCustomImpl offerRepositoryCustom;
    private MockMvc mockMvc;
    private UserDetails mockUser;
    private Model model;
    @InjectMocks
    private OfferServiceImpl offerService;

    private List<Offer> offerList () {
      Offer offer = new Offer();
      offer.setId(1);
      return List.of(offer);
    };

    private Offer offer() {
        Offer offer = new Offer();
        offer.setId(1);
        return offer;
    }


    @Nested
    @DisplayName("Test find All")
    public class TestFindAll {
        @BeforeEach
        void init() {
            List<Offer> list = offerList();
            when(offerRepository.findAll()).thenReturn(list);
        }
        @Test
        public void testFindAll() {

            List<Offer> response = offerService.findAll();

            Assertions.assertEquals(1, response.size());

        }
    }

    @Nested
    @DisplayName("Test find ByDueDate")
    public class TestFindByDueDate {
        @BeforeEach
        void init() {
            List<Offer> list = offerList();
            LocalDate date = LocalDate.now();
            when(offerRepository.findByDueDate(date)).thenReturn(list);
        }
        @Test
        public void testFindByDueDate() {

            LocalDate date = LocalDate.now();

            List<Offer> response = offerService.findByDueDate(date);

            Assertions.assertEquals(1, response.size());

        }
    }

    @Nested
    @DisplayName("Test find All ByDueDate")
    public class TestFindAllByDueDate {
        @BeforeEach
        void init() {
            List<Offer> list = offerList();
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(1);
            when(offerRepository.findByDueDateBetween(startDate, endDate)).thenReturn(list);
        }
        @Test
        public void testFindAllByDueDate() {

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(1);

            List<Offer> response = offerService.findAllByDate(startDate, endDate);

            Assertions.assertEquals(1, response.size());

        }
    }

    @Nested
    @DisplayName("Test find By Id")
    public class TestFindById {
        @BeforeEach
        void init() {
            Offer offer = offer();
            when(offerRepository.findById(offer.getId())).thenReturn(Optional.of(offer));
        }
        @Test
        public void testFindById() {

            Integer id = 1;

            Optional<Offer> response = offerService.findById(id);

            Assertions.assertEquals(1, response.get().getId());

        }
    }

    @Nested
    @DisplayName("Test save")
    public class TestSave {
        @BeforeEach
        void init() {
            Offer offer = offer();
        }
        @Test
        public void testFindById() {
            Offer offer = offer();

            Assertions.assertDoesNotThrow(() -> offerService.save(offer));

        }
    }

}
