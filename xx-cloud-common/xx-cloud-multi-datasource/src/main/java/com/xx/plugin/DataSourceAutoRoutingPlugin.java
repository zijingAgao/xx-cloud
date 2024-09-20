package com.xx.plugin;

import com.xx.annotation.DS;
import com.xx.datasource.DynamicDataSourceContextHolder;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 数据源自动切换插件
 * 基于mybatis的Interceptor
 *
 * @author Agao
 * @date 2024/9/20 14:35
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DataSourceAutoRoutingPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        DS methodDs = invocation.getMethod().getAnnotation(DS.class);
        DS classDs = invocation.getClass().getAnnotation(DS.class);

        String pushedDataSource = null;
        try {
            String dataSource = methodDs != null ? methodDs.value() : classDs != null ? classDs.value() : null;

            if (dataSource != null) {
                pushedDataSource = DynamicDataSourceContextHolder.push(dataSource);
            }
            return invocation.proceed();

        } finally {
            if (pushedDataSource != null) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}

