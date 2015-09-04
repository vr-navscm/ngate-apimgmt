/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserEntity {

	/**
	 * The user first name
	 */
	private String firstname;
	
	/**
	 * The user last name
	 */
	private String lastname;
	
    /**
     * The user name
     */
    private String username;
    
    /**
     * The user password
     */
    private String password;

    /**
     * The user email
     */
    private String mail;
    
    /**
     * The user roles
     */
    private List<String> roles;
    

    /**
     * The user creation date
     */
    @JsonProperty("created_at")
    private Date createdAt;

    /**
     * The user last updated date
     */
    @JsonProperty("updated_at")
    private Date updatedAt;
    
    public String getFirstname() {
  		return firstname;
  	}

  	public void setFirstname(String firstname) {
  		this.firstname = firstname;
  	}

  	public String getLastname() {
  		return lastname;
  	}

  	public void setLastname(String lastname) {
  		this.lastname = lastname;
  	}

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserEntity{");
        sb.append("firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", mail='").append(mail).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", roles='").append(roles).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
