package pxf.tl.lang;

import pxf.tl.help.Whether;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * HTML Table构造器
 *
 * @author potatoxf
 */
public class HtmlTableBuilder<T> {

    protected final List<String[]> columnHeadersList = new LinkedList<>();

    protected final List<Function<T, Object>> columnReferenceList = new LinkedList<>();

    protected final Set<Integer> mergeColumnSet = new LinkedHashSet<>();

    protected char quote = '\'';

    protected boolean isGroupByData = true;

    protected Map<String, String> tableProperties = null;

    protected String tableStyle =
            "border-collapse: collapse;border-spacing: 0;text-align: center;border: 2px solid #000;";

    protected String tableClass = null;

    protected Map<String, String> theadProperties = null;

    protected String theadStyle = null;

    protected String theadClass = null;

    protected Map<String, String> tbodyProperties = null;

    protected String tbodyStyle = null;

    protected String tbodyClass = null;

    // col

    protected BiFunction<String[], Integer, Map<String, String>> columnHeaderPropertiesHandler = null;

    protected BiFunction<String[], Integer, String> columnHeaderStyleHandler =
            (row, index) -> "padding: 4px 16px;border: 1px solid #000;font-weight: bolder;";

    protected BiFunction<String[], Integer, String> columnHeaderClassHandler = null;

    protected BiFunction<String[], Integer, String> columnHeaderHandler = null;

    protected BiFunction<T, Integer, Map<String, String>> columnPropertiesHandler = null;

    protected BiFunction<T, Integer, String> columnStyleHandler =
            (row, index) -> "padding: 4px 16px;border: 1px solid #000;";

    protected BiFunction<T, Integer, String> columnClassHandler = null;

    protected BiFunction<T, Integer, String> columnHandler = null;

    // row

    protected BiFunction<String[], Integer, Map<String, String>> rowHeaderPropertiesHandler = null;

    protected BiFunction<String[], Integer, String> rowHeaderStyleHandler = null;

    protected BiFunction<String[], Integer, String> rowHeaderClassHandler = null;

    protected BiFunction<T, Integer, Map<String, String>> rowPropertiesHandler = null;

    protected BiFunction<T, Integer, String> rowStyleHandler = null;

    protected BiFunction<T, Integer, String> rowClassHandler = null;

    protected HtmlTableBuilder() {
    }

    public static <T> HtmlTableBuilder<T> of() {
        return new HtmlTableBuilder<>();
    }

    public Function<T, Object> getColumnExtractor(int column) {
        return columnReferenceList.get(column);
    }

    public HtmlTableBuilder<T> addColumnHeaders(String... columnHeaders) {
        this.columnHeadersList.add(columnHeaders);
        return this;
    }

    public HtmlTableBuilder<T> addColumnReference(Function<T, Object> columnReference) {
        this.columnReferenceList.add(Objects.requireNonNull(columnReference));
        return this;
    }

    public HtmlTableBuilder<T> setMergeColumnSet(int... mergeColumns) {
        if (!Whether.empty(this.mergeColumnSet)) {
            this.mergeColumnSet.clear();
        }
        for (int mergeColumn : mergeColumns) {
            this.mergeColumnSet.add(mergeColumn);
        }
        return this;
    }

    public HtmlTableBuilder<T> setQuote(char quote) {
        this.quote = quote;
        return this;
    }

    public HtmlTableBuilder<T> setGroupByData(boolean groupByData) {
        isGroupByData = groupByData;
        return this;
    }

    public HtmlTableBuilder<T> setTableProperties(Map<String, String> tableProperties) {
        this.tableProperties = tableProperties;
        return this;
    }

    public HtmlTableBuilder<T> setTableStyle(String tableStyle) {
        this.tableStyle = tableStyle;
        return this;
    }

    public HtmlTableBuilder<T> setTableClass(String tableClass) {
        this.tableClass = tableClass;
        return this;
    }

    public HtmlTableBuilder<T> setTheadProperties(Map<String, String> theadProperties) {
        this.theadProperties = theadProperties;
        return this;
    }

    public HtmlTableBuilder<T> setTheadStyle(String theadStyle) {
        this.theadStyle = theadStyle;
        return this;
    }

    public HtmlTableBuilder<T> setTheadClass(String theadClass) {
        this.theadClass = theadClass;
        return this;
    }

    public HtmlTableBuilder<T> setTbodyProperties(Map<String, String> tbodyProperties) {
        this.tbodyProperties = tbodyProperties;
        return this;
    }

    public HtmlTableBuilder<T> setTbodyStyle(String tbodyStyle) {
        this.tbodyStyle = tbodyStyle;
        return this;
    }

    public HtmlTableBuilder<T> setTbodyClass(String tbodyClass) {
        this.tbodyClass = tbodyClass;
        return this;
    }

    public HtmlTableBuilder<T> setColumnHeaderPropertiesHandler(
            BiFunction<String[], Integer, Map<String, String>> columnHeaderPropertiesHandler) {
        this.columnHeaderPropertiesHandler = columnHeaderPropertiesHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnHeaderStyleHandler(
            BiFunction<String[], Integer, String> columnHeaderStyleHandler) {
        this.columnHeaderStyleHandler = columnHeaderStyleHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnHeaderClassHandler(
            BiFunction<String[], Integer, String> columnHeaderClassHandler) {
        this.columnHeaderClassHandler = columnHeaderClassHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnHeaderHandler(
            BiFunction<String[], Integer, String> columnHeaderHandler) {
        this.columnHeaderHandler = columnHeaderHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnPropertiesHandler(
            BiFunction<T, Integer, Map<String, String>> columnPropertiesHandler) {
        this.columnPropertiesHandler = columnPropertiesHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnStyleHandler(
            BiFunction<T, Integer, String> columnStyleHandler) {
        this.columnStyleHandler = columnStyleHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnClassHandler(
            BiFunction<T, Integer, String> columnClassHandler) {
        this.columnClassHandler = columnClassHandler;
        return this;
    }

    public HtmlTableBuilder<T> setColumnHandler(BiFunction<T, Integer, String> columnHandler) {
        this.columnHandler = columnHandler;
        return this;
    }

    public HtmlTableBuilder<T> setRowHeaderPropertiesHandler(
            BiFunction<String[], Integer, Map<String, String>> rowHeaderPropertiesHandler) {
        this.rowHeaderPropertiesHandler = rowHeaderPropertiesHandler;
        return this;
    }

    public HtmlTableBuilder<T> setRowHeaderStyleHandler(
            BiFunction<String[], Integer, String> rowHeaderStyleHandler) {
        this.rowHeaderStyleHandler = rowHeaderStyleHandler;
        return this;
    }

    public HtmlTableBuilder<T> setRowHeaderClassHandler(
            BiFunction<String[], Integer, String> rowHeaderClassHandler) {
        this.rowHeaderClassHandler = rowHeaderClassHandler;
        return this;
    }

    public HtmlTableBuilder<T> setRowPropertiesHandler(
            BiFunction<T, Integer, Map<String, String>> rowPropertiesHandler) {
        this.rowPropertiesHandler = rowPropertiesHandler;
        return this;
    }

    public HtmlTableBuilder<T> setRowStyleHandler(BiFunction<T, Integer, String> rowStyleHandler) {
        this.rowStyleHandler = rowStyleHandler;
        return this;
    }

    public HtmlTableBuilder<T> setRowClassHandler(BiFunction<T, Integer, String> rowClassHandler) {
        this.rowClassHandler = rowClassHandler;
        return this;
    }

    public String build(List<T> table) {
        StringBuilder container = new StringBuilder();
        build(table, container);
        return container.toString();
    }

    public void build(List<T> table, StringBuilder container) {
        checkParameter();
        if (table == null) {
            table = Collections.emptyList();
        }
        startTable(container);
        appendTableHeader(container);
        startTbody(container);
        if (Whether.empty(mergeColumnSet)) {
            buildCommonTableContent(container, table);
        } else {
            if (isGroupByData) {
                buildTableContentMergeGroupByData(container, table);
            } else {
                buildTableContentMergeNearData(container, table);
            }
        }
        endTbody(container);
        endTable(container);
    }

    protected void checkParameter() {
        if (Whether.empty(columnHeadersList)) {
            throw new IllegalArgumentException("The column headers must be no empty");
        }
        if (Whether.empty(columnReferenceList)) {
            throw new IllegalArgumentException("The column references must be no empty");
        }
        int totalColumn = columnHeadersList.get(0).length;
        if (totalColumn != columnReferenceList.size()) {
            throw new IllegalArgumentException("Column headers and column references must be equal");
        }
        for (int i = 1; i < columnHeadersList.size(); i++) {
            if (columnHeadersList.get(i).length != totalColumn) {
                throw new IllegalArgumentException("The length of all column headers must be equal");
            }
        }
        if (!Whether.empty(mergeColumnSet)) {
            for (int mergeColumnIndex : mergeColumnSet) {
                if (mergeColumnIndex < 0 || mergeColumnIndex >= totalColumn) {
                    throw new IllegalArgumentException(
                            "The merge column index must in the column length range");
                }
            }
        }
    }

    protected void buildTableContentMergeNearData(StringBuilder container, List<T> table) {
        final Map<Integer, Integer> mergeColumnIndex = new HashMap<>(columnReferenceList.size(), 1);
        for (int r = 0; r < table.size(); r++) {
            T row = table.get(r);
            startTbodyTr(container, row, r);
            for (int c = 0; c < columnReferenceList.size(); c++) {
                final Function<T, Object> columnReference = columnReferenceList.get(c);
                // 和并列
                if (mergeColumnSet.contains(c)) {
                    if (r >= mergeColumnIndex.getOrDefault(c, 0)) {
                        Object data = columnReference.apply(row);
                        int i = r + 1;
                        while (i < table.size()) {
                            if (Objects.equals(data, columnReference.apply(table.get(i)))) {
                                i++;
                            } else {
                                break;
                            }
                        }
                        mergeColumnIndex.put(c, i);
                        startTd(container, row, c, i - r);
                        appendColumnValue(container, row, c);
                        endTd(container);
                    }
                } else {
                    startTd(container, row, c, 0);
                    appendColumnValue(container, row, c);
                    endTd(container);
                }
            }
            endCommonTr(container);
        }
    }

    protected void buildTableContentMergeGroupByData(StringBuilder container, List<T> table) {
        Map<Map<Integer, Object>, List<T>> grouping =
                table.stream()
                        .collect(
                                Collectors.groupingBy(
                                        t -> {
                                            Map<Integer, Object> result = new HashMap<>(mergeColumnSet.size(), 1);
                                            for (int mergeColumnIndex : mergeColumnSet) {
                                                result.put(
                                                        mergeColumnIndex, columnReferenceList.get(mergeColumnIndex).apply(t));
                                            }
                                            return result;
                                        }));
        int r = 0;
        for (Map.Entry<Map<Integer, Object>, List<T>> entry : grouping.entrySet()) {
            Map<Integer, Object> mergeColumns = entry.getKey();
            List<T> rows = entry.getValue();
            for (int i = 0; i < rows.size(); i++) {
                T row = rows.get(i);
                startTbodyTr(container, row, r++);
                for (int c = 0; c < columnReferenceList.size(); c++) {
                    if (mergeColumns.containsKey(c)) {
                        if (i == 0) {
                            startTd(container, row, c, rows.size());
                            appendColumnValue(container, row, c);
                            endTd(container);
                        }
                    } else {
                        startTd(container, row, c, 0);
                        appendColumnValue(container, row, c);
                        endTd(container);
                    }
                }
                endCommonTr(container);
            }
        }
    }

    protected void buildCommonTableContent(StringBuilder container, List<T> table) {
        for (int r = 0; r < table.size(); r++) {
            T row = table.get(r);
            startTbodyTr(container, row, r);
            for (int c = 0; c < columnReferenceList.size(); c++) {
                startTd(container, row, c, 0);
                appendColumnValue(container, row, c);
                endTd(container);
            }
            endCommonTr(container);
        }
    }

    protected void startTable(StringBuilder container) {
        container.append("<table");
        appendProperties(container, tableProperties);
        appendProperty(container, "class", tableClass);
        appendProperty(container, "style", tableStyle);
        container.append(">");
    }

    protected void endTable(StringBuilder container) {
        container.append("</table>");
    }

    protected void startThead(StringBuilder container) {
        container.append("<thead");
        appendProperties(container, theadProperties);
        appendProperty(container, "class", theadClass);
        appendProperty(container, "style", theadStyle);
        container.append(">");
    }

    protected void endThead(StringBuilder container) {
        container.append("</thead>");
    }

    protected void startTbody(StringBuilder container) {
        container.append("<tbody");
        appendProperties(container, tbodyProperties);
        appendProperty(container, "class", tbodyClass);
        appendProperty(container, "style", tbodyStyle);
        container.append(">");
    }

    protected void endTbody(StringBuilder container) {
        container.append("</tbody>");
    }

    protected void startTheadTr(StringBuilder container, String[] row, int rowIndex) {
        container.append("<tr");
        appendColumnProperties(
                container,
                row,
                rowIndex,
                rowHeaderPropertiesHandler,
                rowHeaderClassHandler,
                rowHeaderStyleHandler);
        container.append(">");
    }

    protected void startTbodyTr(StringBuilder container, T row, int rowIndex) {
        container.append("<tr");
        appendColumnProperties(
                container, row, rowIndex, rowPropertiesHandler, rowClassHandler, rowStyleHandler);
        container.append(">");
    }

    protected void endCommonTr(StringBuilder container) {
        container.append("</tr>");
    }

    protected void startTh(StringBuilder container, String[] row, int columnIndex, int colspan) {
        container.append("<th");
        if (colspan > 1) {
            appendProperty(container, "colspan", String.valueOf(colspan));
        }
        appendColumnProperties(
                container,
                row,
                columnIndex,
                columnHeaderPropertiesHandler,
                columnHeaderClassHandler,
                columnHeaderStyleHandler);
        container.append(">");
    }

    protected void endTh(StringBuilder container) {
        container.append("</th>");
    }

    protected void startTd(StringBuilder container, T row, int column, int rowspan) {
        container.append("<td");
        if (rowspan > 1) {
            appendProperty(container, "rowspan", String.valueOf(rowspan));
        }
        appendColumnProperties(
                container, row, column, columnPropertiesHandler, columnClassHandler, columnStyleHandler);
        container.append(">");
    }

    protected void endTd(StringBuilder container) {
        container.append("</td>");
    }

    protected <Data> void appendColumnProperties(
            StringBuilder container,
            Data row,
            int index,
            BiFunction<Data, Integer, Map<String, String>> propertiesHandler,
            BiFunction<Data, Integer, String> classHandler,
            BiFunction<Data, Integer, String> styleHandler) {
        if (propertiesHandler != null) {
            appendProperties(container, propertiesHandler.apply(row, index));
        }
        if (classHandler != null) {
            appendProperty(container, "class", classHandler.apply(row, index));
        }
        if (styleHandler != null) {
            appendProperty(container, "style", styleHandler.apply(row, index));
        }
    }

    protected void appendProperty(StringBuilder container, String key, String value) {
        if (key != null && value != null) {
            container
                    .append(" ")
                    .append(key.trim())
                    .append("=")
                    .append(quote)
                    .append(value.trim())
                    .append(quote);
        }
    }

    protected void appendProperties(StringBuilder container, Map<String, String> properties) {
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                appendProperty(container, entry.getKey(), entry.getValue());
            }
        }
    }

    protected void appendTableHeader(StringBuilder container) {
        startThead(container);
        final int maxLength = columnHeadersList.get(0).length;
        for (int r = columnHeadersList.size() - 1; r >= 0; r--) {
            String[] row = columnHeadersList.get(r);
            startTheadTr(container, row, columnHeadersList.size() - r - 1);
            if (r == 0) {
                for (int c = 0; c < row.length; c++) {
                    startTh(container, row, c, 0);
                    appendColumnHeader(container, row, c);
                    endTh(container);
                }
            } else {
                for (int c = 0; c < maxLength; ) {
                    final int i = c++;
                    while (c < maxLength && row[c] == null) {
                        c++;
                    }
                    // 标题列没有合并
                    if (c - i <= 1) {
                        startTh(container, row, i, 0);
                    } else {
                        startTh(container, row, i, c - i);
                    }
                    appendColumnHeader(container, row, i);
                    endTh(container);
                }
            }
            endCommonTr(container);
        }
        endThead(container);
    }

    protected void appendColumnHeader(StringBuilder container, String[] rowData, int columnIndex) {
        appendColumn(container, rowData, columnIndex, (row) -> row[columnIndex], columnHeaderHandler);
    }

    protected void appendColumnValue(StringBuilder container, T rowData, int columnIndex) {
        appendColumn(
                container, rowData, columnIndex, columnReferenceList.get(columnIndex), columnHandler);
    }

    protected <Data> void appendColumn(
            StringBuilder container,
            Data rowData,
            int columnIndex,
            Function<Data, Object> columnReference,
            BiFunction<Data, Integer, String> handler) {
        Object value = columnReference.apply(rowData);
        if (value == null) {
            value = "";
        }
        if (handler != null) {
            String newValue = handler.apply(rowData, columnIndex);
            if (newValue != null) {
                container.append(newValue);
            } else {
                container.append(value);
            }
        } else {
            container.append(value);
        }
    }
}
