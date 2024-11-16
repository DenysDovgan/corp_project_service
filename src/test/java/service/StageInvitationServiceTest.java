package service;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import faang.school.projectservice.service.stage_invitation.filter.StageInvitationFilter;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Data
@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService service;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private StageInvitationValidator stageInvitationValidate;

    @Mock
    private List<StageInvitationFilter> invitationFilters;

    private StageInvitationResponseDto invitationRsDto;
    private StageInvitationRequestDto invitationRqDto;
    private StageInvitation invitation;
    private TeamMember invitedMember;
    private TeamMember executor;

    @Test
    public void testCreateInvitation() {
        when(stageInvitationMapper.toRqEntity(invitationRqDto)).thenReturn(invitation);
        when(stageInvitationMapper.toRsDto(invitation)).thenReturn(new StageInvitationResponseDto());

        StageInvitationResponseDto response = service.createInvitation(invitationRqDto);

        assertNotNull(response);
        verify(stageInvitationValidate).validateInvitation(invitationRqDto);
        verify(stageInvitationRepository).save(invitation);
        verify(stageInvitationMapper).toRsDto(invitation);
    }

    @Test
    public void testAcceptInvitation() {
        invitation = new StageInvitation();
        Long invitationId = 1L;
        Long userId = 1L;

        Stage stage = new Stage();
        invitedMember = new TeamMember();
        List<TeamMember> executors = new ArrayList<>();
        executors.add(invitedMember);
        stage.setExecutors(executors);

        invitation.setStage(stage);

        when(stageInvitationRepository.findById(invitationId)).thenReturn(invitation);
        when(stageInvitationMapper.toRsDto(invitation)).thenReturn(new StageInvitationResponseDto());

        StageInvitationResponseDto response = service.acceptInvitation(invitationId, userId);

        assertNotNull(response);
        assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
        assertEquals(userId, invitation.getInvited().getId());
        assertTrue(invitation.getStage().getExecutors().contains(invitedMember));

        verify(stageInvitationRepository).findById(invitationId);
        verify(stageInvitationValidate).checkStatus(invitation, StageInvitationStatus.ACCEPTED);
        verify(stageInvitationMapper).toRsDto(invitation);
    }

    @Test
    public void testRejectInvitation() {
        Long invitationId = 1L;
        invitation = new StageInvitation();
        invitationRqDto = new StageInvitationRequestDto();
        invitationRqDto.setInvitedId(3L);

        when(stageInvitationRepository.findById(invitationId)).thenReturn(invitation);
        when(stageInvitationMapper.toRsDto(invitation)).thenReturn(new StageInvitationResponseDto());

        StageInvitationResponseDto response = service.rejectInvitation(invitationId, invitationRqDto);

        assertNotNull(response);
        assertEquals(StageInvitationStatus.REJECTED, invitation.getStatus());
        assertEquals(3L, invitation.getInvited().getId());

        verify(stageInvitationValidate).validateDescription(invitationRqDto);
        verify(stageInvitationValidate).checkStatus(invitation, StageInvitationStatus.REJECTED);
        verify(stageInvitationRepository).findById(invitationId);
        verify(stageInvitationMapper).toRsDto(invitation);
    }

    @Test
    public void testViewAllInvitation() {
        Long userId = 1L;
        StageInvitationFilterDto filter = new StageInvitationFilterDto();

        service.viewAllInvitation(userId, filter);

        verify(stageInvitationRepository, times(1)).findAll();
    }
}
