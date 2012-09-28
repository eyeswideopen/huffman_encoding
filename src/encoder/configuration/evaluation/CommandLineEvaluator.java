package encoder.configuration.evaluation;

import encoder.configuration.CommandLineConfiguration;
import encoder.configuration.evaluation.base.EnSeverity;
import encoder.configuration.evaluation.base.EvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;

import java.util.Collection;

/**
 * {@inheritDoc}
 *
 * The command line evaluator performs all checks performed by the {@link DefaultEvaluator default evaluator} plus
 * checks for validity and reasonability of command line arguments.
 */
public class CommandLineEvaluator extends DefaultEvaluator{

    /**
     * {@inheritDoc}
     * @return
     */
    public String getTarget() {
        return "command line arguments";
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public EnSeverity evaluate(IConfiguration configuration, Collection<IEvaluationItem> evaluationItems) {
        if(!(configuration instanceof CommandLineConfiguration)){
            evaluationItems.add(new EvaluationItem("given configuration is no command line configuration", EnSeverity.Error, this));
            return EnSeverity.Error;
        }

        super.evaluate(configuration, evaluationItems);

        CommandLineConfiguration cmdConf = (CommandLineConfiguration)configuration;

        if(cmdConf.isLoggingEnabled()){
            if(cmdConf.decode())
                evaluationItems.add(optionHasNoEffectWhenDecodeSet("log"));
        }

        if(cmdConf.useAffinityLocks()){
            if(cmdConf.decode())
                evaluationItems.add(optionHasNoEffectWhenDecodeSet("affinity locks"));
        }

        if(cmdConf.availableHardwareThreadsSet()){
            if(cmdConf.decode())
                evaluationItems.add(optionHasNoEffectWhenDecodeSet("threads"));
        }

        if(cmdConf.chunkSizeSet()){
            if(cmdConf.decode())
                evaluationItems.add(optionHasNoEffectWhenDecodeSet("chunk size"));
        }

        if(cmdConf.wordSizeSet()){
            if(cmdConf.decode())
                evaluationItems.add(optionHasNoEffectWhenDecodeSet("word size"));
        }

        return DefaultEvaluator.getHighestSeverity(evaluationItems);
    }

    private EvaluationItem optionHasNoEffectWhenDecodeSet(String option){
        return new EvaluationItem(option+" option has no effect, when decode option is set", EnSeverity.Info, this);
    }
}
