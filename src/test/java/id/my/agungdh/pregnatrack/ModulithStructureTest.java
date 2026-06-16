package id.my.agungdh.pregnatrack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithStructureTest {

    @Test
    @DisplayName("Spring Modulith module structure has no cyclic dependencies")
    void noCyclicModuleDependencies() {
        ApplicationModules modules = ApplicationModules.of(PregnaTrackApplication.class);
        modules.verify();
    }
}
