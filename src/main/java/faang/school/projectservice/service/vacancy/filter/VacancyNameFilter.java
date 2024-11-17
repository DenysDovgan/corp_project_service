package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter{
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.name() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy -> vacancy.getName().contains(filters.name()));
    }
}