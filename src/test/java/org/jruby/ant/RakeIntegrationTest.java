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
 * Runs a real build.xml that calls a real Rakefile through the rake-ant gem.
 *
 * Needs the gem's lib directory on RUBYLIB; surefire sets that from
 * -Drake.ant.lib=/path/to/rake-ant/lib, and the test is skipped without it.
 */
class RakeIntegrationTest {
    @Test
    void antBuildRunsRakeTask(@TempDir Path tmp) throws Exception {
        String lib = System.getProperty("rake.ant.lib", "");
        assumeTrue(!lib.isEmpty(), "set -Drake.ant.lib=<rake-ant checkout>/lib to run");
        assumeTrue(new File(lib, "rake/ant/tasks/raketasks.rb").isFile(), "rake-ant not found in " + lib);

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
}
