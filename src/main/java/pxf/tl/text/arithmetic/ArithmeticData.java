package pxf.tl.text.arithmetic;

import java.util.*;

/**
 * Contains a mapping of names to variables.
 *
 * <p>Both the user as well as the {@link ArithmeticExpression.Parser} use a ArithmeticData to
 * resolve a name into a {@link ArithmeticVariable}. In contrast to a simple Map, this approach
 * provides two advantages: It's usually faster, as the variable only needs to be resolved apply.
 * Modifying it and especially reading it when evaluating an expression is as cheap as a simple
 * field access. The second advantage is that scopes can be chained. So variables can be either
 * shared by two expression or kept separate, if required.
 *
 * @author potatoxf
 */
public final class ArithmeticData {
    public static final ArithmeticData ROOT = new ArithmeticData(true);

    static {
        ROOT.createConstant("pi", Math.PI);
        ROOT.createConstant("euler", Math.E);
    }

    /**
     * 环境
     */
    private final Map<String, ArithmeticVariable> context = new TreeMap<String, ArithmeticVariable>();
    /**
     * 父级数据
     */
    private final ArithmeticData parent;

    public ArithmeticData() {
        this(null);
    }

    public ArithmeticData(ArithmeticData parent) {
        if (parent == null) {
            this.parent = ROOT;
        } else {
            this.parent = parent;
        }
    }

    private ArithmeticData(boolean any) {
        this.parent = null;
    }

    /**
     * is contains a variable with the given name.
     *
     * @param name the variable to look for
     * @return return true if exists,or return false
     */
    public boolean containsVariable(String name) {
        if (context.containsKey(name)) {
            return true;
        }
        if (parent != null) {
            return parent.containsVariable(name);
        }
        return false;
    }

    /**
     * Searches for or creates a variable with the given name.
     *
     * <p>If no variable with the given name is found, a new variable is created in this scope
     *
     * @param name the variable to look for
     * @return a variable with the given name
     */
    public ArithmeticVariable getVariable(String name) {
        ArithmeticVariable result = findVariable(name);
        if (result != null) {
            return result;
        }
        return create(name);
    }

    /**
     * Searches for a {@link ArithmeticVariable} with the given name.
     *
     * <p>If the variable does not exist <tt>null</tt> will be returned
     *
     * @param name the name of the variable to search
     * @return the variable with the given name or <tt>null</tt> if no such variable was found
     */
    public ArithmeticVariable findVariable(String name) {
        if (context.containsKey(name)) {
            return context.get(name);
        }
        if (parent != null) {
            return parent.findVariable(name);
        }
        return null;
    }

    /**
     * Searches or creates a variable in this scope.
     *
     * <p>Tries to find a variable with the given name in this scope. If no variable with the given
     * name is found, the parent scope is not checked, but a new variable is created.
     *
     * @param name the variable to search or make
     * @return a variable with the given name from the local scope
     */
    public ArithmeticVariable create(String name) {
        if (context.containsKey(name)) {
            return context.get(name);
        }
        ArithmeticVariable result = new ArithmeticVariable(name);
        context.put(name, result);

        return result;
    }

    /**
     * Searches or creates a variable in this scope.
     *
     * <p>Tries to find a variable with the given name in this scope. If no variable with the given
     * name is found, the parent scope is not checked, but a new variable is created.
     *
     * @param name the variable to search or make
     * @return a variable with the given name from the local scope
     */
    public ArithmeticVariable create(String name, Object numberValue) {
        return create(name).setValue(numberValue);
    }

    /**
     * Searches or creates a variable in this scope.
     *
     * <p>Tries to find a variable with the given name in this scope. If no variable with the given
     * name is found, the parent scope is not checked, but a new variable is created.
     *
     * @param container the variable container
     * @return a variable with the given name from the local scope
     */
    public ArithmeticData create(Map<String, ?> container) {
        for (Map.Entry<String, ?> entry : container.entrySet()) {
            create(entry.getKey()).setValue(entry.getValue());
        }
        return this;
    }

    /**
     * Searches or creates a constant in this scope.If a variable, remove the variable and replace it
     * with a constant.
     *
     * <p>Tries to find a constant with the given name in this scope. If no constant with the given
     * name is found, the parent scope is not checked, but a new constant is created.
     *
     * @param name the constant to search or make
     * @return a constant with the given name from the local scope
     */
    public ArithmeticConstant createConstant(String name, Number value) {
        if (context.containsKey(name)) {
            ArithmeticVariable arithmeticVariable = context.get(name);
            if (arithmeticVariable instanceof ArithmeticConstant) {
                return (ArithmeticConstant) arithmeticVariable;
            } else {
                context.remove(name);
            }
        }
        ArithmeticConstant result = new ArithmeticConstant(name, value);
        context.put(name, result);
        return result;
    }

    /**
     * Removes the variable with the given name from this scope.
     *
     * <p>If will not remove the variable from a parent scope.
     *
     * @param name the name of the variable to remove
     * @return the removed variable or <tt>null</tt> if no variable with the given name existed
     */
    public ArithmeticVariable remove(String name) {
        if (context.containsKey(name)) {
            return context.remove(name);
        } else {
            return null;
        }
    }

    /**
     * Returns all names of variables known to this scope (ignoring those of the parent scope).
     *
     * @return a set of all known variable names
     */
    public Set<String> getLocalNames() {
        return context.keySet();
    }

    /**
     * Returns all names of variables known to this scope or one of its parent scopes.
     *
     * @return a set of all known variable names
     */
    public Set<String> getNames() {
        if (parent == null) {
            return getLocalNames();
        }
        Set<String> result = new TreeSet<String>();
        result.addAll(parent.getNames());
        result.addAll(getLocalNames());
        return result;
    }

    /**
     * Returns all variables known to this scope (ignoring those of the parent scope).
     *
     * @return a collection of all known variables
     */
    public Collection<ArithmeticVariable> getLocalVariables() {
        return context.values();
    }

    /**
     * Returns all variables known to this scope or one of its parent scopes.
     *
     * @return a collection of all known variables
     */
    public Collection<ArithmeticVariable> getVariables() {
        if (parent == null) {
            return getLocalVariables();
        }
        List<ArithmeticVariable> result = new ArrayList<ArithmeticVariable>();
        for (ArithmeticData p = this; p.parent != null; p = p.parent) {
            result.addAll(p.getVariables());
        }
        return result;
    }
}
