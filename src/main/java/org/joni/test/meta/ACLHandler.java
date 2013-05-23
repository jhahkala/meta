package org.joni.test.meta;

import java.util.List;

public class ACLHandler {
    public static boolean hasReadAccess(String user, List<ACLItem> acl) {

        if (acl == null) {
            throw new IllegalArgumentException("Access control list cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        for (ACLItem aclItem : acl) {
            if (user.equals(aclItem.getUser())) {
                if (aclItem.isRead()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasWriteAccess(String user, List<ACLItem> acl) {

        if (acl == null) {
            throw new IllegalArgumentException("Access control list cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        for (ACLItem aclItem : acl) {
            if (user.equals(aclItem.getUser())) {
                if (aclItem.isWrite()) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
