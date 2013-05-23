package org.joni.test.meta;

import java.util.ArrayList;
import java.util.List;

public class AccessControlledImpl implements AccessControlled {
    private List<ACLItem> _acl;
    
    /* (non-Javadoc)
     * @see org.joni.test.meta.AccessControlled#getACL()
     */
    @Override
    public List<ACLItem> getACL() {
        if (_acl == null) {
            return null;
        }
        return new ArrayList<ACLItem>(_acl);
    }

    /* (non-Javadoc)
     * @see org.joni.test.meta.AccessControlled#setACL(java.util.List)
     */
    @Override
    public void setACL(List<ACLItem> acl) {
        if (acl == null) {
            _acl = null;
        } else {
            _acl = new ArrayList<ACLItem>(acl);
        }
    }

    /* (non-Javadoc)
     * @see org.joni.test.meta.AccessControlled#addACLItem(org.joni.test.meta.ACLItem)
     */
    @Override
    public void addACLItem(ACLItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Access control definition cannot be null.");
        }
        if (_acl == null) {
            _acl = new ArrayList<ACLItem>();
        }
        _acl.add(item);
    }

    /* (non-Javadoc)
     * @see org.joni.test.meta.AccessControlled#removeACLItem(int)
     */
    @Override
    public void removeACLItem(int index) {
        if(_acl == null){
            throw new IllegalArgumentException("Cannot remove access control definition, list is empty.");
        }
        if (index >= _acl.size()) {
            throw new IllegalArgumentException("ACL is shorter than given index " + index + ".");
        }
        _acl.remove(index);
    }


}
