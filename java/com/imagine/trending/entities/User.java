package com.imagine.trending.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander
 */
@Entity
@Table(name = "USERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull
    @Size(min = 1, max = 100)
    private String username;
    @NotNull
    @Size(min = 1, max = 100)
    private String password;
    private Set<Discussion> d;
    private Set<Double> setMap,setM;
    private List<Message> msgs;
    
    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        d = new HashSet();
        setMap = new HashSet();
        setM = new HashSet();
        msgs = new ArrayList(); 
    }
    @Id
    @Basic(optional = false)
    @Column(name = "USERNAME")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic(optional = false)
    @Column(name = "PASSWORD")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany
    public Set<Discussion> getD() {
        return d;
    }

    public void setD(Set<Discussion> d) {
        this.d = d;
    }
    @ElementCollection
    public Set<Double> getSetMap() {
        return setMap;
    }

    public void setSetMap(Set setMap) {
        this.setMap = setMap;
    }
    @ElementCollection
    public Set<Double> getSetM() {
        return setM;
    }

    public void setSetM(Set setM) {
        this.setM = setM;
    }
    
    @OneToMany(cascade=ALL)
    public List<Message> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<Message> msgs) {
        this.msgs = msgs;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return username;
    }
    
}
