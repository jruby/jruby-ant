package org.jruby.ant;

import java.util.ArrayList;
import java.util.List;

/** Receives calls made by the stub RakeWrapper in jruby_ant_stub/raketasks.rb. */
public class Recorder {
    public static final List<Object[]> CALLS = new ArrayList<>();

    public static void record(String method, List<?> args, Object project) {
        CALLS.add(new Object[] {method, new ArrayList<Object>(args), project});
    }
}
