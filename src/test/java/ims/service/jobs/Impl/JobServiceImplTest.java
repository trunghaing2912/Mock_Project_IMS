package ims.service.jobs.Impl;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.enums.job.JobStatus;
import fa.training.fjb04.ims.repository.interview.InterviewRepository;
import fa.training.fjb04.ims.repository.job.JobsRepository;
import fa.training.fjb04.ims.service.jobs.Impl.JobServiceImpl;
import fa.training.fjb04.ims.util.dto.jobs.JobsListDTO;
import fa.training.fjb04.ims.util.dto.jobs.LevelListDTO;
import fa.training.fjb04.ims.util.dto.jobs.SkillListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageLevel;
import fa.training.fjb04.ims.util.page.PageSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(PowerMockRunner.class)
class JobServiceImplTest {

    @Mock
    private JobsRepository jobsRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @InjectMocks
    private JobServiceImpl jobServiceImpl;
    private Jobs jobs;

    @BeforeEach
    public void setUp() {

        jobsRepository = mock(JobsRepository.class);
        jobServiceImpl = new JobServiceImpl(jobsRepository, interviewRepository);

        jobs = Jobs.builder()
                .id(1)
                .benefitSet(new HashSet<>())
                .deleted(false)
                .description("ABC")
                .endDate(LocalDate.now().plusDays(10))
                .startDate(LocalDate.now())
                .minSalary(new BigDecimal("1000"))
                .maxSalary(new BigDecimal("2000"))
                .title("Fresher Java")
                .status(JobStatus.DRAFT)
                .interviewList(new ArrayList<>())
                .levelsSet(new HashSet<>())
                .workingAddress("HA NOI")
                .skillsSet(new HashSet<>())
                .build();
    }


    @Test
    public void test_save_job_success() {

        when(jobsRepository.save(jobs)).thenReturn(jobs);

        Jobs savedJob = jobServiceImpl.save(jobs);

        assertSame(jobs, savedJob);
    }

    @Test
    public void testFindAll() {

        when(jobsRepository.findAll()).thenReturn(List.of(jobs));
        List<Jobs> list = jobServiceImpl.findAll();
        assertEquals(1, list.size());

    }

    @Test
    public void testSaveAllJob() {

        List<Jobs> jobsList = List.of(jobs);
        jobServiceImpl.saveAllJob(jobsList);
        verify(jobsRepository).saveAll(jobsList);

    }

    @Test
    public void testUpdateStatus() {

        LocalDate currentDate = LocalDate.now();
        LocalDateTime checkTime = LocalDate.now().atStartOfDay().plusDays(1);
        Jobs jobToOpen = new Jobs();
        jobToOpen.setStartDate(currentDate);
        jobToOpen.setStatus(JobStatus.DRAFT);
        Jobs jobToClose = new Jobs();
        jobToClose.setEndDate(LocalDate.from(checkTime));
        jobToClose.setStatus(JobStatus.OPEN);

        when(jobsRepository.findByStartDateAndStatus(currentDate, JobStatus.DRAFT)).thenReturn(Collections.singletonList(jobToOpen));
        when(jobsRepository.findByEndDateAndStatus(LocalDate.from(checkTime), JobStatus.OPEN)).thenReturn(Collections.singletonList(jobToClose));

        jobServiceImpl.updateJobStatus();

        assertEquals(JobStatus.OPEN, jobToOpen.getStatus());
        assertEquals(JobStatus.CLOSED, jobToClose.getStatus());

        verify(jobsRepository, times(1)).save(jobToOpen);
        verify(jobsRepository, times(1)).save(jobToClose);

    }
    @Test
    public void testFindByJobId() {

        Jobs job = new Jobs();
        job.setId(1);

        when(jobsRepository.findById(1)).thenReturn(Optional.of(job));

        Optional<Jobs> result = jobServiceImpl.findByJobId(1);

        verify(jobsRepository).findById(1);

        assertTrue(result.isPresent());
        assertEquals(job, result.get());
    }
    @Test
    public void testFindByJobIdNotFound() {
        when(jobsRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Jobs> result = jobServiceImpl.findByJobId(1);

        verify(jobsRepository).findById(1);

        assertFalse(result.isPresent());
    }
    @Test
    public void testDeleteJob() {

        Jobs job = new Jobs();
        job.setId(1);

        Interview interview = new Interview();
        interview.setId(1);
        interview.setJobs(job);

        when(jobsRepository.findById(1)).thenReturn(Optional.of(job));
        when(interviewRepository.findByJobId(1)).thenReturn(Collections.singletonList(interview));

        jobServiceImpl.deleteJob(1);

        assertEquals(true, interview.isDeleted());
        verify(interviewRepository).save(interview);

        assertEquals(true, job.isDeleted());
        verify(jobsRepository).save(job);

        assertEquals(LocalDate.now(), job.getEndDate());
    }

    @Test
    public void testDeleteJobNotFound() {

        when(jobsRepository.findById(1)).thenReturn(Optional.empty());

        jobServiceImpl.deleteJob(1);

        verify(jobsRepository).findById(1);

        verify(jobsRepository, never()).save(any(Jobs.class));
    }
    @Test
    public void testChangeStatus() {

        Jobs job = new Jobs();
        job.setId(1);
        job.setStartDate(LocalDate.now().minusDays(1));
        job.setEndDate(LocalDate.now().plusDays(1));

        when(jobsRepository.findById(1)).thenReturn(Optional.of(job));
        when(jobsRepository.save(job)).thenReturn(job);

        Jobs result = jobServiceImpl.changeStatus(1);

        verify(jobsRepository).findById(1);

        assertEquals(JobStatus.OPEN, result.getStatus());
        verify(jobsRepository).save(job);
    }

    @Test
    public void testChangeStatusBeforeStartDate() {

        Jobs job = new Jobs();
        job.setId(1);
        job.setStartDate(LocalDate.now().plusDays(1));
        job.setEndDate(LocalDate.now().plusDays(2));

        when(jobsRepository.findById(1)).thenReturn(Optional.of(job));
        when(jobsRepository.save(job)).thenReturn(job);

        Jobs result = jobServiceImpl.changeStatus(1);

        verify(jobsRepository).findById(1);

        assertEquals(JobStatus.DRAFT, result.getStatus());
        verify(jobsRepository).save(job);
    }

    @Test
    public void testGetJobsHaveStatusOpen() {

        List<Jobs> jobsList = new ArrayList<>();
        Jobs job1 = new Jobs();
        job1.setId(1);
        job1.setStatus(JobStatus.OPEN);
        jobsList.add(job1);
        Jobs job2 = new Jobs();
        job2.setId(2);
        job2.setStatus(JobStatus.OPEN);
        jobsList.add(job2);

        when(jobsRepository.getJobsHaveStatusOpen()).thenReturn(jobsList);

        List<Jobs> result = jobServiceImpl.getJobsHaveStatusOpen();

        verify(jobsRepository).getJobsHaveStatusOpen();

        assertEquals(jobsList, result);
    }
    @Test
    public void testFindByTitle() {

        Jobs job = new Jobs();
        job.setId(1);
        job.setTitle("Software Engineer");

        when(jobsRepository.findByTitle("Software Engineer")).thenReturn(job);

        Jobs result = jobServiceImpl.findByTitle("Software Engineer");

        verify(jobsRepository).findByTitle("Software Engineer");

        assertEquals(job, result);
    }

    @Test
    public void testGetPageJob() {

        mockStatic(SecurityUtils.class);
        Collection<? extends GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("ADMIN"));
        when(SecurityUtils.getCurrentRole()).thenAnswer(invocation -> roles);

        List<JobsListDTO> dummyJobs = new ArrayList<>();
        when(jobsRepository.getJobPaging(0, 10, "search", "status")).thenReturn(dummyJobs);
        when(jobsRepository.getAllJob("search", "status")).thenReturn(dummyJobs);

        Page<JobsListDTO> result = jobServiceImpl.getPageJob(0, 10, "search", "status");

        verify(jobsRepository).getJobPaging(0, 10, "search", "status");
        verify(jobsRepository).getAllJob("search", "status");

        assertEquals(1, result.getTotalPage());
        assertEquals(0, result.getPageIndex());
        assertEquals(10, result.getPageSize());
        assertEquals(roles.toString(), result.getRole());
    }
    @Test
    public void testGetPageSkill() {

        List<SkillListDTO> skillListDTOList = new ArrayList<>();

        when(jobsRepository.getSkillsById(anyInt())).thenReturn(skillListDTOList);

        Integer skillId = 1;

        PageSkill<SkillListDTO> result = jobServiceImpl.getPageSkill(skillId);

        verify(jobsRepository).getSkillsById(skillId);

        assertEquals(skillListDTOList, result.getSkills());
    }
    @Test
    public void testGetPageLevel() {

        List<LevelListDTO> levelListDTOList = new ArrayList<>();

        when(jobsRepository.getLevelsById(anyInt())).thenReturn(levelListDTOList);

        Integer levelId = 1;

        PageLevel<LevelListDTO> result = jobServiceImpl.getPageLevel(levelId);

        verify(jobsRepository).getLevelsById(levelId);

        assertEquals(levelListDTOList, result.getLevels());
    }

}


