package team.projectpulse.rubric;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * The case-insensitive searches lowercase the user's search text in Java and compare it with SQL LOWER() of the
 * column. Lowercasing with the JVM's default locale (PMD UseLocaleWithCaseConversions) makes the search depend on
 * where the server runs: under a Turkish locale "I" lowercases to the dotless "ı", so a search for the seeded
 * criterion "Initiative" becomes LIKE '%ınitiative%' and finds nothing. These tests run the specifications under
 * tr-TR and check the LIKE pattern they hand to the database.
 */
class SearchSpecsLocaleTest {

    private Locale originalDefault;

    @BeforeEach
    void useTurkishDefaultLocale() {
        this.originalDefault = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
    }

    @AfterEach
    void restoreDefaultLocale() {
        Locale.setDefault(this.originalDefault);
    }

    @Test
    void criterionNameSearchDoesNotDependOnServerLocale() {
        assertThat(likePatternOf(CriterionSpecs.containsCriterion("Initiative"))).isEqualTo("%initiative%");
    }

    @Test
    void criterionDescriptionSearchDoesNotDependOnServerLocale() {
        assertThat(likePatternOf(CriterionSpecs.containsDescription("IN MEETINGS"))).isEqualTo("%in meetings%");
    }

    @Test
    void rubricNameSearchDoesNotDependOnServerLocale() {
        assertThat(likePatternOf(RubricSpecs.containsRubricName("Peer Eval RUBRIC"))).isEqualTo("%peer eval rubric%");
    }

    /**
     * Runs a specification against mocked Criteria API objects and returns the pattern it passes to LIKE.
     */
    @SuppressWarnings("unchecked")
    private static <T> String likePatternOf(Specification<T> specification) {
        Root<T> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<Object> column = mock(Path.class);
        Expression<String> loweredColumn = mock(Expression.class);
        doReturn(column).when(root).get(anyString());
        doReturn(loweredColumn).when(criteriaBuilder).lower(any());

        specification.toPredicate(root, query, criteriaBuilder);

        ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
        verify(criteriaBuilder).like(eq(loweredColumn), pattern.capture());
        return pattern.getValue();
    }

}
