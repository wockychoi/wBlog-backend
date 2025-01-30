package com.mapago.config.interceptor;

import com.mapago.model.Audit;
import com.mapago.service.user.UserService;
import java.time.LocalDateTime;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})
})
public class AuditInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];

        if (parameter instanceof Audit audit) {
            String currentUserId = UserService.getCurrentUserId();
            if (audit.getCreId() == null) {
                audit.setCreId(currentUserId);
            }
            audit.setUpdId(currentUserId);
            if (audit.getCreDt() == null) {
                audit.setCreDt(LocalDateTime.now());
            }
            audit.setUpdDt(LocalDateTime.now());
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 필요 시 추가 설정
    }
}