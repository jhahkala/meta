package org.joni.test.meta;

import java.io.Serializable;

/**
 * An immutable access control definition for one user.
 * 
 * @author hahkala
 * 
 */
public class ACLItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String _user = null;
    private boolean _read = false;
    private boolean _write = false;

    public ACLItem(String user, boolean read, boolean write) {
        _user = user;
        _read = read;
        _write = write;
    }

    public String getUser() {
        return _user;
    }

    public boolean isRead() {
        return _read;
    }

    public boolean isWrite() {
        return _write;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_read ? 1231 : 1237);
        result = prime * result + ((_user == null) ? 0 : _user.hashCode());
        result = prime * result + (_write ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        ACLItem other = (ACLItem) obj;
        if (_read != other._read){
            return false;
        }
        if (_user == null) {
            if (other._user != null){
                return false;
            }
        } else if (!_user.equals(other._user)){
            return false;
        }
        if (_write != other._write){
            return false;
        }
        return true;
    }
    
    
}
