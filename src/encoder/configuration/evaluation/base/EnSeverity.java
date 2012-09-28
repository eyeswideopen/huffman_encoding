package encoder.configuration.evaluation.base;

import org.junit.Test;

/**
 * Specifies the severity of a configuration rule violation.
 */
public enum EnSeverity {
    Info,
    Warning,
    Error,
    None;

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString(){
        switch (this){
            case Info:
                return "INFO";
            case Warning:
                return "WARNING";
            case Error:
                return "ERROR";
            default:
                return "";
        }
    }
}
