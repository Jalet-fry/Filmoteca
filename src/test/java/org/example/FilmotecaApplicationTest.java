//package org.example;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class FilmotecaApplicationTest {
//
//    @Test
//    void contextLoadsSuccessfully(ApplicationContext context) {
//        assertNotNull(context, "Application context should not be null");
//    }
//
//    @Test
//    void applicationContextContainsRequiredBeans(ApplicationContext context) {
//        assertAll(
//                () -> assertNotNull(context.getBean("inMemoryCacheImpl"),
//                        "inMemoryCacheImpl bean should be present"),
//                () -> assertNotNull(context.getBean("labControllerAdvice"),
//                        "labControllerAdvice bean should be present")
//        );
//    }
//}