package team.projectpulse.rubric;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class RubricSpecs {

    private RubricSpecs() {
        // Static factory methods only; not meant to be instantiated.
    }

    public static Specification<Rubric> containsRubricName(String providedRubricName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("rubricName")), "%" + providedRubricName.toLowerCase(Locale.ROOT) + "%");
    }

    public static Specification<Rubric> belongsToCourse(Integer courseId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("course").get("id"), courseId);
    }

}
