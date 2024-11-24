package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team_member.TeamMemberService;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class StageService {

    private final StageJpaRepository stageRepository;
    private final StageRolesRepository stageRolesRepository;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageValidator stageValidator;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;

    public StageDto createStage(StageDto stageDto) {
        List<StageRoles> stageRoles = stageRolesRepository.findAllById(stageDto.getStageRolesId());
        Project project = projectService.getProjectById(stageDto.getProjectId());
        List<TeamMember> teamMembers = teamMemberService.findAllById(stageDto.getExecutorsId());

        Stage stageEntity = Stage.builder()
                .stageName(stageDto.getStageName())
                .project(project)
                .stageRoles(stageRoles)
                .executors(teamMembers)
                .build();

        Stage saveStage = save(stageEntity);

        return stageMapper.toDto(saveStage);
    }

    public List<StageDto> getStageByFilter(StageFilterDto stageFilterDto) {
        Stream<Stage> allStages = findAll().stream();

        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(stageFilterDto))
                .reduce(allStages, ((stageStream, filter) -> filter.apply(stageStream, stageFilterDto)), (e1, e2) -> e1)
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(StageDeleteDto stageDeleteDto) {
        Stage stage = getById(stageDeleteDto.getStageId());

        switch (stageDeleteDto.getActionWithTaskDto().getAction()) {
            case "CASCADE" -> performCascadeDelete(stage);
            case "CLOSE" -> performCloseAction(stage);
            case "TRANSFER" -> performTransferAction(stage, stageDeleteDto.getActionWithTaskDto().getTransferStageId());
            default ->
                    throw new DataValidationException("Unknown action with task: " + stageDeleteDto.getActionWithTaskDto().getAction());
        }
    }

    public StageDto updateStage(StageDto stageDto) {
        Stage stage = getById(stageDto.getStageId());

        List<TeamMember> projectTeamMembers = stage.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();

        Map<TeamRole, Long> currentRoleCount = stage.getExecutors().stream()
                .flatMap(member -> member.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));

        ensureRequiredRoles(stage, currentRoleCount, projectTeamMembers);

        stage.setStageName(stageDto.getStageName());

        Stage updatedStage = save(stage);

        return stageMapper.toDto(updatedStage);
    }


    public List<StageDto> getAllProjectStages(Long projectId) {
        List<Stage> stages = projectService.getProjectById(projectId).getStages();
        return stages.stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = getById(stageId);
        return stageMapper.toDto(stage);
    }

    private void performCascadeDelete(Stage stage) {
        List<Task> stageTasks = new ArrayList<>(stage.getTasks());
        stageTasks.clear();
        stage.setTasks(stageTasks);
        delete(stage);
    }

    private void performCloseAction(Stage stage) {
        List<Task> stageTasks = stage.getTasks();
        stageTasks.forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
        });
        delete(stage);
    }

    private void performTransferAction(Stage stage, Long transferStageId) {
        Stage transferStage = getById(transferStageId);

        stage.getTasks().forEach(task -> {
            task.setStage(transferStage);
        });

        save(transferStage);
        delete(stage);
    }

    private void ensureRequiredRoles(Stage stage, Map<TeamRole, Long> currentRoleCount, List<TeamMember> projectTeamMembers) {
        stage.getStageRoles().forEach(stageRole -> {
            TeamRole requiredRole = stageRole.getTeamRole();
            int requiredCount = stageRole.getCount();
            long currentCount = currentRoleCount.getOrDefault(requiredRole, 0L);

            if (currentCount < requiredCount) {
                int invitationsNeeded = requiredCount - (int) currentCount;
                inviteNewMembers(stage, requiredRole, invitationsNeeded, projectTeamMembers, currentRoleCount);
            }
        });
    }

    private void inviteNewMembers(Stage stage, TeamRole requiredRole,
                                  int invitationsNeeded,
                                  List<TeamMember> projectTeamMembers,
                                  Map<TeamRole, Long> currentRoleCount) {
        List<TeamMember> candidates = findCandidates(projectTeamMembers, requiredRole, stage.getExecutors(), invitationsNeeded);

        updateStageExecutors(stage, candidates);
        sendInvitations(stage, candidates);
        updateCurrentRoleCount(currentRoleCount, requiredRole, candidates.size());
    }

    private void updateStageExecutors(Stage stage, List<TeamMember> newMembers) {
        List<TeamMember> updatedExecutors = new ArrayList<>(stage.getExecutors());
        updatedExecutors.addAll(newMembers);
        stage.setExecutors(updatedExecutors);
    }

    private void updateCurrentRoleCount(Map<TeamRole, Long> currentRoleCount, TeamRole role, int addedCount) {
        Long previousCount = currentRoleCount.get(role);
        currentRoleCount.merge(role, (long) addedCount, Long::sum);
    }

    private List<TeamMember> findCandidates(List<TeamMember> projectTeamMembers, TeamRole requiredRole,
                                            List<TeamMember> currentExecutors, int invitationsNeeded) {
        return projectTeamMembers.stream()
                .filter(member -> member.getRoles().contains(requiredRole) && !currentExecutors.contains(member))
                .limit(invitationsNeeded)
                .collect(Collectors.toList());
    }

    private void sendInvitations(Stage stage, List<TeamMember> candidates) {
        candidates.forEach(candidate -> {
            StageInvitation invitation = StageInvitation.builder()
                    .stage(stage)
                    .invited(candidate)
                    .status(StageInvitationStatus.PENDING)
                    .build();
            save(invitation);
        });
    }

    private StageInvitation save(StageInvitation stageInvitation) {
        log.info("Saving stage invitation: {}", stageInvitation.getId());

        stageValidator.validationOnNull(stageInvitation, "Stage invitation cannot be null");

        StageInvitation savedInvitation = stageInvitationRepository.save(stageInvitation);

        log.info("Successfully saved stage invitation with ID: {}", savedInvitation.getId());
        return savedInvitation;
    }

    private List<Stage> findAll() {
        log.info("Retrieving all stages");

        List<Stage> stages = stageRepository.findAll();

        log.info("Retrieved {} stages", stages.size());
        return stages;
    }

    private Stage save(Stage stage) {
        log.info("Saving stage: {}", stage.getStageId());

        Stage savedStage = stageRepository.save(stage);

        log.info("Successfully saved stage with ID: {}", savedStage.getStageId());
        return savedStage;
    }

    private Stage getById(Long stageId) {
        log.info("Retrieving stage by ID: {}", stageId);

        stageValidator.validationOnNull(stageId, "Stage ID cannot be null");

        Stage stage = stageRepository.getById(stageId);

        stageValidator.validationOnNull(stage, "Stage not found for ID: " + stageId);

        log.info("Retrieved stage: {}", stage.getStageId());
        return stage;
    }

    private void delete(Stage stage) {
        log.info("Deleting stage: {}", stage.getStageId());

        stageValidator.validationOnNull(stage, "Stage cannot be null");

        stageRepository.delete(stage);
        log.info("Successfully deleted stage with ID: {}", stage.getStageId());
    }

    public Stage getStageEntity(long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage", stageId));
    }
}
