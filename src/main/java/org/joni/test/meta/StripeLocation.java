package org.joni.test.meta;

import java.io.Serializable;
import java.net.URI;

/**
 * The immutable (not strictly as URI can be changed by java.net.* classes) stripe location class that holds the
 * information needed for obtaining the stripe.
 * 
 * @author hahkala
 * 
 */
public class StripeLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    private URI _uri = null;
    private String _type = null;
    private String _version = null;

    public StripeLocation(URI uri, String type, String version) {
        _uri = uri;
        _type = type;
        _version = version;
    }

    public URI getURI() {
        return _uri;
    }

    public String getType() {
        return _type;
    }

    public String getVersion() {
        return _version;
    }

}
