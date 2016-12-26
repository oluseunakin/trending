package com.imagine.trending.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander
 */
@Entity
@Table(name = "OPINION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Opinion.findAll", query = "SELECT o FROM Opinion o"),
    @NamedQuery(name = "Opinion.findById", query = "SELECT o FROM Opinion o WHERE o.id = :id")})
public class Opinion implements Serializable{
    
    @Lob
    private String opinion;
    private int agree;
    private int disagree;
    @Id
    private double id;
    @OneToMany(fetch=FetchType.LAZY,cascade=ALL)
    private Set<Opinion> comments;
    @OneToMany(cascade=ALL)
    private Set<Media> media;
    @OneToOne
    private User u;
    @ManyToOne
    private Discussion d;

    public Opinion() {
    }

    public Opinion(double i){
        id = i;
        agree = 0;disagree=0;
        comments = new HashSet();
        media = new HashSet();
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public int getDisagree() {
        return disagree;
    }

    public void setDisagree(int disagree) {
        this.disagree = disagree;
    }

    public double getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Opinion> getComments() {
        return comments;
    }

    public void setComments(Set<Opinion> comments) {
        this.comments = comments;
    }

    public Set<Media> getMedia() {
        return media;
    }

    public void setMedia(Set<Media> media) {
        this.media = media;
    }

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public Discussion getD() {
        return d;
    }

    public void setD(Discussion d) {
        this.d = d;
    }

    @Override
    public boolean equals(Object obj) {
        Opinion o = (Opinion)obj;
        return this.id == o.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.id) ^ (Double.doubleToLongBits(this.id) >>> 32));
        return hash;
    }
}
