package id.my.agungdh.pregnatrack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BeanCycleDependencyTest {

    private static final String PROJECT_PACKAGE_PREFIX = "id.my.agungdh.";

    @Test
    @DisplayName("no cyclic bean dependencies in application context")
    void noCyclicBeanDependencies(ApplicationContext context) {
        ConfigurableListableBeanFactory factory =
                (ConfigurableListableBeanFactory) context.getAutowireCapableBeanFactory();

        Map<String, Set<String>> graph = new HashMap<>();
        for (String name : factory.getBeanDefinitionNames()) {
            if (!isProjectBean(name, factory)) {
                continue;
            }
            String[] deps = factory.getDependenciesForBean(name);
            Set<String> filtered = new HashSet<>();
            for (String dep : deps) {
                if (isProjectBean(dep, factory)) {
                    filtered.add(dep);
                }
            }
            graph.put(name, filtered);
        }

        List<List<String>> cycles = findCycles(graph);

        assertTrue(cycles.isEmpty(),
                "Found cyclic bean dependencies:\n" + formatCycles(cycles));
    }

    private boolean isProjectBean(String name, ConfigurableListableBeanFactory factory) {
        try {
            String type = factory.getBeanDefinition(name).getBeanClassName();
            return type != null && type.startsWith(PROJECT_PACKAGE_PREFIX);
        } catch (Exception e) {
            return false;
        }
    }

    private List<List<String>> findCycles(Map<String, Set<String>> graph) {
        List<List<String>> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        List<String> stack = new ArrayList<>();

        List<String> ordered = new ArrayList<>(graph.keySet());
        Collections.sort(ordered);
        for (String node : ordered) {
            if (!visited.contains(node)) {
                dfs(node, graph, visited, inStack, stack, cycles);
            }
        }
        return cycles;
    }

    private void dfs(String node, Map<String, Set<String>> graph,
                     Set<String> visited, Set<String> inStack,
                     List<String> stack, List<List<String>> cycles) {
        visited.add(node);
        inStack.add(node);
        stack.add(node);

        List<String> deps = new ArrayList<>(graph.getOrDefault(node, Collections.emptySet()));
        Collections.sort(deps);
        for (String dep : deps) {
            if (!graph.containsKey(dep)) {
                continue;
            }
            if (!visited.contains(dep)) {
                dfs(dep, graph, visited, inStack, stack, cycles);
            } else if (inStack.contains(dep)) {
                int idx = stack.indexOf(dep);
                List<String> cycle = new ArrayList<>(stack.subList(idx, stack.size()));
                cycle.add(dep);
                cycles.add(cycle);
            }
        }

        stack.remove(stack.size() - 1);
        inStack.remove(node);
    }

    private String formatCycles(List<List<String>> cycles) {
        StringBuilder sb = new StringBuilder();
        for (List<String> cycle : cycles) {
            sb.append("  ").append(String.join(" -> ", cycle)).append('\n');
        }
        return sb.toString();
    }
}
