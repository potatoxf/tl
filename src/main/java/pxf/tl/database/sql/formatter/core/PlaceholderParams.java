package pxf.tl.database.sql.formatter.core;

import pxf.tl.api.Sized;
import pxf.tl.help.Whether;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Handles placeholder replacement with given params.
 */
public interface PlaceholderParams extends Sized {

    PlaceholderParams EMPTY = new Empty();

    /**
     * @param params query param
     */
    static PlaceholderParams of(Map<String, ?> params) {
        return new NamedPlaceholderParams(params);
    }

    /**
     * @param params query param
     */
    static PlaceholderParams of(List<?> params) {
        return new IndexedPlaceholderParams(params);
    }

    Object get();

    Object getByName(String key);

    /**
     * Returns param value that matches given placeholder with param key.
     *
     * @param token token.key Placeholder key token.value Placeholder value
     * @return param or token.value when params are missing
     */
    default Object get(Token token) {
        if (isEmpty()) {
            return token.value;
        }
        if (!(token.key == null || Whether.empty(token.key))) {
            return this.getByName(token.key);
        } else {
            return this.get();
        }
    }

    class NamedPlaceholderParams implements PlaceholderParams {
        private final Map<String, ?> params;

        NamedPlaceholderParams(Map<String, ?> params) {
            this.params = params;
        }

        @Override
        public int size() {
            return params.size();
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object getByName(String key) {
            return this.params.get(key);
        }

        @Override
        public String toString() {
            return this.params.toString();
        }
    }

    class IndexedPlaceholderParams implements PlaceholderParams {
        private final Queue<?> params;

        @Override
        public int size() {
            return params.size();
        }

        IndexedPlaceholderParams(List<?> params) {
            this.params = new ArrayDeque<>(params);
        }

        @Override
        public Object get() {
            return this.params.poll();
        }

        @Override
        public Object getByName(String key) {
            return null;
        }

        @Override
        public String toString() {
            return this.params.toString();
        }
    }

    class Empty implements PlaceholderParams {
        Empty() {
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object getByName(String key) {
            return null;
        }

        @Override
        public String toString() {
            return "[]";
        }
    }
}
