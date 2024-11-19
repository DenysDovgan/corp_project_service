package faang.school.projectservice.service;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final StageInvitationService stageInvitationService;
    private final StageMapper stageMapper;
    private final List<Filter<Stage, StageFilterDto>> stageFilters;

    public StageService(StageRepository stageRepository,
                        TeamMemberService teamMemberService,
                        ProjectService projectService,
                        @Lazy StageInvitationService stageInvitationService,
                        StageMapper stageMapper,
                        List<Filter<Stage, StageFilterDto>> stageFilters) {
        this.stageRepository = stageRepository;
        this.teamMemberService = teamMemberService;
        this.projectService = projectService;
        this.stageInvitationService = stageInvitationService;
        this.stageMapper = stageMapper;
        this.stageFilters = stageFilters;
    }

    public void setExecutor(Long stageId, Long executorId) {
        Stage stage = stageRepository.getById(stageId);
        List<TeamMember> executors = stage.getExecutors();
        executors.add(teamMemberService.getTeamMemberByUserId(executorId));
        stage.setExecutors(executors);

        stageRepository.save(stage);
    }

    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        Project project = projectService.getProjectById(stageDto.getProjectId());
        stage.setProject(project);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Transactional
    public void updateStage(long stageId, TeamMemberDto teamMemberDto) {
        Stage stage = getStageById(stageId);
        String role = teamMemberDto.getTeamRole().toString();
        if (!isParticipantWithRoleExist(stage, role)) {
            var teamMembers = getProjectMembersWithRole(stage, role);
            int requiredNumberOfUsers = numberStageUsersWithRole(stage, role);
            sendStageInvitations(stage, teamMembers, requiredNumberOfUsers);

        }
    }

    public List<StageDto> getStagesByProjectIdFiltered(long projectId, StageFilterDto filters) {
        Stream<Stage> stage = stageRepository.findAllByProjectId(projectId).stream();
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(stage, (currentStream, filter) ->
                                filter.apply(currentStream, filters),
                        (s1, s2) -> s1)
                .map(stageMapper::toDto)
                .toList();
    }

    public List<StageDto> getStagesByProjectId(long projectId) {
        return stageRepository.findAllByProjectId(projectId).stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        stageRepository.delete(stage);
    }

    public void deleteStage(long stageId, long anotherStageId) {
        Stage stage = moveTasks(stageId, anotherStageId);
        stageRepository.delete(stage);
    }

    public StageDto getStageDtoById(long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    public boolean existsById(Long stageId) {
        return stageRepository.existsById(stageId);
    }

    public Stage getStageById(Long stageId) {
        return stageRepository.getById(stageId);
    }

    private List<TeamMember> getProjectMembersWithRole(Stage stage, String role) {
        return teamMemberService.getProjectParticipantsWithRole(stage.getProject(), role);
    }

    private Stage moveTasks(long stageId, long anotherStageId) {
        Stage stage = stageRepository.getById(stageId);
        Stage anotherStage = stageRepository.getById(anotherStageId);
        anotherStage.setTasks(stage.getTasks());
        return stage;
    }

    private boolean isParticipantWithRoleExist(Stage stage, String role) {
        return stage.getExecutors().stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole.toString().equalsIgnoreCase(role));
    }

    private int numberStageUsersWithRole(Stage stage, String role) {
        return stage.getStageRoles().stream()
                .filter(stageRoles -> stageRoles.getTeamRole().toString().equalsIgnoreCase(role))
                .mapToInt(StageRoles::getCount)
                .findFirst()
                .orElse(0);
    }

    private void sendStageInvitations(Stage stage, List<TeamMember> teamMembers, int requiredNumberOfUsers) {
        int invitationsToSend = Math.min(requiredNumberOfUsers, teamMembers.size());

        for (int i = 0; i < invitationsToSend; i++) {
            StageInvitationDto invitation = createStageInvitation(stage, teamMembers.get(i));
            stageInvitationService.sendStageInvitation(invitation);
        }
    }

    private StageInvitationDto createStageInvitation(Stage stage, TeamMember teamMember) {
        return StageInvitationDto.builder()
                .stageId(stage.getStageId())
                .authorId(stage.getProject().getOwnerId())
                .invitedId(teamMember.getId())
                .build();
    }
}

