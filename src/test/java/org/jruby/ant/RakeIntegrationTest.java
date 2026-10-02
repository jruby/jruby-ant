package org.jruby.ant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Runs a real build.xml that calls a real Rakefile through the rake-ant gem
 * that is bundled into the jar (target/classes during the build).
 *
 * rake-ant finds Ant's jars via ANT_HOME or by running `ant -diagnostics`, so
 * the test is skipped on machines without an Ant install.
 */
class RakeIntegrationTest {
    @Test
    void antBuildRunsRakeTask(@TempDir Path tmp) throws Exception {
        assumeTrue(antInstalled(), "needs ANT_HOME or ant on the PATH");

        Path marker = tmp.resolve("marker.txt");
        Path rakefile = tmp.resolve("Rakefile");
        Files.copy(new File("src/test/resources/integration/Rakefile").toPath(), rakefile);

        Project project = new Project();
        DefaultLogger logger = new DefaultLogger();
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setMessageOutputLevel(Project.MSG_WARN);
        project.addBuildListener(logger);
        project.init();
        project.setUserProperty("rakefile", rakefile.toString());
        System.setProperty("marker.file", marker.toString());
        File buildFile = new File("src/test/resources/integration/build.xml");
        ProjectHelper.configureProject(project, buildFile);
        project.executeTarget("run-rake");

        assertEquals("ran from rake via ant\n", new String(Files.readAllBytes(marker), StandardCharsets.UTF_8));
    }

    private static boolean antInstalled() {
        String antHome = System.getenv("ANT_HOME");
        if (antHome != null && new File(antHome).exists()) return true;
        String path = System.getenv("PATH");
        if (path == null) return false;
        for (String dir : path.split(File.pathSeparator)) {
            if (new File(dir, "ant").canExecute() || new File(dir, "ant.bat").canExecute()) return true;
        }
        return false;
    }
}
