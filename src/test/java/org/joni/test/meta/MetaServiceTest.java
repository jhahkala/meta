package org.joni.test.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.bouncycastle.crypto.CryptoException;
import org.glite.security.trustmanager.ContextWrapper;
import org.joni.test.meta.server.MetaServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.TMHessianURLConnectionFactory;
import com.eaio.uuid.UUID;

import fi.hip.sicx.srp.HandshakeException;
import fi.hip.sicx.srp.SRPAPI;
import fi.hip.sicx.srp.SRPClient;
import fi.hip.sicx.srp.SRPUtil;
import fi.hip.sicx.srp.SessionKey;
import fi.hip.sicx.srp.SessionToken;
import fi.hip.sicx.srp.hessian.HessianSRPProxy;
import fi.hip.sicx.srp.hessian.HessianSRPProxyFactory;
import fi.hip.sicx.srp.hessian.TMHostnameVerifier;

public class MetaServiceTest {

    public static final String TEST_USER = "USerNAmssfedfs";
    public static final String TEST_USER_PW = "PassWordaa";
    public static final String TEST_USER2 = "2userNNAamee";
    public static final String TEST_USER2_PW = "passslkjlkj";
    public static final String TRUSTED_CLIENT_CONFIG_FILE = "src/test/meta-client-trusted.conf";
    public static final String TRUSTED_CLIENT2_CONFIG_FILE = "src/test/meta-client2-trusted.conf";
    public static final String SERVER_PURGE_CONFIG_FILE = "src/test/meta-purge.conf";

    private static MetaServer server;

    @After
    public void endserver() throws Exception {
        System.out.println("****Stop");
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Before
    public void setupServer() throws Exception {
        System.out.println("Starting server....");
        server = new MetaServer();
        server.configure(SERVER_PURGE_CONFIG_FILE);
        server.start();

    }

    public MetaDataAPI login(String username, String passwordString, boolean addUser) throws FileNotFoundException, IOException,
            GeneralSecurityException, CryptoException, HandshakeException {
        // client
        HessianSRPProxyFactory factory = HessianSRPProxyFactory.getFactory(TRUSTED_CLIENT_CONFIG_FILE);
        String srpUrl = "https://localhost:40666/SRPService";
        SRPAPI srpService = (SRPAPI) factory.create(SRPAPI.class, srpUrl);

        SRPClient.putVerifier(srpService, username, passwordString);

        byte identity[] = SRPUtil.stringBytes(username);
        byte password[] = SRPUtil.stringBytes(passwordString);

        SessionKey key = SRPClient.login(srpService, identity, password);

        String url = "https://localhost:40666/MetaService";
        MetaDataAPI service = (MetaDataAPI) factory.create(MetaDataAPI.class, url);
        HessianSRPProxy proxy = (HessianSRPProxy) Proxy.getInvocationHandler(service);
        proxy.setSession(new SessionToken(identity, key.getK()).toString());

        if (addUser) {
            UserInfo info = new UserInfo();
            info.setName(username);
            service.addUser(info);
        }

        return service;

    }

    /**
     * @param args
     * @throws Exception
     */
    @Test
    public void testFilePut() throws Exception {
        // client
        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        boolean exception = false;
        try {
            service.putFile(null);
        } catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFile subdir = new MetaFileImpl();
        subdir.setDirectory(true);
        subdir.setName("subdir");
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        subdir.addACLItem(new ACLItem(TEST_USER2, true, true));
        service.putFile(subdir);

        exception = false;
        try {
            service.putFile(root);
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.putFile(new MetaFileImpl().setName("Test").setParent(new UUID()));
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        List<ACLItem> acl = new ArrayList<ACLItem>();
        acl.add(new ACLItem("peter", true, true));
        MetaFile testRootWithoutAccess = new MetaFileImpl().setName("TestRootNoPermissiontowrite");
        testRootWithoutAccess.setACL(acl);
        service.putFile(testRootWithoutAccess);
        exception = false;
        try {
            service.putFile(new MetaFileImpl().setName("Test").setParent(testRootWithoutAccess.getId()));
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        // client
        MetaDataAPI service2 = login(TEST_USER2, TEST_USER2_PW, true);

        MetaFile subdirFail = new MetaFileImpl();
        subdirFail.setDirectory(true);
        subdirFail.setName("subdirfail");
        subdirFail.addACLItem(new ACLItem(TEST_USER2, true, true));
        exception = false;
        service2.putFile(subdirFail);
        service2.deleteFile(subdirFail.getId());

        subdirFail.setParent(root.getId());
        exception = false;
        try { // fail adding subdir to root no access superuser
            service2.putFile(subdirFail);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);

        // subdirFail.setParent(subdir.getId());
        // service2.putFile(subdirFail);

    }

    @Test
    public void testFileUpdate() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setName("subdir");
        subdir.setDirectory(true);
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subdir);

        MetaFileImpl rootfile = new MetaFileImpl();
        rootfile.setName("rootfile");
        rootfile.setParent(root.getId());
        rootfile.addACLItem(new ACLItem(TEST_USER, false, false));
        service.putFile(rootfile);

        MetaFileImpl subfile = new MetaFileImpl();
        subfile.setName("subfile");
        subfile.setParent(subdir.getId());
        subfile.addACLItem(new ACLItem(TEST_USER, true, false));
        service.putFile(subfile);

        List<MetaFile> files = new ArrayList<MetaFile>();
        files = service.getListFile(root.getId());
        for (MetaFile file : files) {
            System.out.println("ls root: " + file);
        }

        boolean exception = false;
        try {
            service.updateFile(null);
        } catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.updateFile(subdir.setParent(null));
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.updateFile(new MetaFileImpl());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.updateFile(subfile.setParent(root.getId()));

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.updateFile(subfile.setParent(new UUID()));

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        // service.updateFile(root.removeACLItem(0).addACLItem(new ACLItem(TEST_USER, false, false)));
        // exception = false;
        // try {
        // service.updateFile(subdir.setName("subdirv2").setParent(root.getId()));
        //
        // } catch (IOException e) {
        // System.out.println(e.getMessage());
        // exception = true;
        // }
        // assertTrue(exception);

        // exception = false;
        // try {
        // service.updateFile(root.setName("rootv2"));
        //
        // } catch (IOException e) {
        // exception = true;
        // }
        // assertTrue(exception);

        // root = new MetaFileImpl();
        // root.setDirectory(true);
        // root.setName("root");
        // root.addACLItem(new ACLItem(TEST_USER, true, true));
        // service.putFile(root);
        //
        // subdir = new MetaFileImpl();
        // subdir.setName("subdir");
        // subdir.setDirectory(true);
        // subdir.setParent(root.getId());
        // subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        // service.putFile(subdir);
        //
        // MetaFile subsubdir = new MetaFileImpl();
        // subsubdir.setName("subsubdir");
        // subsubdir.setDirectory(true);
        // subsubdir.setParent(subdir.getId());
        // subsubdir.addACLItem(new ACLItem(TEST_USER, true, true));
        // service.putFile(subsubdir);
        // service.updateFile(subdir.setName("newsub"));
        //
        // service.deleteFile(subdir.getId());
        // exception = false;
        // try {
        // service.updateFile(subsubdir.setName("newsub"));
        //
        // } catch (IOException e) {
        // exception = true;
        // }
        // assertTrue(exception);

    }

    @Test
    public void testDelete() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        boolean exception = false;
        try {
            service.deleteFile(null);

        } catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.deleteFile(new UUID());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        // add test files
        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setName("subdir");
        subdir.setDirectory(true);
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subdir);

        MetaFile subsubdir = new MetaFileImpl();
        subsubdir.setName("subsubdir");
        subsubdir.setDirectory(true);
        subsubdir.setParent(subdir.getId());
        subsubdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subsubdir);

        // check that nonexistent file delete fails also when some files exists
        exception = false;
        try {
            service.deleteFile(new UUID());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try { // can't delete sub as it's not empty
            service.deleteFile(subdir.getId());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        service.deleteFile(subsubdir.getId());
        service.putFile(subsubdir);
        service.deleteFile(subsubdir.getId());
        service.putFile(subsubdir);

        subdir = (MetaFileImpl) service.getFile(subdir.getId());
        subdir.removeACLItem(0);
        subdir.addACLItem(new ACLItem(TEST_USER, true, false));
        service.updateFile(subdir);
        System.out.println(2);
        exception = false;
        try { // no rights to delete subsubdir
            service.deleteFile(subsubdir.getId());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);
        subdir.removeACLItem(0);
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.updateFile(subdir);
        service.deleteFile(subsubdir.getId());
        service.deleteFile(subdir.getId());
        service.deleteFile(root.getId());

    }

    @Test
    public void testGetByPath() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        boolean exception = false;
        try {
            service.getFileByPath((String) null);
        } catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.getFileByPath(new String());
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        String drootdir = "root", dsubdir = "/root/subdir", dsubsubdir = "/root/subdir/subdir";

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName(drootdir);
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setDirectory(true);
        subdir.setName("subdir");
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subdir);

        MetaFile subsubdir = new MetaFileImpl();
        subsubdir.setName("subsubdir");
        subsubdir.setDirectory(true);
        subsubdir.setParent(subdir.getId());
        subsubdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subsubdir);

        // Root has to be added before adding to service
        UserInfo info = new UserInfo();
        info.setName(TEST_USER);
        List<UUID> roots = new ArrayList<UUID>();
        roots.add(root.getId());
        info.setRoots(roots);
        assertTrue(info.getRoots().size() == 1);
        service.updateUserInfo(info);

        // check that nonexistent file delete fails also when some files exists
        exception = false;
        try {
            service.getFileByPath(new String());
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);
        String idOrig = dsubdir;
        service.getFileByPath(idOrig);

        assertEquals(service.getFileByPath(idOrig).getId(), subdir.getId());
        assertEquals(service.getFileByPath(idOrig).getName(), subdir.getName());

        // Check that "/" returns null
        assertTrue(service.getFileByPath("/") == null);

        service.getFileByPath(idOrig);
        subdir = (MetaFileImpl) service.getFile(subdir.getId());
        subdir.removeACLItem(0);
        subdir.addACLItem(new ACLItem(TEST_USER, false, true));
        service.updateFile(subdir);
        exception = false;
        try {
            service.getFileByPath(dsubsubdir);
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);
        MetaFile testsubdir = service.getFileByPath(idOrig);
        assertTrue(testsubdir != null);
        assertTrue(testsubdir.listFiles() != null);
        root = service.getFile(root.getId());
        root.removeACLItem(0);
        root.addACLItem(new ACLItem(TEST_USER, false, true));
        service.updateFile(root);
        exception = false;
        try {
            service.getFileByPath(drootdir);
        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

    }

    @Test
    public void testGet() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        boolean exception = false;
        try {
            service.getFile((UUID) null);

        } catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.getFile(new UUID());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setName("subdir");
        subdir.setDirectory(true);
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subdir);

        MetaFile subsubdir = new MetaFileImpl();
        subsubdir.setName("subsubdir");
        subsubdir.setDirectory(true);
        subsubdir.setParent(subdir.getId());
        subsubdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subsubdir);

        // check that nonexistent file delete fails also when some files exists
        exception = false;
        try {
            service.getFile(new UUID());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);
        UUID idOrig = subdir.getId();
        UUID id = service.getFile(subdir.getId()).getId();
        assertEquals(id, idOrig);

        assertEquals(service.getFile(subdir.getId()).getId(), subdir.getId());
        assertEquals(service.getFile(subdir.getId()).getName(), subdir.getName());

        subdir = (MetaFileImpl) service.getFile(subdir.getId());

        subdir.removeACLItem(0);
        subdir.addACLItem(new ACLItem(TEST_USER, false, true));
        service.updateFile(subdir);
        exception = false;
        try {
            service.getFile(subsubdir.getId());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);
        MetaFile testsubdir = service.getFile(subdir.getId());
        assertTrue(testsubdir != null);
        assertTrue(testsubdir.listFiles() == null);
        root = service.getFile(root.getId());
        root.removeACLItem(0);
        root.addACLItem(new ACLItem(TEST_USER, false, true));
        service.updateFile(root);
        exception = false;
        try {
            service.getFile(root.getId());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

    }

    @Test
    public void testGetListFile() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        boolean exception = false;
        try {
            service.getListFile(null);

        } catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

        exception = false;
        try {
            service.getListFile(new UUID());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setName("subdir");
        subdir.setDirectory(true);
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subdir);

        MetaFile subsubdir = new MetaFileImpl();
        subsubdir.setName("subsubdir");
        subsubdir.setDirectory(true);
        subsubdir.setParent(subdir.getId());
        subsubdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subsubdir);

        MetaFileImpl rootfile = new MetaFileImpl();
        rootfile.setName("rootfile");
        rootfile.setParent(root.getId());
        rootfile.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(rootfile);

        // check that nonexistent file delete fails also when some files exists
        exception = false;
        try {
            service.getListFile(new UUID());

        } catch (IOException e) {
            exception = true;
        }
        assertTrue(exception);

        assertEquals(service.getListFile(subdir.getId()).get(0).getId(), subdir.getId());
        assertEquals(service.getListFile(subdir.getId()).get(0).getName(), subdir.getName());
        assertEquals(service.getListFile(subdir.getId()).get(1).getId(), subsubdir.getId());
        assertEquals(service.getListFile(subdir.getId()).get(1).getName(), subsubdir.getName());

        service.getListFile(subsubdir.getId());
        subdir = (MetaFileImpl) service.getFile(subdir.getId());
        subdir.removeACLItem(0);
        subdir.addACLItem(new ACLItem(TEST_USER, false, true));
        service.updateFile(subdir);
        exception = false;
        try {
            service.getListFile(subsubdir.getId());

        } catch (IOException e) {
            exception = true;
        }
        // superuser has access, need to test with another user...
        // assertTrue(exception);
        List<MetaFile> testsubdirList = service.getListFile(subdir.getId());
        assertTrue(testsubdirList != null);
        assertTrue(testsubdirList.get(0).listFiles() == null);

        List<MetaFile> testrootfileList = service.getListFile(rootfile.getId());
        assertEquals(testrootfileList.size(), 1);
        assertEquals(testrootfileList.get(0).getId(), rootfile.getId());
        assertEquals(testrootfileList.get(0).getName(), rootfile.getName());

        root = service.getListFile(root.getId()).get(0);
        root.removeACLItem(0);
        root.addACLItem(new ACLItem(TEST_USER, false, true));
        service.updateFile(root);
        exception = false;
        try {
            service.getListFile(root.getId());

        } catch (IOException e) {
            exception = true;
        }
        // superuser has access, need to test with another user...
        // assertTrue(exception);
    }

    // @Test
    public void notestStress() throws Exception {

        // client
        File configFile = new File(TRUSTED_CLIENT_CONFIG_FILE);
        Properties props = new Properties();
        props.load(new FileReader(configFile));
        ContextWrapper wrapper = new ContextWrapper(props, false);

        TMHostnameVerifier verifier = new TMHostnameVerifier();

        String url = "https://sicx1.hip.helsinki.fi:40666/MetaService";
        HessianProxyFactory factory = new HessianProxyFactory();
        TMHessianURLConnectionFactory connectionFactory = new TMHessianURLConnectionFactory();
        connectionFactory.setWrapper(wrapper);
        connectionFactory.setVerifier(verifier);
        connectionFactory.setHessianProxyFactory(factory);
        factory.setConnectionFactory(connectionFactory);
        MetaDataAPI service = (MetaDataAPI) factory.create(MetaDataAPI.class, url);

        UserInfo info = new UserInfo();
        info.setName(TEST_USER);
        service.addUser(info);

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);

        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setName("subdir");
        subdir.setDirectory(true);
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(subdir);

        MetaFileImpl rootfile = new MetaFileImpl();
        rootfile.setName("rootfile");
        rootfile.setParent(root.getId());
        rootfile.addACLItem(new ACLItem(TEST_USER, false, false));
        service.putFile(rootfile);

        MetaFileImpl subfile = new MetaFileImpl();
        subfile.setName("rootfile");
        subfile.setParent(subdir.getId());
        subfile.addACLItem(new ACLItem(TEST_USER, true, false));
        service.putFile(subfile);

        List<MetaFile> files = new ArrayList<MetaFile>();
        Date start = new Date();
        int i;
        for (i = 0; i < 1; i++) {
            files = service.getListFile(root.getId());
        }
        Date stop = new Date();
        for (MetaFile file : files) {
            System.out.println("ls stress root: " + file);
        }
        System.out.println("time for " + i + " rounds of ls : " + (stop.getTime() - start.getTime()));

    }

    // @Test
    public void testFile() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, true);

        MetaFile root = new MetaFileImpl();
        root.setDirectory(true);
        root.setName("root");
        root.addACLItem(new ACLItem(TEST_USER, true, true));
        service.putFile(root);
        MetaFile root2 = service.getFile(root.getId());
        System.out.println("Got: " + root2);
        // ls root
        List<MetaFile> files = service.getListFile(root.getId());
        System.out.println("ls of root: ");
        for (MetaFile file : files) {
            System.out.println("ls root: " + file);
        }
        MetaFileImpl subdir = new MetaFileImpl();
        subdir.setName("subdir");
        subdir.setDirectory(true);
        subdir.setParent(root.getId());
        subdir.addACLItem(new ACLItem(TEST_USER, true, true));
        System.out.println("Saving: " + subdir);
        service.putFile(subdir);
        System.out.println("updating: " + root);
        // ls root
        System.out.println("ls of root: ");
        files = service.getListFile(root.getId());
        for (MetaFile file : files) {
            System.out.println("ls root: " + file);
        }
        MetaFileImpl rootfile = new MetaFileImpl();
        rootfile.setName("rootfile");
        rootfile.setParent(root.getId());
        rootfile.addACLItem(new ACLItem(TEST_USER, false, false));
        service.putFile(rootfile);

        MetaFileImpl subfile = new MetaFileImpl();
        subfile.setName("rootfile");
        subfile.setParent(subdir.getId());
        subfile.addACLItem(new ACLItem(TEST_USER, true, false));
        service.putFile(subfile);
        files = service.getListFile(root.getId());
        for (MetaFile file : files) {
            System.out.println("ls root: " + file);
        }
        files = service.getListFile(subdir.getId());
        for (MetaFile file : files) {
            System.out.println("ls subdir: " + file);
        }
    }

    @Test
    public void testGetUserArg() throws Exception {

        MetaDataAPI service = login(TEST_USER, TEST_USER_PW, false);

        UserInfo info = new UserInfo();
        info.setName(TEST_USER);

        String rootName = "testingRoot";
        MetaFile root = null;
        if (rootName != null) {
            root = new MetaFileImpl();
            root.setDirectory(true);
            root.setName(rootName);
            root.addACLItem(new ACLItem(TEST_USER, true, true));
            List<UUID> roots = new ArrayList<UUID>();
            roots.add(root.getId());
            info.setRoots(roots);
        }

        service.addUser(info);

        UserInfo testUser = service.getOtherUserInfo(TEST_USER2);

        assertNull(testUser);

        UserInfo info2 = new UserInfo();
        info2.setName(TEST_USER2);

        String rootName2 = "testingRoot2";
        MetaFile root2 = null;
        if (rootName2 != null) {
            root2 = new MetaFileImpl();
            root2.setDirectory(true);
            root2.setName(rootName2);
            root2.addACLItem(new ACLItem(TEST_USER, true, true));
            List<UUID> roots = new ArrayList<UUID>();
            roots.add(root2.getId());
            info2.setRoots(roots);
        }

        service.addUser(info2);

        assertNull(service.getOtherUserInfo(TEST_USER2));
        
        service = login(TEST_USER2, TEST_USER2_PW, false);

        assertNotNull(service.getOtherUserInfo(TEST_USER));
        @SuppressWarnings("unused")
        UserInfo user3 = service.getUserInfo();

    }

}
