package cz.ajraa.tournament;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModulithArchitectureTest {

    @Test
    void verifyArchitecture() {
        ApplicationModules modules = ApplicationModules.of(SpringApiApplication.class);
        modules.verify();
    }
}
