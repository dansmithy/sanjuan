package com.github.dansmithy.sanjuan.model;

import com.github.dansmithy.sanjuan.rest.serialize.Iso8601CustomDateSerializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;


public class User {

    @Indexed
	private String username;
	private Date firstLogin;
    private Date lastLogin;
    private Long timesLoggedIn;

	@Id
	private ObjectId id;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

    @JsonSerialize(using = Iso8601CustomDateSerializer.class)
    public Date getFirstLogin() {
        return copyOf(firstLogin);
    }

    public void setFirstLogin(Date firstLogin) {
        this.firstLogin = firstLogin;
    }

    @JsonSerialize(using = Iso8601CustomDateSerializer.class)
    public Date getLastLogin() {
        return copyOf(lastLogin);
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Long getTimesLoggedIn() {
        return timesLoggedIn;
    }

    public void setTimesLoggedIn(Long timesLoggedIn) {
        this.timesLoggedIn = timesLoggedIn;
    }

    private static Date copyOf(Date date) {
        return date == null ? null : new Date(date.getTime());
    }

}
