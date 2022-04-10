package com.myg.yxy.config;

import com.hxsoft.hsf.boot.component.db.PaginationInterceptor;
import com.hxsoft.hsf.boot.component.db.PaginationSettings;
import com.hxsoft.hsf.boot.component.db.dialect.SqlDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPaginationConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationSettings settings = PaginationSettings.builder()
                .sqlDialect(SqlDialect.MYSQL)
                .defaultPage(1)
                .defaultPageSize(10)
                .build();
        return new PaginationInterceptor(settings);
    }
}