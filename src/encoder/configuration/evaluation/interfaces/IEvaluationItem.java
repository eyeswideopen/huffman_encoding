package encoder.configuration.evaluation.interfaces;

import encoder.configuration.evaluation.base.EnSeverity;

/**
 * An evaluation item describes a configuration rule violation, consisting of a {@link EnSeverity severity},
 * a {@link #getMessage() description} and a target configuration
 * retrieved via the {@link #getEvaluator() evaluator} (see {@link IEvaluator IEvaluator}).
 */
public interface IEvaluationItem {

    /**
     * Informative message on the configuration rule violation.
     * @return
     */
    String getMessage();

    /**
     * The {@link EnSeverity severity} of the configuration rule violation.
     * @return
     */
    EnSeverity getSeverity();

    /**
     * Retrieves a reference to the {@link IEvaluator evaluator} which created this
     * evaluation item.
     * @return
     */
    IEvaluator getEvaluator();
}
