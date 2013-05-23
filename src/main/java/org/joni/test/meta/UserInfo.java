package org.joni.test.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.eaio.uuid.UUID;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String _name;
    private List<UUID> _roots = new ArrayList<UUID>();
    /**
     * @return the _name
     */
    public String getName() {
        return _name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        _name = name;
    }
    /**
     * @return the _roots
     */
    public List<UUID> getRoots() {
        return _roots;
    }
    /**
     * @param roots the roots to set
     */
    public void setRoots(List<UUID> roots) {
        _roots = roots;
    }
    
    public String toString(){
        return _name;
    }
    
}
