package faang.school.projectservice.service.internship.internship_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.service.task.task_filter.TaskFilter;

import java.util.List;
import java.util.stream.Stream;

public class TaskDescriptionFilter implements TaskFilter {

    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getDescription() != null;
    }

    @Override
    public List<Task> apply(Stream<Task> taskStream, TaskFilterDto filters) {
        return taskStream.filter(task -> task.getDescription().contains(filters.getDescription()))
                .toList();
    }
}
