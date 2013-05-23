package org.joni.test.meta;

import java.io.Serializable;
import java.net.URL;

/**
 * The immutable key piece object.
 * 
 * @author hahkala
 * 
 */
public class KeyPieceLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    private URL _url = null;
    private String _type = null;
    private String _version = null;

    public KeyPieceLocation(URL url, String type, String version) {
        _url = url;
        _type = type;
        _version = version;
    }

    public URL getURL() {
        return _url;
    }

    public String getType() {
        return _type;
    }

    public String getVersion() {
        return _version;
    }

}
