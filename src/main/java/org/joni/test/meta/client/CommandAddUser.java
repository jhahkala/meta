package org.joni.test.meta.client;

import com.beust.jcommander.Parameter;

public class CommandAddUser {

    @Parameter(names = "--name", description = "Mandatory name of the user.")
    public String name = null;

    @Parameter(names = "--root", description = "Optionally add a named root directory at the same time.")
    public String root;

    @Parameter(names = "--sla", description = "Optionally set the sla of the root that is added.")
    public String sla;

}
