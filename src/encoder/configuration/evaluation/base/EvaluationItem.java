package encoder.configuration.evaluation.base;

import encoder.configuration.evaluation.interfaces.IEvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluator;


/**
 * {@inheritDoc}
 */
public class EvaluationItem implements IEvaluationItem {
    private String _message;
    private EnSeverity _severity;
    private IEvaluator _evaluator;

    /**
     *
     * @param message
     * @param severity
     * @param evaluator
     */
    public EvaluationItem(String message, EnSeverity severity, IEvaluator evaluator){
        _message = message;
        _severity = severity;
        _evaluator = evaluator;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public String getMessage() {
        return _message == null ? "" : _message;
    }


    /**
     * {@inheritDoc}
     * @return
     */
    public EnSeverity getSeverity() {
        return _severity;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public IEvaluator getEvaluator() {
        return _evaluator;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString(){
        return "["+_severity+"] '"+_message+"' in '"+_evaluator.getTarget()+"'";
    }
}
