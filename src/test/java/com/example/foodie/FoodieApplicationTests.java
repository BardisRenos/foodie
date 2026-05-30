package com.example.foodie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class FoodieApplicationTests {

    ApplicationModules modules = ApplicationModules.of(FoodieApplication.class);

    @Test
    void contextLoads() {
    }

    /**
     * Prints a human-readable overview of all modules and their dependencies.
     * Run this whenever you want to see the architecture.
     */
    @Test
    void printModuleOverview() {
        modules.forEach(System.out::println);
    }

    /**
     * Generates architecture documentation (AsciiDoc + PlantUML diagrams)
     * into target/spring-modulith-docs/
     */
    @Test
    void generateDocumentation() {
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}
