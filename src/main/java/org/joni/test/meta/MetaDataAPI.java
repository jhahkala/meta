package org.joni.test.meta;

import java.io.IOException;
import java.util.List;

import com.eaio.uuid.UUID;

/**
 * The metadata interface, used to communicate the metadata between the client and server.
 * 
 * @author hahkala
 * 
 */
public interface MetaDataAPI {
    /**
     * Puts a file to the parent directory.
     * 
     * @param parent The directory to add the file to. IOException is thrown if the parent is not a directory.
     * @param newFile The new file to put into the parent directory.
     * @throws IOException is thrown if the file already exists, user has no rights to write to the directory or if the
     *             user has no more space to store the file.
     */
    public void putFile(MetaFile newFile) throws IOException;

    /**
     * Updates the file updatedFile with the new information.
     * 
     * @param updatedFile the file to update.
     * @throws IOException thrown in case the user has no write permission to the file.
     */
    public void updateFile(MetaFile updatedFile) throws IOException;

    /**
     * Deletes the file from metadata catalog.
     * 
     * @param parent the directory to remove the file from.
     * @param deleteFile the file to remove, also sub-directories and files are removed.
     */
    public void deleteFile(UUID id) throws IOException;

    /**
     * Returns the metadata of requested file.
     * 
     * @param id The id of the file to return.
     * @return the metadata of the requested file.
     * @throws IOException thrown in case the file doesn't exist or the user has no rights to access the file.
     */
    public MetaFile getFile(UUID id) throws IOException;

    /**
     * Returns the metadata of requested file.
     * 
     * @param id The id of the file to return.
     * @return the metadata of the requested file.
     * @throws IOException thrown in case the file doesn't exist or the user has no rights to access the file.
     */
    public MetaFile getFileByPath(String path) throws IOException;   
    
    /**
     * Optimization method, gets a file metadata and metadata of subfiles in case the file is a directory.
     * 
     * @param id The file or directory to return.
     * @return The list starting with the requested file and followed by the metadata of all files the directory
     *         contains in case the file si a directory.
     * @throws IOException in case the file doesn't exist, or the user has no access to read it.
     */
    public List<MetaFile> getListFile(UUID id) throws IOException;

    
    /**
     * Get the user's information.
     * @return The information of the user.
     */
    public UserInfo getUserInfo();
    
    /**
     * Get the given user's information. Superuser can use this method to access the information of the given user.
     * @return The information of the user.
     * @throws SessionException 
     */
    public UserInfo getOtherUserInfo(String name) throws SessionException;
    
    /**
     * Superuser can use this method to add an user to the system.
     * @return The information of the user.
     * @throws IOException in case user already exists or there is some other error.
     */
    public void addUser(UserInfo userInfo) throws IOException;
    /**
     * Used to update the user information.
     * @return The information of the user.
     * @throws IOException 
     */
    public void updateUserInfo(UserInfo userInfo) throws IOException;
    
    /**
     * Returns the version of the service.
     * 
     * @return The string representation of the service version. e.g. "2.1.5"
     */
    public String getVersion();
}
