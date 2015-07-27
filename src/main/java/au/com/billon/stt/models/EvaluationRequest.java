package au.com.billon.stt.models;

/**
 * Created by Zheng on 27/07/2015.
 */
public class EvaluationRequest {
    public static final String EVALUATION_TYPE_XPATH = "XPath";
    private String type;
    private String expression;
    private String input;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
