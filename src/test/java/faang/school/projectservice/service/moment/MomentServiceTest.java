package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Spy
    private MomentRepository momentRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Spy
    @InjectMocks
    private MomentService momentService;


    private Project project = new Project();
    private ProjectDto projectDto = new ProjectDto();
    private String momentName = "All subprojects finished";

    @BeforeEach
    void setUp() {
        when(projectMapper.toDto(project)).thenReturn(projectDto);
    }

    @Test
    public void testAddMomentByName() {
        momentService.addMomentByName(project, momentName);
        verify(momentService, times(1)).updateMoment(any(), any());
        verify(momentService, times(0)).createMoment(any(), any());
    }

    @Test
    public void testFindMomentByName() {
        Moment newMoment = new Moment();
        newMoment.setId(2L);
        newMoment.setName("name");
        newMoment.setDescription("description");
        newMoment.setDate(LocalDateTime.now());
        momentRepository.save(newMoment);
        Optional<Moment> result = momentRepository.findById(2L);

//        Optional<Moment> result = momentService.findMomentByName(momentName);
        assertTrue(result.isPresent());
        assertEquals(momentName, result.get().getName());
    }
}
