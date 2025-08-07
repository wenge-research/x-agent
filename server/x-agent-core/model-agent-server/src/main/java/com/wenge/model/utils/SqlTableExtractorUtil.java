package com.wenge.model.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.util.HashSet;
import java.util.Set;

public class SqlTableExtractorUtil {

    /**
     * 获取SQL中的表名
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    public static Set<String> extractTables(String sql) throws JSQLParserException {
        Set<String> tables = new HashSet<>();
        Statement statement = CCJSqlParserUtil.parse(sql);

        if (statement instanceof Select) {
            Select select = (Select) statement;
            // 处理 WITH 子句（CTE）
            if (select.getWithItemsList() != null) {
                for (WithItem withItem : select.getWithItemsList()) {
                    tables.add(withItem.getName().toLowerCase());
                }
            }

            // 获取 SelectBody 并处理
            SelectBody selectBody = select.getSelectBody();
            if (selectBody instanceof PlainSelect) {
                processPlainSelect((PlainSelect) selectBody, tables);
            } else if (selectBody instanceof SetOperationList) {
                // 处理 UNION, INTERSECT 等操作
                for (SelectBody body : ((SetOperationList) selectBody).getSelects()) {
                    if (body instanceof PlainSelect) {
                        processPlainSelect((PlainSelect) body, tables);
                    }
                }
            }
        } else if (statement instanceof Insert) {
            Insert insert = (Insert) statement;
            String name = insert.getTable().getName();
            tables.add(name.toLowerCase());
        } else if (statement instanceof Update) {
            Update update = (Update) statement;
            String name = update.getTable().getName();
            tables.add(name.toLowerCase());
        } else if (statement instanceof Delete) {
            Delete delete = (Delete) statement;
            String name = delete.getTable().getName();
            tables.add(name.toLowerCase());
        }

        return tables;
    }

    /**
     * 处理 PlainSelect
     * @param plainSelect
     * @param tables
     */
    private static void processPlainSelect(PlainSelect plainSelect, Set<String> tables) {
        // 处理 FROM 子句
        FromItem fromItem = plainSelect.getFromItem();
        processFromItem(fromItem, tables);

        // 处理 JOIN 子句
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                processFromItem(join.getRightItem(), tables);
            }
        }

        // 处理子查询（WHERE 子句中的子查询等）
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(new TableNameFinder(tables));
        }
    }


    /**
     * 处理 FROM 子句
     * @param fromItem
     * @param tables
     */
    private static void processFromItem(FromItem fromItem, Set<String> tables) {
        if (fromItem instanceof Table) {
            tables.add(((Table) fromItem).getName().toLowerCase());
        } else if (fromItem instanceof SubSelect) {
            // 处理子查询
            SelectBody subSelectBody = ((SubSelect) fromItem).getSelectBody();
            if (subSelectBody instanceof PlainSelect) {
                processPlainSelect((PlainSelect) subSelectBody, tables);
            }
        } else if (fromItem instanceof LateralSubSelect) {
            // 处理 LATERAL 子查询
            SelectBody subSelectBody = ((LateralSubSelect) fromItem).getSubSelect().getSelectBody();
            if (subSelectBody instanceof PlainSelect) {
                processPlainSelect((PlainSelect) subSelectBody, tables);
            }
        }
    }

    // 用于查找 WHERE 子句中的表名
    private static class TableNameFinder extends ExpressionVisitorAdapter {
        private final Set<String> tables;

        public TableNameFinder(Set<String> tables) {
            this.tables = tables;
        }

        @Override
        public void visit(SubSelect subSelect) {
            if (subSelect.getSelectBody() instanceof PlainSelect) {
                processPlainSelect((PlainSelect) subSelect.getSelectBody(), tables);
            }
        }
    }
}
