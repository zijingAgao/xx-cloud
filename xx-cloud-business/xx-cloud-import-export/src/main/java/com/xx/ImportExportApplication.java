package com.xx;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Agao
 * @date 2024/9/24 10:38
 */
@SpringBootApplication
public class ImportExportApplication {
  public static void main(String[] args) {
//    SpringApplication.run(ImportExportApplication.class, args);
    new SpringApplicationBuilder()
            .sources(ImportExportApplication.class)
//            .main(ImportExportApplication.class)
            .allowCircularReferences(true)
            .properties("application.yaml").run(args);
  }

}
