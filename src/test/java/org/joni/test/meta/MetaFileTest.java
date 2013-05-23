package org.joni.test.meta;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.eaio.uuid.UUID;

import junit.framework.TestCase;

public class MetaFileTest extends TestCase {

    @Test
    public void testConst1() {
        MetaFile file = new MetaFileImpl();
        assertNotNull(file);
    }

    @Test
    public void testConst2() {
        UUID id = new UUID();
        MetaFile file = new MetaFileImpl(id);
        assertTrue(file.getId().equals(id));
    }

    @Test
    public void testDirectory() {
        MetaFile file = new MetaFileImpl();
        assertFalse(file.isDirectory());
        file.setDirectory(true);
        assertTrue(file.isDirectory());
        file.setDirectory(false);
        assertFalse(file.isDirectory());
        file.setDirectory(true);
        MetaFile newFile = new MetaFileImpl().setParent(file.getId());
        file.addFile(newFile);
        boolean exception = false;
        file.setDirectory(true);
        try {
            file.setDirectory(false);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        // test bizarre way of switching from dir to file
        file.removeFile(newFile.getId());
        file.setDirectory(false);
        assertNull(file.listFiles());

    }

    @Test
    public void testParent() {
        MetaFile file = new MetaFileImpl();
        assertNull(file.getParent());
        UUID id = new UUID();
        file.setParent(id);
        assertTrue(file.getParent().equals(id));
        UUID id2 = new UUID();
        file.setParent(id2);
        assertTrue(file.getParent().equals(id2));
    }

    @Test
    public void testFiles() {
        MetaFile file = new MetaFileImpl();
        file.setDirectory(true);
        MetaFile file1 = new MetaFileImpl();
        assertNull(file.listFiles());
        file1.setParent(file.getId());
        file.addFile(file1);
        List<UUID> fileIds = file.listFiles();
        assertTrue(fileIds.size() == 1);
        assertTrue(fileIds.get(0).equals(file1.getId()));
        file.removeFile(file1.getId());
        fileIds = file.listFiles();
        assertTrue(fileIds.size() == 0);

        List<MetaFile> newFiles = new ArrayList<MetaFile>();
        for (int i = 0; i < 10; i++) {
            newFiles.add(new MetaFileImpl());
            newFiles.get(i).setParent(file.getId());
            file.addFile(newFiles.get(i));
        }
        List<UUID> newFileIds = file.listFiles();
        assertTrue(newFileIds.size() == 10);
        int n = 0;
        for (MetaFile newNewFile : newFiles) {
            assertEquals(newNewFile.getId(), newFileIds.get(n++));
        }

        for (int i = 0; i < 5; i++) {
            file.removeFile(newFileIds.get(2 * i));
        }

        List<UUID> newNewFileIds = file.listFiles();

        assertTrue(newNewFileIds.size() == 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(newNewFileIds.get(i), newFiles.get((i * 2) + 1).getId());
        }

    }

    @Test
    public void testAddFile() {
        MetaFile file = new MetaFileImpl();
        boolean exception = false;
        try {
            file.addFile(null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            file.addFile(new MetaFileImpl().setParent(file.getId()));
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            file.addFile(new MetaFileImpl());
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        file.setDirectory(true);
        exception = false;
        try {
            file.addFile(null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        exception = false;
        try {
            file.addFile(new MetaFileImpl());
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        exception = false;
        try {
            file.addFile(new MetaFileImpl().setParent(file.getId()));
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertFalse(exception);
    }

    @Test
    public void testRemoveFile() {
        MetaFile file = new MetaFileImpl();
        boolean exception = false;
        try {
            file.removeFile(null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            file.removeFile(new UUID());
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        MetaFile newFile = new MetaFileImpl().setParent(file.getId());
        file.setDirectory(true);
        exception = false;
        try {
            file.removeFile(new UUID());
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        file.addFile(newFile);
        exception = false;
        try {
            file.removeFile(null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            file.removeFile(new UUID());
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            file.removeFile(newFile.getId());
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertFalse(exception);
    }

    @Test
    public void testToString() {
        UUID id = new UUID();
        MetaFile file = new MetaFileImpl(id);
        assertEquals(file.toString(), "null f id: " + id);
        file.setName("File");
        assertEquals(file.toString(), "File f id: " + id);
        file.setDirectory(true);
        assertEquals(file.toString(), "File d id: " + id);

    }

    @Test
    public void testLength() {
        MetaFile file = new MetaFileImpl();
        assertEquals(file.getLength(), 0);
        file.setLength(1000010);
        assertEquals(file.getLength(), 1000010);

    }

    @Test
    public void testId() {
        UUID id = new UUID();
        MetaFile file = new MetaFileImpl(id);
        assertEquals(id, file.getId());
        UUID id2 = new UUID();
        assertNotSame(id2, file.getId());
        MetaFile file2 = new MetaFileImpl();
        assertNotSame(id, file2.getId());
        assertNotSame(id2, file2.getId());
    }

    @Test
    public void testName() {
        MetaFile file = new MetaFileImpl();
        assertNull(file.getName());
        file.setName("File");
        assertEquals("File", file.getName());
        file.setName("dir");
        assertEquals("dir", file.getName());
        file.setDirectory(true);
        file.setName("root");
        assertEquals("root", file.getName());
        boolean exception = false;
        try {
            file.setName(null);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);
    }

    @Test
    public void testBlockSize() {
        MetaFile file = new MetaFileImpl();
        assertEquals(file.getBlockSize(), 10000);
        file.setBlockSize(1000010);
        assertEquals(file.getBlockSize(), 1000010);
        file.setBlockSize(0);
        assertEquals(file.getBlockSize(), 0);

    }

    @Test
    public void testStripes() throws URISyntaxException {
        MetaFile file = new MetaFileImpl();
        assertNull(file.getStripes());
        file.setStripes(null);

        List<StripeLocation> locations = new ArrayList<StripeLocation>();
        file.setStripes(locations);
        assertEquals(file.getStripes().size(), 0);
        locations.add(new StripeLocation(new URI("http://location"), "type", "1.0"));
        assertEquals(file.getStripes().size(), 0);
        file.setStripes(locations);
        assertEquals(file.getStripes().size(), 1);
        StripeLocation location = file.getStripes().get(0);
        assertEquals(location.getURI(), locations.get(0).getURI());
        assertEquals(location.getType(), locations.get(0).getType());
        assertEquals(location.getVersion(), locations.get(0).getVersion());
        file.setStripes(null);
        assertNull(file.getStripes());

    }

    @Test
    public void testKeyPieces() throws MalformedURLException {
        MetaFile file = new MetaFileImpl();
        assertNull(file.getKeyPieces());
        file.setKeyPieces(null);

        List<KeyPieceLocation> locations = new ArrayList<KeyPieceLocation>();
        file.setKeyPieces(locations);
        assertEquals(file.getKeyPieces().size(), 0);
        locations.add(new KeyPieceLocation(new URL("http://location"), "type", "1.0"));
        assertEquals(file.getKeyPieces().size(), 0);
        file.setKeyPieces(locations);
        assertEquals(file.getKeyPieces().size(), 1);
        KeyPieceLocation location = file.getKeyPieces().get(0);
        assertEquals(location.getURL(), locations.get(0).getURL());
        assertEquals(location.getType(), locations.get(0).getType());
        assertEquals(location.getVersion(), locations.get(0).getVersion());
        file.setKeyPieces(null);
        assertNull(file.getKeyPieces());

    }

    @Test
    public void testACLs() {
        MetaFile file = new MetaFileImpl();
        assertNull(file.getACL());
        file.setACL(null);

        List<ACLItem> locations = new ArrayList<ACLItem>();
        file.setACL(locations);
        assertEquals(file.getACL().size(), 0);
        locations.add(new ACLItem("john", true, false));
        assertEquals(file.getACL().size(), 0);
        file.setACL(locations);
        assertEquals(file.getACL().size(), 1);
        ACLItem location = file.getACL().get(0);
        assertEquals(location.getUser(), locations.get(0).getUser());
        assertEquals(location.isRead(), locations.get(0).isRead());
        assertEquals(location.isWrite(), locations.get(0).isWrite());
        file.setACL(null);
        assertNull(file.getACL());
        boolean exception = false;
        try{
        file.addACLItem(null);
        } catch (IllegalArgumentException e){
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try{
        file.removeACLItem(0);
        } catch (IllegalArgumentException e){
            exception = true;
        }
        assertTrue(exception);
        file.addACLItem(new ACLItem("john", true, false));
        exception = false;
        try{
        file.removeACLItem(10);
        } catch (IllegalArgumentException e){
            exception = true;
        }
        assertTrue(exception);
        file.removeACLItem(0);
        assertEquals(file.getACL().size(), 0);
    }

    @Test
    public void testRestricted() {
        MetaFileImpl file = new MetaFileImpl();
        assertFalse(file.isRestricted());
        file.setRestricted(true);
        assertTrue(file.isRestricted());
        file.setRestricted(false);
        assertFalse(file.isRestricted());
    }
}
