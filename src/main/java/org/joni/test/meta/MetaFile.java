package org.joni.test.meta;

import java.io.Serializable;
import java.util.List;

import com.eaio.uuid.UUID;

public interface MetaFile extends Comparable<MetaFile>, AccessControlled, Serializable{

    /**
     * Checks whether the file is a directory or not.
     * 
     * @return returns true if the file is a directory.
     */
    public boolean isDirectory();

    /**
     * Set the file to be a directory or not.
     * 
     * @param isDir true if the file is to be a directory.
     */
    public MetaFile setDirectory(boolean isDir);

    /**
     * Get the parent id.
     * 
     * @return the id of the parent directory.
     */
    public UUID getParent();

    /**
     * Set the parent directory id.
     * 
     * @param id The parent directory id.
     */
    public MetaFile setParent(UUID id);

    /**
     * The list of files and directories in this directory.
     * 
     * @return the list of ids id the files in the directory. null if the directory is empty.
     */
    public List<UUID> listFiles();

    /**
     * Adds a file to this directory. The parent of the file has to be the this directory for the addition to succeed.
     * 
     * @param newFile the file to add to this directory.
     */
    public MetaFile addFile(MetaFile newFile);

    /**
     * removes the file with the given id.
     * 
     * @param id the id of the file to remove.
     */
    public MetaFile removeFile(UUID id);

    /**
     * Get the length of the file in bytes.
     * 
     * @return the length of the file in bytes.
     */
    public long getLength();

    /**
     * Sets the length of the file in bytes.
     * 
     * @param length the length of the file in bytes.
     */
    public MetaFile setLength(long length);

    /**
     * Get the id if the file.
     * 
     * @return The id of the file.
     */
    public UUID getId();

    /**
     * Get the name of the file.
     * 
     * @return The name of the file.
     */
    public String getName();

    /**
     * Sets the name of the file.
     * 
     * @param name the name of the file.
     */
    public MetaFile setName(String name);

    /**
     * List the locations of the stripes of this file.
     * 
     * @return the list of locations of the files.
     */
    public List<StripeLocation> getStripes();

    /**
     * Sets the locations of the stripes of the file.
     * 
     * @param locations The list of locations of the stripes of the file.
     */
    public MetaFile setStripes(List<StripeLocation> locations);

    /**
     * List the locations of the stripes of this file.
     * 
     * @return the list of locations of the files.
     */
    public int getMinStripes();

    /**
     * Sets the locations of the stripes of the file.
     * 
     * @param locations The list of locations of the stripes of the file.
     */
    public MetaFile setMinStripes(int minStripes);

    /**
     * List the locations of the key pieces.
     * 
     * @return The list of locations of the key pieces.
     */
    public List<KeyPieceLocation> getKeyPieces();

    /**
     * Sets the locations of the key pieces.
     * 
     * @param locations The list locations of the key pieces.
     */
    public MetaFile setKeyPieces(List<KeyPieceLocation> locations);

    /**
     * Sets the block size of the file, used for striping etc.
     * 
     * @param The file block size in bytes.
     */
    public MetaFile setBlockSize(long size);

    /**
     * Returns the file block size.
     * 
     * @returns the file block size in bytes.
     */
    public long getBlockSize();

    /**
     * Flag for files that the user are allowed to list, but not see the stripe etc info.
     * 
     * @return Whether the access to the file contents are denied.
     */
    public boolean isRestricted();


    /**
     * Returns a restricted view to the file. For example when listing a directory with files you don't have read access
     * to.
     * 
     * @return The file without stripe and key piece locations, block size, and without sub file/directory list in case
     *         of directory.
     */
    public MetaFile restricted();
    
    
    /**
     * Sets the SLA definition for this file.
     * 
     * @param sla The SLA of this file.
     */
    public void setSLA(SLA sla);
    
    /**
     * Gets the SLA definition for this file.
     * 
     * @return The SLA of this file.
     */
    public SLA getSLA();

    /**
     * Sets the combined pad length that was used (crypting and extra).
     * 
     * @param sla The SLA of this file.
     */
    public void setPadLength(long padLen);
    
    /**
     * Gets the combined pad length that was used (crypting and extra).
     * 
     * @return The SLA of this file.
     */
    public long getPadLength();

    /**
     * Sets the extra pad length that was left after crypting pad length.
     * 
     * @param sla The SLA of this file.
     */
    public void setStripePadLength(long stripePadLen);
    
    /**
     * Gets the extra pad length that was left after crypting pad length.
     * 
     * @return The SLA of this file.
     */
    public long getStripePadLength();

    /**
     * Gets the lst modification time for this file.
     * 
     * @return The last modification time.
     */
    public long getLastModified();

    /**
     * Gets the creation time for this file.
     * 
     * @return The creation time.
     */
    public long getCreated();
    
}
