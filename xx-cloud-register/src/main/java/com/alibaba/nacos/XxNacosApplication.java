package com.alibaba.nacos;

import com.alibaba.nacos.config.ConfigConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author nacos
 * <p>
 * nacos console 源码运行，方便开发 生产从官网下载zip最新版集群配置运行
 */
@EnableScheduling
@SpringBootApplication
public class XxNacosApplication {

	public static void main(String[] args) {
		if (initEnv()) {
			SpringApplication.run(XxNacosApplication.class, args);
		}
	}

	/**
	 * 初始化运行环境
	 */
	private static boolean initEnv() {
		System.setProperty(ConfigConstants.STANDALONE_MODE, "true");
		System.setProperty(ConfigConstants.AUTH_ENABLED, "true");
		System.setProperty(ConfigConstants.LOG_BASEDIR, "logs");
		System.setProperty(ConfigConstants.LOG_ENABLED, "false");
		System.setProperty(ConfigConstants.NACOS_CONTEXT_PATH, "/nacos");
		return true;
	}

}
