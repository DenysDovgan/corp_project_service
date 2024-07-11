package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Stream;

@Component
public class VacancySkillsFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return (filters.getSkillIdsPattern() != null && !filters.getSkillIdsPattern().isEmpty());
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy ->
                new HashSet<>(vacancy.getRequiredSkillIds())
                .containsAll(filters.getSkillIdsPattern()));
    }
}
