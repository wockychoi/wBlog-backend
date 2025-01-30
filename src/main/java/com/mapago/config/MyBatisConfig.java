package com.mapago.config;

import com.github.pagehelper.PageInterceptor;
import com.mapago.config.interceptor.AuditInterceptor;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.mapago.mapper")
public class MyBatisConfig {

    @Value("${tencent.cos.url}")
    private String tencentCosUrl;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/**/*.xml"));

        org.apache.ibatis.session.Configuration myBatisConfig = new org.apache.ibatis.session.Configuration();
        myBatisConfig.setMapUnderscoreToCamelCase(true);
        myBatisConfig.setCacheEnabled(true);
        myBatisConfig.setLazyLoadingEnabled(false);
        sessionFactory.setConfiguration(myBatisConfig);

        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mariadb");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(properties);

        sessionFactory.setPlugins(new Interceptor[]{new AuditInterceptor()});

        Properties sqlSessionFactoryProperties = new Properties();
        sqlSessionFactoryProperties.setProperty("tencentCosUrl", tencentCosUrl); // 설정값 주입
        sessionFactory.setConfigurationProperties(sqlSessionFactoryProperties);

        return sessionFactory.getObject();
    }
}