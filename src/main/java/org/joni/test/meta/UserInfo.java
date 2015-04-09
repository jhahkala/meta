package org.joni.test.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.eaio.uuid.UUID;

/**
 * 
 * 
 * @author J
 *
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String _name;
    private List<UUID> _roots = new ArrayList<UUID>();
    private List<String> _friends = new ArrayList<String>();
    private List<FriendRequest> _requests = new ArrayList<FriendRequest>();
    private boolean _private = true;
    private boolean _acceptRequests = true;
    private String encPrivKey;
    private String pubKey;
    
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
        return "UserInfo: " + _name;
    }
    
	public List<String> getFriends() {
		return _friends;
	}
	
	public void setFriends(List<String> friends) {
		this._friends = friends;
	}
	
	public List<FriendRequest> getRequests() {
		return _requests;
	}
	
	public void setRequests(List<FriendRequest> requests) {
		this._requests = requests;
	}
	
	/**
	 * @return the _private
	 */
	public boolean isPrivate() {
		return _private;
	}
	
	/**
	 * @param _private the _private to set
	 */
	public void setPrivate(boolean _private) {
		this._private = _private;
	}
	
	/**
	 * @return the _noRequests
	 */
	public boolean isAcceptRequests() {
		return _acceptRequests;
	}
	
	/**
	 * @param _noRequests the _noRequests to set
	 */
	public void setAcceptRequests(boolean _noRequests) {
		this._acceptRequests = _noRequests;
	}
	/**
	 * @return the encPrivKey
	 */
	public String getEncPrivKey() {
		return encPrivKey;
	}
	/**
	 * @param encPrivKey the encPrivKey to set
	 */
	public void setEncPrivKey(String encPrivKey) {
		this.encPrivKey = encPrivKey;
	}
	/**
	 * @return the pubKey
	 */
	public String getPubKey() {
		return pubKey;
	}
	/**
	 * @param pubKey the pubKey to set
	 */
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
    
}
