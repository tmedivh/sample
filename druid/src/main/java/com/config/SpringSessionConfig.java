package com.config;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 开启http session
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1801)
public class SpringSessionConfig {

}
