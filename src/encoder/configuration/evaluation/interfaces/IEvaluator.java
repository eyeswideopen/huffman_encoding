package encoder.configuration.evaluation.interfaces;

import encoder.configuration.evaluation.base.EnSeverity;
import encoder.configuration.interfaces.IConfiguration;

import java.util.Collection;

/**
 * An evaluator is created by an {@link IConfiguration IConfiguration} implementation instance's
 * {@link encoder.configuration.interfaces.IConfiguration#getEvaluator()} method and is intended for
 * checking the configuration's values' validity.
 */
public interface IEvaluator {
    /**
     * Retrieves the evaluation target (i.e. configuration file or command line arguments)
     * @return
     */
    String getTarget();

    /**
     * Fills the collection of {@link IEvaluationItem evaluation items} with remarks on the
     * {@link IConfiguration configuration} in three categories specified by {@link EnSeverity}:
     * Error, Warning and Information.
     * @param configuration
     * @param evaluationItems
     * @return the highest (worst) {@link EnSeverity severity} encountered during evaluation
     */
    EnSeverity evaluate(IConfiguration configuration, Collection<IEvaluationItem> evaluationItems);
}
