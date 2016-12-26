package com.imagine.trending.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "DISCUSSION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Discussion.findAll", query = "SELECT d FROM Discussion d"),
    @NamedQuery(name = "Discussion.findByName", query = "SELECT d FROM Discussion d WHERE d.name = :name"),
    @NamedQuery(name = "Discussion.findByUser", query = "SELECT d FROM Discussion d WHERE d.u = :u")})
public class Discussion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    private int count;
    @ManyToMany(mappedBy = "d",cascade={PERSIST,MERGE,REFRESH})
    private Set<User> u;
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private List<String> activities;
    @Lob
    private String about;
    private String creator;
    @OneToMany(cascade=ALL)
    private Set<Opinion> opinions;
    @OneToMany(cascade=ALL)
    private Set<Media> media;
    private transient String ledit;
    
    public Discussion() {
    }

    public Discussion(String name) {
        this.about = "";
        this.name = name;
        u = new HashSet();
        activities = new ArrayList();
        opinions = new HashSet();
        media = new HashSet();
        ledit = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    public Set<User> getU() {
        return u;
    }

    public void setU(Set<User> u) {
        this.u = u;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public Set<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(Set<Opinion> opinions) {
        this.opinions = opinions;
    }

    public Set<Media> getMedia() {
        return media;
    }

    public void setMedia(Set<Media> media) {
        this.media = media;
    }

    public String getLedit() {
        return ledit;
    }

    public void setLedit(String ledit) {
        this.ledit = ledit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Discussion)) {
            return false;
        }
        Discussion other = (Discussion) object;
        return this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return name ;
    }
}
