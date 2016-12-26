package com.imagine.trending.beans;


import com.imagine.trending.entities.Discussion;
import com.imagine.trending.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alexander
 */
@Stateless
@Startup
public class JustSessionBean{
    
    @PersistenceContext
    private EntityManager em;
    
    public String checkUsername(String s){
        if(em.find(User.class, s)==null)
            return "";
        return "Username has already been chosen, Try mixing with numbers to create a unique id.";
    }
    
    public String trender(){
        String r="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<Discussions>"; 
        List<Discussion> l = new ArrayList();
        List<Discussion> ld = em.createNamedQuery("Discussion.findAll").getResultList();
        if(ld.size()>0){
            int highest=ld.get(0).getCount();
            for(Discussion dd:ld){
                int e = dd.getCount();
                if(e==0)continue;
                if(e==highest)
                    l.add(dd); 
                else if(e>highest){
                    highest = e;
                    l.clear();
                    l.add(dd);
                }
            }
        }
        for(Discussion d:l){
            if(d.getCount()>1)
                r += "<disc>" + d.getName().toUpperCase()+" is TRENDING with "+d.getCount()+" Opinions </disc>";
            else
                r += "<disc>" + d.getName().toUpperCase()+" is TRENDING with "+d.getCount()+" Opinion </disc>";
        }
        r += "</Discussions>";
        return r;
    }
}
