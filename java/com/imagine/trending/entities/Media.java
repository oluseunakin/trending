package com.imagine.trending.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander
 */
@Entity
@Table(name = "MEDIA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Media.findAll", query = "SELECT m FROM Media m"),
    @NamedQuery(name = "Media.findByName", query = "SELECT m FROM Media m WHERE m.name = :name")})
public class Media implements Serializable{
    
    @Id
    private String name;
    private String ct;
    private byte[] m;
    private String caption;

    public Media() {
    }

    public Media(String n){
        name = n;
    }
    
    public Media(String n,String c,byte[] m){
        name = n;
        ct = c;
        this.m = m;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public byte[] getM() {
        return m;
    }

    public void setM(byte[] m) {
        this.m = m;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Media other = (Media) obj;
        return this.hashCode()==other.hashCode();
    }
    
    
}
