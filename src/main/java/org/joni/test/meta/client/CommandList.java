package org.joni.test.meta.client;

import com.beust.jcommander.Parameter;

public class CommandList {
    @Parameter(names = { "-r", "--recursive" }, description = "Use to list also subdirectories.")
    public boolean recursive = false;

    @Parameter(names = "--root", description = "Define the directory to start listing from. Defauld is the root.")
    public String root;

    @Parameter(names = "--name", description = "Define the user name to start listing from. Defauld is the one defined by the credential.")
    public String userName;

}
