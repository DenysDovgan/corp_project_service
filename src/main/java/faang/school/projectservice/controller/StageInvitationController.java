package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.RejectStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFiltersDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stage-invitation")
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public ResponseEntity<StageInvitationDto> sendStageInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return ResponseEntity.ok(stageInvitationService.sendStageInvitation(stageInvitationDto));
    }

    @PutMapping("/accept")
    public ResponseEntity<StageInvitationDto> acceptStageInvitation(@Valid @RequestBody AcceptStageInvitationDto stageInvitation) {
        return ResponseEntity.ok(stageInvitationService.acceptStageInvitation(stageInvitation));
    }

    @PutMapping("/reject")
    public ResponseEntity<StageInvitationDto> rejectStageInvitation(@Valid @RequestBody RejectStageInvitationDto rejectStageInvitationDto) {
        return ResponseEntity.ok(stageInvitationService.rejectStageInvitation(rejectStageInvitationDto));
    }

    @PostMapping("/filters")
    public ResponseEntity<List<StageInvitationDto>> filterStageInvitation(@Valid @RequestBody StageInvitationFiltersDto filters) {
        return ResponseEntity.ok(stageInvitationService.filters(filters));
    }
}