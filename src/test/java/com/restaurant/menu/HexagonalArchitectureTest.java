package com.restaurant.menu;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class HexagonalArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
        .importPackages("com.restaurant.menu");

    @Test
    void domainLayerShouldNotDependOnInfrastructure() {
        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure");

        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnSpring() {
        ArchRule rule = noClasses()
            .that().resideInAnyPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
            .because("Domain layer must be framework-free");

        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnJpa() {
        ArchRule rule = noClasses()
            .that().resideInAnyPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..")
            .because("Domain layer must not depend on JPA");

        rule.check(classes);
    }

    @Test
    void applicationShouldOnlyDependOnDomain() {
        ArchRule rule = noClasses()
            .that().resideInAnyPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..")
            .because("Application layer must not depend on infrastructure");

        rule.check(classes);
    }

}
