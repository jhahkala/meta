package org.joni.test.meta;

import java.io.Serializable;

/**
 * Placeholder for real SLA implementation.
 * 
 * @author hahkala
 *
 */
public class SLA implements Serializable {

    private static final long serialVersionUID = 2667041556243774173L;
    private String _sla;
    
    public SLA(String sla){
        _sla  = sla;
    }

    public String getSLA(){
        return _sla;
    }
    
    public void setSLA(String sla){
        _sla = sla;
    }
}
