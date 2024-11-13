package faang.school.projectservice.repository;

import java.util.List;

import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StageInvitationRepository {
    private final StageInvitationJpaRepository repository;

    public StageInvitation save(StageInvitation stageInvitation) {
        return repository.save(stageInvitation);
    }

    public StageInvitation findById(Long stageInvitationId) {
        return repository.findById(stageInvitationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Stage invitation doesn't exist by id: %s", stageInvitationId))
        );
    }

    public List<StageInvitation> findByInvited_UserId(Long userId){
        return repository.findByInvited_UserId(userId);
    }

    public List<StageInvitation> findByAuthor_UserId(Long userId){
        return repository.findByAuthor_UserId(userId);
    }

    public List<StageInvitation> findByInvited_UserIdAndStatus(Long userId, StageInvitationStatus status){
        return repository.findByInvited_UserIdAndStatus(userId, status);
    }

    public List<StageInvitation> findByInvited_UserIdAndStage(Long userId, Stage stage){
        return repository.findByInvited_UserIdAndStage(userId, stage);
    }


    public boolean stageInvitationExist (Long stageInvitationId){
        return repository.existsById(stageInvitationId);
    }

    public List<StageInvitation> findAll() {
        return repository.findAll();
    }
}
