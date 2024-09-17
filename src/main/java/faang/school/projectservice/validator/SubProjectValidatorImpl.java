package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;

public class SubProjectValidatorImpl implements SubProjectValidator{
    @Override
    public void validate(Project project) throws RootProjectsParentMustNotBeNull,
            CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull {
        if (project.getParentProject() == null) {
            throw new ParentProjectMusNotBeNull();
        }
        if (project.getParentProject().getParentProject() == null) {
            throw new RootProjectsParentMustNotBeNull();
        }
        if (project.getParentProject().getVisibility() == ProjectVisibility.PUBLIC
                && project.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new CannotCreatePrivateProjectForPublicParent();
        }
    }
}
