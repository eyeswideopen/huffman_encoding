package encoder.configuration.evaluation;

import encoder.configuration.evaluation.base.EnSeverity;
import encoder.configuration.evaluation.base.EvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;

import java.util.Collection;

/**
 * {@inheritDoc}
 *
 * The default evaluator checks all basic configuration values needed for execution in the
 * {@link encoder.processing.Processor Processor} class.
 */
public class DefaultEvaluator implements IEvaluator {
    private String _target;

    /**
     *
     */
    public DefaultEvaluator(){
        this(null);
    }

    /**
     *
     * @param target textual description of the evaluator target (i.e. configuration file 'xy'),
     *               default value: 'hard coded default configuration'
     */
    public DefaultEvaluator(String target){
        if(target==null)
            _target = "hard coded default configuration";
        else
            _target = target;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public String getTarget() {
        return _target;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public EnSeverity evaluate(IConfiguration configuration, Collection<IEvaluationItem> evaluationItems) {
        // word size checks
        if(configuration.getWordSize()<1)
            evaluationItems.add(new EvaluationItem("word size must be bigger than 0",EnSeverity.Error,this));
        else if(configuration.getWordSize()<2)
            evaluationItems.add(new EvaluationItem("word size of 1 is technically possible, but spares any sense", EnSeverity.Warning, this));
        if(configuration.getWordSize()>32)
            evaluationItems.add(new EvaluationItem("word size must not be bigger than 32", EnSeverity.Error, this));
        else if(configuration.getWordSize()>12)
            evaluationItems.add(new EvaluationItem("big word sizes (>12) require huge amounts of memory and might crash the application", EnSeverity.Warning, this));

        // chunk size checks
        if(configuration.getChunkSize()<1)
            evaluationItems.add(new EvaluationItem("chunk size must be greater than 0", EnSeverity.Error, this));
        else if(configuration.getChunkSize()<128)
            evaluationItems.add(new EvaluationItem("small chunks (<128) are likely to result in more header data than payload", EnSeverity.Warning, this));
        if(configuration.getChunkSize()>1000000)
            evaluationItems.add(new EvaluationItem("big chunks may cause out of memory conditions", EnSeverity.Warning, this));

        // (hardware) thread and affinity checks
        if(configuration.getAvailableHardwareThreads()<1)
            evaluationItems.add(new EvaluationItem("threads must be more than 0", EnSeverity.Error, this));
        else if(configuration.getAvailableHardwareThreads()<2)
            evaluationItems.add(new EvaluationItem("with only one thread, no parallelization will take place", EnSeverity.Warning, this));
        if(configuration.getAvailableHardwareThreads()>Runtime.getRuntime().availableProcessors())
            evaluationItems.add(
                    new EvaluationItem(
                            "more threads configured than hardware threads available"+
                            (configuration.useAffinityLocks()?" and affinity locks activated":""),
                            configuration.useAffinityLocks()?EnSeverity.Error:EnSeverity.Warning,
                            this));

        return getHighestSeverity(evaluationItems);
    }

    /**
     * Retrieves the highest (worst) {@link EnSeverity severity} occurring in the given collection
     * of {@link IEvaluationItem evaluation items}
     *
     * @param evaluationItems
     * @return
     */
    public static EnSeverity getHighestSeverity(Collection<IEvaluationItem> evaluationItems){
        boolean hasInfo = false;
        boolean hasWarning = false;
        boolean hasError = false;

        for(IEvaluationItem item : evaluationItems){
            switch (item.getSeverity()){
                case Info:
                    hasInfo = true;
                    break;
                case Warning:
                    hasWarning = true;
                    break;
                case Error:
                    hasError = true;
                    break;
            }
            if(hasError && hasWarning && hasInfo)
                break;
        }

        if(hasError)
            return EnSeverity.Error;
        if(hasWarning)
            return EnSeverity.Warning;
        if(hasInfo)
            return EnSeverity.Info;
        return EnSeverity.None;
    }
}
