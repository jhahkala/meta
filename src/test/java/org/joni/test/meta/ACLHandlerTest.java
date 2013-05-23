package org.joni.test.meta;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class ACLHandlerTest extends TestCase {
    public static final String TEST_USER = "john";

    @Test
    public void testReadAccess() {
        boolean exception = false;
        try {

            ACLHandler.hasReadAccess(null, null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            ACLHandler.hasReadAccess(TEST_USER, null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            List<ACLItem> acl = new ArrayList<ACLItem>();
            acl.add(new ACLItem(TEST_USER, true, true));
            ACLHandler.hasReadAccess(null, acl);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        List<ACLItem> acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, true, true));
        assertTrue(ACLHandler.hasReadAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasReadAccess("Peter", acl));

        acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, false, true));
        assertFalse(ACLHandler.hasReadAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasReadAccess("Peter", acl));

        acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, false, false));
        assertFalse(ACLHandler.hasReadAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasReadAccess("Peter", acl));

        acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, true, false));
        assertTrue(ACLHandler.hasReadAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasReadAccess("Peter", acl));

        // stupid to need this for getting 100% in unit test coverage...
        @SuppressWarnings("unused")
        ACLHandler handler = new ACLHandler();
    }

    @Test
    public void testWriteAccess() {
        boolean exception = false;
        try {

            ACLHandler.hasWriteAccess(null, null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            ACLHandler.hasWriteAccess(TEST_USER, null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            List<ACLItem> acl = new ArrayList<ACLItem>();
            acl.add(new ACLItem(TEST_USER, true, true));
            ACLHandler.hasWriteAccess(null, acl);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        List<ACLItem> acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, true, true));
        assertTrue(ACLHandler.hasWriteAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasWriteAccess("Peter", acl));

        acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, true, false));
        assertFalse(ACLHandler.hasWriteAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasWriteAccess("Peter", acl));

        acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, false, false));
        assertFalse(ACLHandler.hasWriteAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasWriteAccess("Peter", acl));

        acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, false, true));
        assertTrue(ACLHandler.hasWriteAccess(TEST_USER, acl));
        assertFalse(ACLHandler.hasWriteAccess("Peter", acl));
    }

    @Test
    public void testEquals() {
        List<ACLItem> acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem(TEST_USER, true, true));
        List<ACLItem> acl2 = new ArrayList<ACLItem>();
        acl2.add(new ACLItem(TEST_USER, true, true));
        assertTrue(acl.equals(acl2));
        assertTrue(acl.equals(acl));
        ACLItem item = new ACLItem(TEST_USER, true, true);
        ACLItem item2 = new ACLItem(TEST_USER, true, true);
        assertEquals(item, item2);
        assertEquals(item, item);
        assertTrue(item.equals(item2));
        assertFalse(item.equals(null));
        assertFalse(item.equals(TEST_USER));
        assertFalse(item.equals(new ACLItem(TEST_USER, false, true)));
        assertFalse(item.equals(new ACLItem(TEST_USER, true, false)));
        assertTrue(item.equals(new ACLItem(TEST_USER, true, true)));
        assertFalse(item.equals(new ACLItem(null, true, true)));
        assertFalse(item.equals(new ACLItem("Peter", true, true)));
        assertFalse(new ACLItem(null, true, true).equals(new ACLItem("Peter", true, true)));
        assertTrue(new ACLItem(null, true, true).equals(new ACLItem(null, true, true)));
        assertTrue(new ACLItem(TEST_USER, true, true).equals(new ACLItem(TEST_USER, true, true)));

    }

    @Test
    public void testHashCode() {
        assertEquals(new ACLItem(TEST_USER, false, true).hashCode(), new ACLItem(TEST_USER, false, true).hashCode());
        assertEquals(new ACLItem(TEST_USER, false, false).hashCode(), new ACLItem(TEST_USER, false, false).hashCode());
        assertEquals(new ACLItem(TEST_USER, true, true).hashCode(), new ACLItem(TEST_USER, true, true).hashCode());
        assertEquals(new ACLItem(TEST_USER, true, false).hashCode(), new ACLItem(TEST_USER, true, false).hashCode());
        assertEquals(new ACLItem(null, true, false).hashCode(), new ACLItem(null, true, false).hashCode());
        assertNotSame(new ACLItem(TEST_USER, true, false).hashCode(), new ACLItem(TEST_USER, true, true).hashCode());
        assertNotSame(new ACLItem(TEST_USER, true, false).hashCode(), new ACLItem(TEST_USER, false, true).hashCode());
        assertNotSame(new ACLItem(TEST_USER, true, false).hashCode(), new ACLItem(TEST_USER, false, false).hashCode());
        assertNotSame(new ACLItem("peter", true, false).hashCode(), new ACLItem(TEST_USER, false, false).hashCode());
        assertNotSame(new ACLItem("peter", true, false).hashCode(), new ACLItem(TEST_USER, true, false).hashCode());
        assertNotSame(new ACLItem(null, true, false).hashCode(), new ACLItem(TEST_USER, true, false).hashCode());
    }
}
