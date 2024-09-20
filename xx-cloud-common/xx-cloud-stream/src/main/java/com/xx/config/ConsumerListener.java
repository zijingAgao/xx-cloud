package com.xx.config;

import com.xx.pojo.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * @author Agao
 * @date 2024/6/3 16:44
 */
@Configuration
public class ConsumerListener {

  /**
   * 邮件消费者 消费者，方法名就是交换机名字前缀
   *
   * @return
   */
  @Bean
  @Transactional
  public Consumer<Person> emailMsg() {
    return person -> System.out.println("Received: " + person);
  }

  /**
   * 短信消费者 消费者，方法名就是交换机名字前缀
   *
   * @return
   */
  @Bean
  public Consumer<Person> shortMsg() {
    return person -> System.out.println("Received: " + person);
  }
}
