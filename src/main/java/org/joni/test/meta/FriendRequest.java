package org.joni.test.meta;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FriendRequest  implements Serializable {
    private static final long serialVersionUID = 23452352345L;
    private String _name;
    private Calendar _date = new GregorianCalendar();
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	/**
	 * @return the _date
	 */
	public Calendar getDate() {
		return _date;
	}
}
