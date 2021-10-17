package cn.edu.neu.citybrain.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DBQuery {
    ExecutorService executorService;
    private final boolean useMultiQuery;

    private List<QueryRunnable> queryTasks = new ArrayList<>();
    private Map<String, List> resultsWithTag = new HashMap<>();

    public DBQuery(ExecutorService executorService) {
        this(executorService, true);
    }

    public DBQuery(ExecutorService executorService, boolean useMultiQuery) {
        this.executorService = executorService;
        this.useMultiQuery = useMultiQuery;
    }

    public void add(String tag, String sql, Class<?> clz, List<String> fieldList, Object... parameters) {
        queryTasks.add(new QueryRunnable<>(tag, sql, clz, fieldList, parameters));
    }

    public <T> List<T> get(String tag) {
        return (List<T>) resultsWithTag.get(tag);
    }

    public void execute() {
        CountDownLatch latch = null;
        if (useMultiQuery) {
            latch = new CountDownLatch(queryTasks.size());
        }

        for (QueryRunnable queryTask : queryTasks) {
            queryTask.latch = latch;
            if (useMultiQuery) {
                executorService.execute(queryTask);
            } else {
                queryTask.run();
            }
        }

        if (useMultiQuery) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (QueryRunnable queryTask : queryTasks) {
            resultsWithTag.put(queryTask.tag, queryTask.results);
        }
    }

    private static class QueryRunnable<T> implements Runnable {
        public String tag;
        public String sql;
        public Class<T> clazz;
        public List<T> results;
        public CountDownLatch latch;
        public List<String> fieldList;
        public Object[] parameters;

        public QueryRunnable(String tag, String sql, Class<T> clazz, List<String> fieldList, Object... parameters) {
            this.tag = tag;
            this.sql = sql;
            this.clazz = clazz;
            this.fieldList = fieldList;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            try {
                results = executeQuery();
            } finally {
                if (latch != null) {
                    latch.countDown();
                }
            }
        }

        private List<T> executeQuery() {
            JdbcSupport dao = new JdbcSupport();
            List<T> results = dao.query(sql, clazz, fieldList, parameters);
            dao.close();
            return results;
        }
    }
}