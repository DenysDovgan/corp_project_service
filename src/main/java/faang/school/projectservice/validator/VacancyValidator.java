package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private static final List<TeamRole> ROLES_TO_CREATE_VACANCY = List.of(TeamRole.OWNER, TeamRole.MANAGER);
    private final VacancyRepository vacancyRepository;
    private final TeamMemberService teamMemberService;

    public void validateVacancyExistsById(Long vacancyId) {
        if (!vacancyRepository.existsById(vacancyId)) {
            throw new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %s", vacancyId));
        }
    }

    public void validateVacancyCreatorRole(VacancyDto dto) {
        TeamMember teamMember = teamMemberService.getTeamMemberByUserId(dto.getCreatedBy());
        if (teamMember.getRoles().stream().noneMatch((ROLES_TO_CREATE_VACANCY::contains))) {
            throw new DataValidationException("Vacancy can be created by following roles " + ROLES_TO_CREATE_VACANCY);
        }
    }
}
