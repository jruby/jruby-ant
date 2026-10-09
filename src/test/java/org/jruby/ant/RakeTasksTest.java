package org.jruby.ant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Exercises the Java half of the tasks against a stub RakeWrapper. */
class RakeTasksTest {
    static class StubRake extends Rake {
        @Override protected String rakeTasksLibrary() { return "jruby_ant_stub/raketasks"; }
    }

    static class StubRakeImport extends RakeImport {
        @Override protected String rakeTasksLibrary() { return "jruby_ant_stub/raketasks"; }
    }

    private final Project project = new Project();

    @BeforeEach
    void reset() {
        Recorder.CALLS.clear();
    }

    @Test
    void defaultLibraryIsTheRakeAntGemPath() {
        assertEquals("rake/ant/tasks/raketasks", new RakeTaskBase() {
            { /* skip acquireRakeReference side effects by not calling execute */ }
            @Override protected void acquireRakeReference() { }
        }.rakeTasksLibrary());
    }

    @Test
    void rakeRunsDefaultWhenNoTaskOrFile() {
        Rake task = new StubRake();
        task.setProject(project);
        task.execute();

        assertEquals(1, Recorder.CALLS.size());
        assertEquals("execute", Recorder.CALLS.get(0)[0]);
        assertEquals(Collections.emptyList(), Recorder.CALLS.get(0)[1]);
    }

    @Test
    void rakePassesFileAndTask() {
        Rake task = new StubRake();
        task.setProject(project);
        task.setFile("Custom.rake");
        task.setTask("build");
        task.execute();

        assertEquals(Arrays.asList("-f", "Custom.rake", "build"), Recorder.CALLS.get(0)[1]);
    }

    @Test
    void antProjectIsExposedToRuby() {
        Rake task = new StubRake();
        task.setProject(project);
        task.execute();

        assertSame(project, Recorder.CALLS.get(0)[2]);
    }

    @Test
    void importPassesFile() {
        RakeImport task = new StubRakeImport();
        task.setProject(project);
        task.setFile("tasks.rake");
        task.execute();

        assertEquals("import", Recorder.CALLS.get(0)[0]);
        assertEquals(Arrays.asList("-f", "tasks.rake"), Recorder.CALLS.get(0)[1]);
    }

    @Test
    void rubyErrorsBecomeBuildExceptions() {
        Rake task = new StubRake();
        task.setProject(project);
        task.setTask("explode");

        BuildException e = assertThrows(BuildException.class, task::execute);
        assertTrue(e.getMessage().startsWith("Build failed: "), e.getMessage());
        assertTrue(e.getMessage().contains("rake aborted!"), e.getMessage());
    }
}
