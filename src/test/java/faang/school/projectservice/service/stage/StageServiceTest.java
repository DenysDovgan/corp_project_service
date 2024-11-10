package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.dto.stage.ProjectDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.stage.TaskDto;
import faang.school.projectservice.mapper.executor.ExecutorMapperImpl;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.role.StageRolesMapperImpl;
import faang.school.projectservice.mapper.stage.StageMapperGeneralImpl;
import faang.school.projectservice.mapper.stage.StageMapperWithRolesToFillImpl;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.stage.filters.StageFilter;
import faang.school.projectservice.service.stage.filters.StageTaskStatusFilter;
import faang.school.projectservice.service.stage.filters.StageTeamRoleFilter;
import faang.school.projectservice.validator.Stage.StageValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageValidator stageValidator;
    @InjectMocks
    private StageService stageService;

    private StageMapperGeneralImpl stageMapperGeneral;
    private StageMapperWithRolesToFillImpl stageMapperWithRolesToFill;

    private Stage stage;
    private Project projectInProgress;
    private Task testTask;
    private TeamMember teamMemberOwner;
    private TeamMember teamMemberDesigner;
    private TeamMember teamMemberDeveloper;
    private StageRoles stageRolesOwner;
    private StageRoles stageRolesDesigner;
    private StageRoles stageRolesDeveloper;

    private StageDtoGeneral stageDtoGeneral;
    private StageDtoGeneral stageDtoGeneralWithRolesToFill;
    private ProjectDto projectDto;
    private TaskDto taskDto;
    private ExecutorDto executorDtoOwner;
    private ExecutorDto executorDtoDesigner;
    private ExecutorDto executorDtoDeveloper;
    private StageRolesDto stageRolesDtoOwner;
    private StageRolesDto stageRolesDtoDesigner;
    private StageRolesDto stageRolesDtoDeveloper;

    @BeforeEach
    void setUp() {
        List<StageFilter> stageFilters = List.of(
                new StageTaskStatusFilter(),
                new StageTeamRoleFilter());
        ReflectionTestUtils.setField(stageService, "stageFilters", stageFilters);

        ExecutorMapperImpl executorMapper = new ExecutorMapperImpl();
        ProjectMapperImpl projectMapper = new ProjectMapperImpl();
        StageRolesMapperImpl rolesMapper = new StageRolesMapperImpl();
        TaskMapperImpl taskMapper = new TaskMapperImpl();

        stageMapperGeneral = new StageMapperGeneralImpl(
                projectMapper,
                rolesMapper,
                taskMapper,
                executorMapper);
        stageMapperWithRolesToFill = new StageMapperWithRolesToFillImpl(
                projectMapper,
                rolesMapper,
                taskMapper,
                executorMapper);
    }

    @BeforeEach
    public void init() {
        projectInProgress = new Project();
        projectInProgress.setId(1L);
        projectInProgress.setName("Test Project In Progress");

        //Initializing Task
        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");
        testTask.setStatus(TaskStatus.IN_PROGRESS);

        // Initialize TeamMembers
        teamMemberOwner = new TeamMember();
        teamMemberOwner.setId(1L);
        teamMemberOwner.setRoles(List.of(TeamRole.OWNER));
        //stageRolesOwner.setStage(stage);

        teamMemberDesigner = new TeamMember();
        teamMemberDesigner.setId(2L);
        teamMemberDesigner.setRoles(List.of(TeamRole.DESIGNER));
        //stageRolesDesigner.setStage(stage);

        teamMemberDeveloper = new TeamMember();
        teamMemberDeveloper.setId(3L);
        teamMemberDeveloper.setRoles(List.of(TeamRole.DEVELOPER));
        //stageRolesDeveloper.setStage(stage);

        //Initialize Roles
        stageRolesOwner = new StageRoles();
        stageRolesOwner.setId(1L);
        stageRolesOwner.setTeamRole(TeamRole.OWNER);
        //teamMemberOwner.setStages(stage);

        stageRolesDesigner = new StageRoles();
        stageRolesDesigner.setId(2L);
        stageRolesDesigner.setTeamRole(TeamRole.DESIGNER);
        //teamMemberDesigner.setStages(stage);

        stageRolesDeveloper = new StageRoles();
        stageRolesDeveloper.setId(3L);
        stageRolesDeveloper.setTeamRole(TeamRole.DEVELOPER);
        //teamMemberDeveloper.setStages(stage);

        //Initializing Stage
        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Test Stage");
        stage.setProject(projectInProgress);
        stage.setTasks(List.of(testTask));
        stage.setStageRoles(List.of(stageRolesOwner, stageRolesDesigner, stageRolesDeveloper));
        stage.setExecutors(List.of(teamMemberOwner, teamMemberDesigner, teamMemberDeveloper));

        // Initializing ProjectDto
        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project In Progress");

        // Initializing TaskDto
        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task");
        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        // Initialize ExecutorDtos
        executorDtoOwner = new ExecutorDto();
        executorDtoOwner.setTeamMemberId(1L);
        executorDtoOwner.setRoles(List.of(TeamRole.OWNER));

        executorDtoDesigner = new ExecutorDto();
        executorDtoDesigner.setTeamMemberId(2L);
        executorDtoDesigner.setRoles(List.of(TeamRole.DESIGNER));

        executorDtoDeveloper = new ExecutorDto();
        executorDtoDeveloper.setTeamMemberId(3L);
        executorDtoDeveloper.setRoles(List.of(TeamRole.DEVELOPER));

        // Initialize RolesDtos
        stageRolesDtoOwner = new StageRolesDto();
        stageRolesDtoOwner.setId(1L);
        stageRolesDtoOwner.setTeamRole(TeamRole.OWNER);
        stageRolesDtoOwner.setStageId(1L);

        stageRolesDtoDesigner = new StageRolesDto();
        stageRolesDtoDesigner.setId(2L);
        stageRolesDtoDesigner.setTeamRole(TeamRole.DESIGNER);
        stageRolesDtoDesigner.setStageId(1L);

        stageRolesDtoDeveloper = new StageRolesDto();
        stageRolesDtoDeveloper.setId(3L);
        stageRolesDtoDeveloper.setTeamRole(TeamRole.DEVELOPER);
        stageRolesDtoDeveloper.setStageId(1L);

        // Initializing StageDtoGeneral
        stageDtoGeneral = new StageDtoGeneral();
        stageDtoGeneral.setId(1L);
        stageDtoGeneral.setName("Test Stage");
        stageDtoGeneral.setProject(projectDto);
        stageDtoGeneral.setTasksActiveAtStage(List.of(taskDto));
        stageDtoGeneral.setRolesActiveAtStage(List.of(stageRolesDtoDesigner, stageRolesDtoOwner, stageRolesDtoDeveloper));
        stageDtoGeneral.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner, executorDtoDeveloper));

        stageDtoGeneralWithRolesToFill = new StageDtoGeneral();
        stageDtoGeneralWithRolesToFill.setId(2L);
        stageDtoGeneralWithRolesToFill.setName("Test Stage Role Missing");
        stageDtoGeneralWithRolesToFill.setProject(projectDto);
        stageDtoGeneralWithRolesToFill.setTasksActiveAtStage(List.of(taskDto));
        stageDtoGeneralWithRolesToFill.setRolesActiveAtStage(List.of(stageRolesDtoOwner, stageRolesDtoDesigner, stageRolesDtoDeveloper));
        stageDtoGeneralWithRolesToFill.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner));
    }

    @Test
    void teatCreateSavesStage() {
        doNothing().when(stageValidator).validateProjectNotClosed(stageDtoGeneralWithRolesToFill.getProject().getId());
        doNothing().when(stageValidator).validateEveryTeamMemberHasRoleAtStage(stageDtoGeneralWithRolesToFill);

        stageDtoGeneralWithRolesToFill.setId(null);
        Stage stageEntity = stageMapperGeneral.toEntity(stageDtoGeneralWithRolesToFill);
        stageEntity.setStageId(1L);

        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
            Stage savedStage = invocation.getArgument(0);
            stage.setStageId(1L);
            return savedStage;
        });

        StageDtoWithRolesToFill stageDtoOnReturn = stageMapperWithRolesToFill.toDto(stageEntity);

        stageService.create(stageDtoGeneralWithRolesToFill);

        ArgumentCaptor<Stage> stageCaptor = ArgumentCaptor.forClass(Stage.class);
        verify(stageRepository, times(1)).save(stageCaptor.capture());
        Stage capturedStage = stageCaptor.getValue();

        assertEquals(stageDtoGeneralWithRolesToFill.getName(), stageDtoOnReturn.getName());
        assertNotNull(capturedStage.getStageId());
        assertEquals(stageDtoOnReturn.getRolesToBeFilled(), List.of(stageRolesDtoDeveloper));
    }

    @Test
    void getByFilter() {
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void getAll() {
    }

    @Test
    void deleteById() {
    }
}