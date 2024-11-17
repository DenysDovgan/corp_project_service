package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.exception.invitation.InvalidInvitationDataException;
import faang.school.projectservice.mapper.invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.invitation.filter.InvitationFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<InvitationFilter> invitationFilters;

    public StageInvitationDTO sendInvitation(StageInvitationDTO stageInvitationDTO) {
        log.info("Получен запрос на отправку приглашения: {}", stageInvitationDTO);

        Stage stage = stageRepository.getById((stageInvitationDTO.getStageId()));
        if (stage == null) {
            log.warn("Этап не найден: {}", stageInvitationDTO.getStageId());
            throw new EntityNotFoundException("Этап не найден c ID: " + stageInvitationDTO.getStageId());
        }
        TeamMember invited = teamMemberRepository.findById((stageInvitationDTO.getInvitedId()));
        if (invited == null) {
            log.warn("Приглашаемый участник не найден: {}", stageInvitationDTO.getInvitedId());
            throw new EntityNotFoundException("Приглашаемый участник не найден");
        }

        TeamMember author = teamMemberRepository.findById((stageInvitationDTO.getAuthorId()));
        if (author == null) {
            log.warn("Автор не найден: {}", stageInvitationDTO.getAuthorId());
            throw new EntityNotFoundException("Автор не найден");
        }

        if (!stage.getProject().equals(invited.getTeam().getProject())) {
            log.warn("Приглашение не может быть отправлено: участник не принадлежит проекту этапа");
            throw new InvalidInvitationDataException("Участник не принадлежит проекту этапа");
        }

        StageInvitation invitation = stageInvitationMapper.toEntity(stageInvitationDTO);
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отправлено: {}", savedInvitation);
        return stageInvitationMapper.toDto(savedInvitation);
    }

    public StageInvitationDTO acceptInvitation(Long invitationId) {
        log.info("Принятие приглашения с ID: {}", invitationId);

        Optional<StageInvitation> invationOpt = Optional.ofNullable(stageInvitationRepository.findById(invitationId));

        if (!invationOpt.isPresent()) {
            log.warn("Приглашение с ID {} не найдено", invitationId);
            throw new InvalidInvitationDataException("Приглашение с ID " + invitationId + " не найдено");
        }

        StageInvitation invitation = invationOpt.get();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.getStage().getExecutors().add(invitation.getInvited());

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно принято: {}", updatedInvitation);
        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public StageInvitationDTO rejectInvitation(Long invitationId, String rejectionReason) {
        log.info("Отклонение приглашения с ID: {}. Причина: {}", invitationId, rejectionReason);

        Optional<StageInvitation> invitationOpt = Optional.ofNullable(stageInvitationRepository.findById(invitationId));
        if (!invitationOpt.isPresent()) {
            log.warn("Приглашение с ID {} не найдено", invitationId);
            throw new InvalidInvitationDataException("Приглашение с ID " + invitationId + " не найдено");
        }

        StageInvitation invitation = invitationOpt.get();
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setRejectionReason(rejectionReason);

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отклонено: {}", updatedInvitation);
        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public List<StageInvitationDTO> getFilteredInvitations(StageInvitationDTO invitationFilterDTO) {
        log.info("Фильтрация приглашений с фильтром: {}", invitationFilterDTO);
        List<StageInvitation> invitations = stageInvitationRepository.findAll();

        if (invitations == null || invitations.isEmpty()) {
            log.warn("Не найдено приглашений, соответствующих фильтру: {}", invitationFilterDTO);
            return List.of();
        }

        Stream<StageInvitation> filteredInvitations = invitations.stream();

        List<StageInvitationDTO> result = invitationFilters.stream()
            .filter(filter -> filter.isApplicable(invitationFilterDTO))
            .flatMap(filter -> filter.apply(filteredInvitations, invitationFilterDTO))
            .map(stageInvitationMapper::toDto)
            .collect(Collectors.toList());

        if (result.isEmpty()) {
            log.warn("Не найдено приглашений, соответствующих фильтру: {}", invitationFilterDTO);
        } else {
            log.info("Найдено {} приглашений, соответствующих фильтру: {}", result.size(), invitationFilterDTO);
        }
        return result;
    }
}