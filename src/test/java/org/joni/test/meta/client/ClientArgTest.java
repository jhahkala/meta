package org.joni.test.meta.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.beust.jcommander.JCommander;

public class ClientArgTest {
    @Test
    public void testAddUser() {
        CommandMain cm = new CommandMain();
        JCommander jc = new JCommander(cm);

        CommandAddUser add = new CommandAddUser();
        jc.addCommand("addUser", add);
        jc.parse("-v", "--conf", "testfile.conf", "addUser", "--name", "cn=lasdkj, c=ch", "--root", "lasd root");

        assertTrue(cm.verbose);
        assertEquals(cm.configFile, "testfile.conf");
        assertEquals(jc.getParsedCommand(), "addUser");
        assertEquals(add.name, "cn=lasdkj, c=ch");
        assertEquals(add.root, "lasd root");
    }

    @Test
    public void testList() {
        CommandMain cm = new CommandMain();
        JCommander jc = new JCommander(cm);

        CommandList list = new CommandList();
        jc.addCommand("list", list);
        jc.parse("-v", "--conf", "testfile.conf", "list", "--name", "cn=lasdkj, c=ch");

        assertTrue(cm.verbose);
        assertEquals(cm.configFile, "testfile.conf");
        assertEquals(jc.getParsedCommand(), "list");
        assertEquals(list.userName, "cn=lasdkj, c=ch");
    }

}
