package team.projectpulse.evaluation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import team.projectpulse.rubric.Criterion;
import jakarta.persistence.*;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;
    @ManyToOne(optional = false)
    private Criterion criterion;
    private Double actualScore;
    @ManyToOne(optional = false)
    private PeerEvaluation peerEvaluation;


    public Rating() {
    }

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "The throw is the score validation in setActualScore, on purpose. CT_CONSTRUCTOR_THROW guards against a finalizer attack, which needs untrusted code in the JVM to subclass Rating; Project Pulse loads none. Its fixes do not apply: JPA forbids a final entity, and a final finalize() is deprecated for removal.")
    public Rating(Criterion criterion, Double actualScore) {
        this.criterion = criterion;
        setActualScore(actualScore);
    }

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "The throw is the score validation in setActualScore, on purpose. CT_CONSTRUCTOR_THROW guards against a finalizer attack, which needs untrusted code in the JVM to subclass Rating; Project Pulse loads none. Its fixes do not apply: JPA forbids a final entity, and a final finalize() is deprecated for removal.")
    public Rating(Integer ratingId, Criterion criterion, Double actualScore) {
        this.ratingId = ratingId;
        this.criterion = criterion;
        setActualScore(actualScore);
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Double getActualScore() {
        return actualScore;
    }

    public void setActualScore(Double actualScore) {
        if (actualScore > criterion.getMaxScore() || actualScore < 0) {
            throw new IllegalArgumentException("Actual score must be between 0 and " + criterion.getMaxScore());
        }
        this.actualScore = actualScore;
    }

    public PeerEvaluation getPeerEvaluation() {
        return peerEvaluation;
    }

    public void setPeerEvaluation(PeerEvaluation peerEvaluation) {
        this.peerEvaluation = peerEvaluation;
    }

}
