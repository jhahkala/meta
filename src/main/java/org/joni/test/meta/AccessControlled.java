package org.joni.test.meta;

import java.util.List;

/**
 * Interface of an object with ACL controlling the access.
 * 
 * @author hahkala
 *
 */
public interface AccessControlled {
    /**
     * Lists the access control list (ACL).
     * 
     * @return the ACL.
     */
    public List<ACLItem> getACL();

    /**
     * Sets the access control list (ACL).
     * 
     * @param acl the ACL.
     */
    public void setACL(List<ACLItem> acl);

    /**
     * Adds and access control definition to the access control list (ACL).
     * 
     * @param item the definition to add to the ACL.
     */
    public void addACLItem(ACLItem item);

    /**
     * Removes the access control definition from the access control list (ACL).
     * 
     * @param index The index of the definition to remove from the ACL.
     */
    public void removeACLItem(int index);
    
}
