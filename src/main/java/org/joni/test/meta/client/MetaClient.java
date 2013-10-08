package org.joni.test.meta.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.glite.security.trustmanager.ContextWrapper;
import org.joni.test.meta.ACLItem;
import org.joni.test.meta.MetaDataAPI;
import org.joni.test.meta.MetaFile;
import org.joni.test.meta.MetaFileImpl;
import org.joni.test.meta.SLA;
import org.joni.test.meta.UserInfo;

import com.beust.jcommander.JCommander;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianSRPProxy;
import com.caucho.hessian.client.HessianSRPProxyFactory;
import com.caucho.hessian.client.TMHessianURLConnectionFactory;
import com.eaio.uuid.UUID;


/**
 * @author hahkala
 * 
 */
public class MetaClient {
    
    public static final String ENDPOINT_OPT = "metaService";

    /**
     * @param args
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws GeneralSecurityException 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, GeneralSecurityException {
        CommandMain cm = new CommandMain();
        JCommander jc = new JCommander(cm);

        CommandAddUser add = new CommandAddUser();
        CommandList list = new CommandList();
        jc.addCommand("addUser", add);
        jc.addCommand("list", list);
        jc.parse(args);

        if(cm.configFile == null){
            System.out.println("Command missing mandatory configuration file parameter.");
            jc.usage();
            System.exit(1);
        }
        // client
        File configFile = new File(cm.configFile);
        if(!configFile.exists()){
            System.out.println("Configuration file " + cm.configFile + " does not exist.");
            System.exit(2);
        }
        
        Properties props = new Properties();
        props.load(new FileReader(configFile));
        ContextWrapper wrapper = new ContextWrapper(props, false);
        
        TMHostnameVerifier verifier = new TMHostnameVerifier();         
        
        String url = props.getProperty(ENDPOINT_OPT, "https://localhost:40669/MetaService");
        HessianSRPProxyFactory factory = new HessianSRPProxyFactory();
        TMHessianURLConnectionFactory connectionFactory = new TMHessianURLConnectionFactory();
        connectionFactory.setWrapper(wrapper);
        connectionFactory.setVerifier(verifier);
        connectionFactory.setHessianProxyFactory(factory);
        factory.setConnectionFactory(connectionFactory);
        MetaDataAPI service = (MetaDataAPI) factory.create(MetaDataAPI.class, url);
        HessianSRPProxy proxy = (HessianSRPProxy) Proxy.getInvocationHandler(service);
        
        proxy.setSession("srpTest");
 
        
        if(cm.verbose){
            System.out.println("Server version: " + service.getVersion());
        }
        
        if(jc.getParsedCommand().equals("addUser")){
            addUser(add, service);            
        }
        
        if(jc.getParsedCommand().equals("list")){
            list(list, service);            
        }
        
    }
    
    public static void addUser(CommandAddUser add, MetaDataAPI service) throws IOException{
        String userName = add.name;
        if(userName == null){
            System.out.println("Error: No username given.");
            System.exit(3);
        }
        UserInfo info = new UserInfo();
        info.setName(add.name);
        
        String rootName = add.root;
        MetaFile root = null;
        if(rootName != null){
            root = new MetaFileImpl();
            root.setDirectory(true);
            root.setName(add.root);
            root.addACLItem(new ACLItem(add.name, true, true));
            List<UUID> roots = new ArrayList();
            roots.add(root.getId());
            info.setRoots(roots);
            if(add.sla != null){
                root.setSLA(new SLA(add.sla));
            }
        }
        
        service.addUser(info);
        System.out.println("Added user: " + info);
        if(root != null){
            service.putFile(root);
            System.out.println("Added root: " + root);
        }
    }

    public static void list(CommandList listCommand, MetaDataAPI service) throws IOException{
        String root = listCommand.root;
        if(root == null){
            root = "/";
        }
        
        UserInfo user;
        String name = listCommand.userName;
        if (name == null){
            user = service.getUserInfo();
        } else {
            user = service.getOtherUserInfo(name);
        }
        //TODO implement root search
        //String path[] = root.split("/");        
        
        if(user != null){
            if (user.getRoots() != null){
                for(UUID id:user.getRoots()){
                    listFiles(0, id, listCommand.recursive, service);
                }
            }
        }
        
    }
    
    public static void listFiles(int depth, UUID id, boolean recursive, MetaDataAPI service) throws IOException{
        List<MetaFile> files = service.getListFile(id);
        if(files == null || files.size() == 0){
            return;
        }
        
        MetaFile main = files.get(0);
        
        print(main, depth);
        
        files.remove(0);
        
        for(MetaFile curr: files){
            if(curr.isDirectory()){
                listFiles(depth + 1, curr.getId(), recursive, service);
            } else {
                print(curr, depth+1);
            }
        }
        
        
    }
    
    public static void print(MetaFile file, int depth){
        for(int i = 0; i < depth; i++){
            System.out.print("  ");
        }
        System.out.println(file);
    }

}
