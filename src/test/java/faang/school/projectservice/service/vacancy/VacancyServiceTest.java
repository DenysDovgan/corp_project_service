package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.filter.VacancyNameFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Mock
    private CandidateService candidateService;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private VacancyService vacancyService;

    @BeforeEach
    public void setUp() {
        List<VacancyFilter> vacancyFilters = List.of(new VacancyNameFilter());
        ReflectionTestUtils.setField(vacancyService, "vacancyFilters", vacancyFilters);
    }

    @Test
    public void createVacancyWithoutCuratorTest() {
        VacancyDto vacancyDto = new VacancyDto();

        assertThrows(DataValidationException.class, () -> vacancyService.createVacancy(vacancyDto));
    }

    @Test
    public void createVacancyWithCuratorNotManagerTest() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setCreatedBy(1L);
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.TESTER));
        when(teamMemberService.findById(1L)).thenReturn(teamMember);

        assertThrows(DataValidationException.class, () -> vacancyService.createVacancy(vacancyDto));
    }

    @Test
    public void createVacancyTest() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setName("New vacancy");
        vacancyDto.setCreatedBy(10L);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.MANAGER));
        when(teamMemberService.findById(10L)).thenReturn(teamMember);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        VacancyDto createVacancy = vacancyService.createVacancy(vacancyDto);

        assertEquals(vacancyDto.getId(), createVacancy.getId());
        assertEquals(vacancyDto.getName(), createVacancy.getName());
    }

    @Test
    public void updateVacancyToCloseAndCandidatesNotEnoughTest() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setCount(2);
        vacancy.setCandidates(List.of(new Candidate()));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);

        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(vacancyDto));
    }

    @Test
    public void updateVacancyToCloseTest() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setCount(1);
        vacancy.setCandidates(List.of(new Candidate()));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        VacancyDto updateVacancy = vacancyService.updateVacancy(vacancyDto);

        assertEquals(vacancy.getId(), updateVacancy.getId());
        assertEquals(vacancy.getCount(), updateVacancy.getCount());
    }

    @Test
    public void updateVacancyTest() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCount(2);
        vacancy.setCandidates(List.of(new Candidate()));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        VacancyDto updateVacancy = vacancyService.updateVacancy(vacancyDto);

        assertEquals(vacancy.getId(), updateVacancy.getId());
        assertEquals(vacancy.getCount(), updateVacancy.getCount());
    }

    @Test
    public void deleteVacancyWithEmptyIdTest() {
        Long id = 2L;
        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(id));
    }

    @Test
    public void deleteVacancyTest() {
        Long id = 3L;
        Vacancy vacancy = new Vacancy();
        Candidate firstCandidate = new Candidate();
        firstCandidate.setId(1L);
        Candidate secondCandidate = new Candidate();
        secondCandidate.setId(2L);
        vacancy.setCandidates(List.of(firstCandidate, secondCandidate));
        when(vacancyRepository.findById(id)).thenReturn(Optional.of(vacancy));

        vacancyService.deleteVacancy(id);

        verify(vacancyRepository, times(1)).deleteById(id);
    }

    @Test
    public void getVacanciesByFilterTest() {
        VacancyFilterDto nameFilter = new VacancyFilterDto("First");
        Vacancy firstVacancy = new Vacancy();
        firstVacancy.setId(1L);
        firstVacancy.setName("First vacancy");
        Vacancy secondVacancy = new Vacancy();
        secondVacancy.setId(2L);
        secondVacancy.setName("Second vacancy");
        when(vacancyRepository.findAll()).thenReturn(List.of(firstVacancy, secondVacancy));

        List<VacancyDto> vacanciesByFilter = vacancyService.getVacanciesByFilter(nameFilter);

        assertEquals(1, vacanciesByFilter.size());
        assertEquals(firstVacancy.getId(), vacanciesByFilter.get(0).getId());
        assertEquals(firstVacancy.getName(), vacanciesByFilter.get(0).getName());
    }

    @Test
    public void findByIdWithEmptyIdTest() {
        Long id = 2L;
        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> vacancyService.findById(id));
    }

    @Test
    public void findByIdTest() {
        Long id = 3L;
        Vacancy vacancy = new Vacancy();
        when(vacancyRepository.findById(id)).thenReturn(Optional.of(vacancy));

        VacancyDto findVacancy = vacancyService.findById(id);

        assertEquals(vacancy.getId(), findVacancy.getId());
    }
}