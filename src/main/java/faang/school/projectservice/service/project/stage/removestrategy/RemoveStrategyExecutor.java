package faang.school.projectservice.service.project.stage.removestrategy;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RemoveStrategyExecutor {
    protected final StageRepository stageRepository;
    @Getter
    private final RemoveStrategy strategyType;

    public abstract void execute(Stage stage, RemoveTypeDto removeTypeDto);
}