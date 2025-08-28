package lk.jiat.globemed.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Context class for Interpreter Pattern
 * Holds variables and data for query interpretation
 */
public class QueryContext {
    private Map<String, Object> variables;
    
    public QueryContext() {
        this.variables = new HashMap<>();
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
    
    public void removeVariable(String name) {
        variables.remove(name);
    }
    
    public void clearVariables() {
        variables.clear();
    }
    
    public Map<String, Object> getAllVariables() {
        return new HashMap<>(variables);
    }
    
    @Override
    public String toString() {
        return "QueryContext{variables=" + variables + "}";
    }
}