package com.imagine.trending.beans;


import com.imagine.trending.entities.Media;
import com.imagine.trending.entities.Discussion;
import com.imagine.trending.entities.Message;
import com.imagine.trending.entities.Opinion;
import com.imagine.trending.entities.User;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alexander
 */

@Stateful
public class Trending  {
    
    private User u;
    @PersistenceContext
    private EntityManager em;
    private static final Set<User> s = new HashSet();
    
    public String getU() {
        String r = u.getUsername();
        List<Message> l = u.getMsgs();
        int i = 0;
        for(Message st:l){
            if(!st.getReadflag())
                i++;
        }
        if(i==0) return r;
        else{
            r += ":"+i;
            return r;
        }
    }
    
    public String online(String ss){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><d>";
        Discussion di = em.find(Discussion.class, ss);
        Set<User> l = di.getU();
        Set<User> ll = new HashSet();
        ll.addAll(l);
        ll.remove(u);
        for(User uu:ll){
            if(s.contains(uu)){
                r += "<user online='online'>"+uu.getUsername()+"</user>";
            }else
                r += "<user online='offline'>"+uu.getUsername()+"</user>";
        }
        r+="</d>";
        return r;
    }
    
    public String signUp(User uu){ 
        u = uu;
        em.persist(uu);
        s.add(u);
        return "/trending.html";
    }
    
    public String cLogin(User uu){
        u = em.find(User.class, uu.getUsername().toLowerCase());
        if(u != null){
            if(u.getPassword().equals(uu.getPassword()))
                return "";
            else
                return "Username and Password don't match.";
        }else
            return "User doesn't exists, Sign Up instead";
    }
    
    public String login(User uu){
        u = em.find(User.class, uu.getUsername().toLowerCase());
        if(s.add(u))
            return "/trending.html";
        else return "You are logged in already";
    }
    
    public void logout(List li,List ll){
        s.remove(u);
        this.agree(li);
        this.disagree(ll);
        em.flush();
    }
    
    private void agree(List<String> li){
        for(String s:li){
            String[] ss = s.split(":");
            double id = Double.parseDouble(ss[0].substring(ss[0].indexOf('_')+1));
            int msg = Integer.parseInt(ss[1]);
            Opinion o = em.find(Opinion.class, id);
            if(o.getAgree()!=msg){
                if(o.getAgree()<msg){
                    u.getSetMap().add(o.getId());
                    if(!o.getU().getUsername().equals(u.getUsername()))
                        o.getD().getActivities().add(u.getUsername().toUpperCase()+" agreed with "+o.getU().getUsername().toUpperCase()+" 's Opinion");
                    else
                        o.getD().getActivities().add(u.getUsername().toUpperCase()+" agreed with his/her Opinion");
                }else
                    u.getSetMap().remove(o.getId());
                o.setAgree(msg);
            }
            em.merge(o);
            em.merge(u);
        }
    }
    
    private void disagree(List<String> li){
        for(String s : li){
            String[] ss = s.split(":");
            double id = Double.parseDouble(ss[0].substring(ss[0].indexOf('_')+1));
            int msg = Integer.parseInt(ss[1]);
            Opinion o = em.find(Opinion.class, id);
            if(o.getDisagree()!=msg){
                if(o.getDisagree()<msg){
                    u.getSetM().add(o.getId());
                    if(!o.getU().getUsername().equals(u.getUsername()))
                        o.getD().getActivities().add(u.getUsername().toUpperCase()+" Disagreed with "+o.getU().getUsername().toUpperCase()+" 's Opinion");
                    else
                        o.getD().getActivities().add(u.getUsername().toUpperCase()+" Disagreed with His/Her Opinion");
                }else
                    u.getSetM().remove(o.getId());
                o.setDisagree(msg);
            }
            em.merge(o);
            em.merge(u);
        }
    }
    
    public String getComments(double id){      
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        Opinion o = em.find(Opinion.class, id);
        Set<Opinion> l = o.getComments();
        r += "<Opinions>";
        for(Opinion oo:l){
            r += this.getOpinion(oo);
        }
        r += "</Opinions>";
        return r;
    }
    
    private String getOpinion(Opinion o){
        String r = "<Opinion id='"+o.getD().getName()+"_"+o.getId()+"'>"
                      + "<msg>"+o.getOpinion()+"</msg>"
                      + "<agree>"+o.getAgree()+"</agree>"
                      + "<disagree>"+o.getDisagree()+"</disagree>"
                      + "<aset>"+u.getSetMap().contains(o.getId())+"</aset>"
                      + "<dset>"+u.getSetM().contains(o.getId())+"</dset>"
                      + "<media>";
                      for(Media m:o.getMedia()){
                          if(m.getCt().startsWith("image"))
                              r += "<image>"+'$'+o.getId()+"_"+m.getName()+"</image>";
                          else if(m.getCt().startsWith("audio"))
                              r += "<aud>"+'$'+o.getId()+"_"+m.getName()+"</aud>";
                          else
                              r += "<vid>"+'$'+o.getId()+"_"+m.getName()+"</vid>";
                      }
               r += "</media>";
              if(o.getComments().size()>1)
                  r += "<comments>"+o.getComments().size()+" Comments </comments>";
              else if(o.getComments().size()==1)
                  r += "<comments>"+o.getComments().size()+" Comment </comments>";
              r += "</Opinion>";
        return r;
    }
    
    private String getDiscussion(Discussion d){
        String r = "<Discussion id = '"+d.getName()+"'>"
                      + "<name>"+d.getName()+"</name>"
                      + "<desc>"+d.getAbout()+"</desc>"
                      + "<ledit>"+d.getLedit()+"</ledit>"
                      + this.suggestions(d);
                      if(!d.getMedia().isEmpty()){
                      r+= "<dmedia>";
                      for(Media m:d.getMedia()){
                          if(m.getCt().startsWith("image"))
                              r += "<image>"+d.getName()+"_"+m.getName()+"</image>";
                          else if(m.getCt().startsWith("audio"))
                              r += "<aud>"+d.getName()+"_"+m.getName()+"</aud>";
                          else
                              r += "<vid>"+d.getName()+"_"+m.getName()+"</vid>";
                      }
               r += "</dmedia>";}
               for(Opinion o:d.getOpinions()){
                   r += this.getOpinion(o);
               }
               r += "</Discussion>";
        return r;
    }
   
    public String getDisc(Discussion d){
        String r = "<!DOCTYPE html>"
                + "<html>"
                       + "<head>"
                           + "<title>Trending- "+d.getName()+"</title>"
                           + "<script src=\"folder/script.js\"></script>"
                           + "<link href=\"folder/css.css\" rel=\"stylesheet\"/>"
                       + "</head>"
                       + "<body class='disc'>"
                           + "<div>"  
                                + "<h1>"+d.getName()+"</h1>"
                                + "<button onclick='activities()' class='abc'>ACTIVITIES</button>";
                                if(d.getU().contains(u))
                                    r += "<button disabled='disabled' class='unsub'>Subscribed</button>";
                                else
                                    r += "<button onclick='discsub(event)' class='unsub' id='"+d.getName()+"'>Subscribe?</button>";
                                r += "<h3 class='h3'>" + d.getAbout()+"</h3>"
                                 + "<div class='hide' id='ms'></div>";
                            r+= "</div><div id='mdiv'>"; 
                            Set<Media> l = d.getMedia();
                            Iterator<Media> it = l.iterator();
                                 if(l.size()==1){
                                      Media m = it.next();
                                     if(m.getCt().startsWith("image"))
                                         r += "<img class='image' src='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+"'/>";
                                     else if(m.getCt().startsWith("audio"))
                                         r += "<audio controls src='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+"'/>";
                                     else
                                         r += "<video controls width='700px' height='700px' src='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+"'/>";
                                 }
                                 else {
                                   r += "<table class='table'>";
                                   int j=0;
                                     for(int i=0;i<Math.ceil((float)l.size()/4);i++){
                                        r += "<tr>";
                                        while(it.hasNext()){
                                             j++;
                                             Media m = it.next();
                                             if(m.getCt().startsWith("image"))
                                                 r += "<td><a href='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+"' > <img class='image' src='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+ "' /></a></td>";
                                             else if(m.getCt().startsWith("audio"))
                                                 r += "<td><a href='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+"' > <audio controls src='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+ "' ></audio></a></td>";
                                             else
                                                 r += "<td><a href='/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+"' > <video controls src=/Trending/mediaservlet/"+d.getName()+"_"+m.getName()+ "' ></video></a></td>";
                                             if(j==4){
                                                j=0;
                                                break;
                                             }
                                        }
                                        r += "</tr>";
                                      }
                                 r += "</table>";
                              }    
                             r += "</div>";
                             r += "<hr></hr>";
                                 if(d.getCount()>0){
                                     r += "<div id='opdiv'>";
                                     for(Opinion o:d.getOpinions()){ 
                                         String id = d.getName()+"_"+o.getId();
                                         r += "<div class='opclass' id='"+id+"'>"
                                                    + "<p class='odiv'>"+o.getOpinion()+"</p>";
                                         Set<Media> ll = o.getMedia();
                                         Iterator<Media> itt = ll.iterator();
                                         if(ll.size()==1){
                                             Media m = itt.next();
                                             if(m.getCt().startsWith("image"))
                                                 r += "<img class='image' src='/Trending/mediaservlet/$"+o.getId()+"_"+m.getName()+"'/>";
                                             else if(m.getCt().startsWith("audio"))
                                                 r += "<audio controls src='/Trending/mediaservlet/"+o.getId()+"_"+m.getName()+"'/>";
                                             else
                                                 r += "<video controls width='700px' height='700px' src='/Trending/mediaservlet/"+o.getId()+"_"+m.getName()+"'/>";
                                         }else{
                                             int j=0;
                                             r += "<table class='table'>";
                                             for(int i=0;i<Math.ceil((float)l.size()/4);i++){
                                                 r += "<tr>";
                                                   while(itt.hasNext()){
                                                       j++;
                                                     Media m = itt.next();
                                                     if(m.getCt().startsWith("image"))
                                                         r += "<td><a href='/Trending/mediaservlet/"+'$'+o.getId()+"_"+m.getName()+"' > <img class='image' src='/Trending/mediaservlet/$"+o.getId()+"_"+m.getName()+ "'/></a></td>";
                                                     else if(m.getCt().startsWith("audio"))
                                                         r += "<td><a href'=/Trending/mediaservlet/$"+o.getId()+"_"+m.getName()+"' > <audio src='/Trending/mediaservlet/"+o.getId()+"_"+m.getName()+ "'></audio></a></td>";
                                                     else
                                                         r += "<td><a href'=/Trending/mediaservlet/$"+o.getId()+"_"+m.getName()+"' > <video src='/Trending/mediaservlet/"+o.getId()+"_"+m.getName()+ "'></video></a></td>";
                                                     if(j==4){
                                                         j=0;break;
                                                     }
                                                   }
                                                 r += "</tr>";
                                            }
                                             r += "</table>";
                                         }
                                         r += "<div id='adiv'>"
                                             + "<pre id='a"+id+"'>"+o.getAgree()+" Agreed"+"</pre>"
                                             + "<pre id='d"+id+"'>"+o.getDisagree()+" Disagreed"+"</pre>";
                                             if(o.getComments().size()>0)
                                                 r += "<a href=\"\"><pre onclick='getComment(event)' id='g"+id+"'>"+o.getComments().size()+" Comments </pre></a>"; 
                                           r += "</div></div>";
                                     }
                                     r += "</div>";
                                 }
                       r += "</body>"
                + "</html>";
        return r;
    }
    
    public String getAll(){
        String r="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        User uu = em.find(User.class, u.getUsername());
        r += "<Disc><Discussions>";
        Set<Discussion> l = uu.getD();   
        for(Discussion d:l)
            r += this.getDiscussion(d);
        r += "</Discussions>";
        String a = getMost();
        if(!"".equals(a))
            r += "<news>" + a + "</news>";
        r += "</Disc>";
        return r;
    }
    
    private String getMost(){
        String r = "";
        Set<Discussion> l =u.getD();
        List<Discussion> all = em.createNamedQuery("Discussion.findAll").getResultList();
        all.removeAll(l);
        List<Opinion> ll = new ArrayList();
        for(Discussion d:all){ 
            Opinion op=null,opp=null;
            int high=0,h=0;
            for(Opinion o:d.getOpinions()){    
                if(o.getAgree()>high){
                    high = o.getAgree();
                    op = o;
                    continue;
                }
                if(o.getDisagree()>h){
                    h = o.getDisagree();
                    opp = o;
                }
            }  
            ll.add(op);
            ll.add(opp);
        }
        if(ll.size()>0){
            r += "<op>";
            for(Opinion o:ll){
                if(o!=null)
                    r += this.getOpinion(o);
            }
            r += "</op>";
        }
        for(int i=0;i<all.size();i++){
            r += "<ad>"+all.get(i).getName()+"</ad>";
        }
        return r;
    }
  
    private String suggestions(Discussion s){
        String r="";
        String[] sep = s.getName().split("\\W");
        List<Discussion> f = new ArrayList();
        List<Discussion> l = em.createNamedQuery("Discussion.findAll").getResultList();
        Set<Discussion> sd = u.getD();
        l.removeAll(sd);
        for(String ss:sep){
            Pattern p = Pattern.compile(ss);
            if(ss.equalsIgnoreCase("is")||ss.equalsIgnoreCase("and")||ss.equalsIgnoreCase("or")||
                    ss.equalsIgnoreCase("am")||ss.equalsIgnoreCase("are")||ss.equalsIgnoreCase("was")||
                    ss.equalsIgnoreCase("were")||ss.equalsIgnoreCase("so")||ss.equalsIgnoreCase("very"))continue;
            for(Discussion d:l){
                if(d.equals(s))continue;
                String[] dep = d.getName().split("\\W");
                for(String de:dep){
                    Matcher m = p.matcher(de);
                    if(m.find()){
                        f.add(d);break;
                    }
                } 
            }
        }
        for(Discussion d:f){
            r += "<sug>" + d.getName()+"</sug>";
        }
        return r;
    }
    
    public String createTot(String s,String de,Set<Media> b){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        Discussion d = em.find(Discussion.class, s);
        if(d==null){
            d = new Discussion(s);
            d.setLedit("");
            d.setCreator(u.getUsername());
            d.setAbout(de);
            d.setMedia(b);
            u.getD().add(d);
            d.getU().add(u);
            d.getActivities().add(u.getUsername().toUpperCase()+" created Topic "+d.getName().toUpperCase());
            em.merge(d);}
        else if(d.getU().contains(u))
            return "";
        else{
            d.getU().add(u);
            u.getD().add(d);
            d.getActivities().add(u.getUsername().toUpperCase()+" joined Topic "+d.getName().toUpperCase());
            em.merge(d);
        }    
        r += this.getDiscussion(d);
        return r;
    }
    
    public String addOpinion(String disc,Set<Media> b){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        String[] s = disc.split("#");
        String id = s[0];
        String real = s[1];
        Opinion o = new Opinion(Calendar.getInstance().getTimeInMillis()*Math.random());
        o.setOpinion(real);
        o.setMedia(b);
        o.setU(u);
        Discussion d = em.find(Discussion.class, id);
        d.setCount(d.getCount()+1);
        o.setD(d);
        Set<Opinion> l = d.getOpinions();
        l.add(o);
        String st = u.getUsername().toUpperCase() + " gave an Opinion on the Discussion "+ d.getName();
        d.getActivities().add(st);
        em.merge(d);
        return r + this.getOpinion(o);
    }
    
    public String addOpinion(String disc,String msg,double own,Set<Media> b){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        Opinion o = new Opinion(Calendar.getInstance().getTimeInMillis()*Math.random()); 
        o.setOpinion(msg);
        o.setU(u);
        o.setMedia(b);
        Discussion d = em.find(Discussion.class, disc);
        o.setD(d);
        Opinion owner = em.find(Opinion.class, own);
        owner.getComments().add(o);
        String st = u.getUsername().toUpperCase() + " commented on "+owner.getU().getUsername().toUpperCase()+"'s Opinion";
        d.getActivities().add(st);
        d.setCount(d.getCount()+1);
        em.persist(o);
        em.merge(d);
        return r + this.getOpinion(o);
    }
    
    public String comm(String i,List<String> li){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        Discussion d = em.find(Discussion.class,i.substring(0, i.indexOf('_')));
        for(String v:li){
            String[] st = v.split(":");
            double id = Double.parseDouble(st[0].substring(st[0].indexOf("_")+1));
            Opinion opi = em.find(Opinion.class, id);
            String[] ss = st[1].split("_");    
            if(opi.getAgree()<Integer.parseInt(ss[0]))
                u.getSetMap().add(id);
            else if(opi.getAgree()>Integer.parseInt(ss[0])) u.getSetMap().remove(id);
            if(opi.getDisagree()<Integer.parseInt(ss[1]))
                u.getSetM().add(id);
            else if(opi.getDisagree()>Integer.parseInt(ss[1]))
                u.getSetM().remove(id);
            opi.setAgree(Integer.parseInt(ss[0]));
            opi.setDisagree(Integer.parseInt(ss[1]));
        }
        Opinion o = em.find(Opinion.class, Double.parseDouble(i.substring(i.indexOf('_')+1)));    
        em.merge(d);
        em.merge(u);
        return r + getNo(o); 
    }
    
    private String getNo(Opinion op){
        String r = "<comments>";
        int j = op.getComments().size();
        if(j<=1){
            r += j + " Comment </comments>";
            return r;
        }else{
            r += j + " Comments </comments>";
            return r;
        }
    }
    
    public Media getMedia(String d,String m){
        Discussion dd = em.find(Discussion.class, d);
        Set<Media> l = dd.getMedia();
        for(Media me:l){
            if(me.getName().equalsIgnoreCase(m))
                return me;
        }
        return null;
    }
    public Media getOMedia(Double o,String m){
        Opinion op = em.find(Opinion.class, o);
        Set<Media> l = op.getMedia();
        for(Media me:l){
            if(me.getName().equalsIgnoreCase(m))
                return me;
        }
        return null;
    }
    public void unSub(String d){
        Set<Discussion> l = u.getD();
        Discussion di = em.find(Discussion.class, d);
        if(l.contains(di)){
            l.remove(di);
            di.getU().remove(u);
        }
        em.merge(u);
        em.merge(di);
    }
   
    public String edit(String id){
        Discussion d = em.find(Discussion.class, id);
        if(d.getCreator().equalsIgnoreCase(u.getUsername()))
            return "true";
        else
            return "";
    }
    
    public String edi(double i){
        Opinion o = em.find(Opinion.class, i);
        if(o.getU().getUsername().equalsIgnoreCase(u.getUsername()))
            return "true";
        else
            return "";
    }
    
    public String saveChanges(String id,String name,String d,Set<Media> l,Set<Media> ll){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        Discussion di = em.find(Discussion.class, id);
        Set<User> su = di.getU();
        su.remove(u);
        Set<String> sst = new HashSet();
        for(User us:su){
            sst.add(us.getUsername());
        }
        di.setName(name);
        di.setAbout(d);
        Set<Media> li = di.getMedia();
        li.removeAll(l);
        li.addAll(ll);
        di.getActivities().add(0,"Topic "+di.getName()+" has been edited.");
        di.setLedit(Date.valueOf(LocalDate.now())+" "+Time.valueOf(LocalTime.now()));
        Chat.sendEdit(sst);
        em.merge(di);
        return r + this.getDiscussion(di);
    }
    
    public String save(double id,String d,Set<Media> l,Set<Media> ll){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        Opinion o = em.find(Opinion.class, id);
        o.setOpinion(d);
        Set<Media> li = o.getMedia();
        li.removeAll(l);
        li.addAll(ll);
        em.merge(o);
        return r + this.getOpinion(o);
    }
    
    public String sub(String id){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        Discussion d = em.find(Discussion.class, id);
        if(d.getU().contains(u))
            return "";
        else{
            d.getU().add(u);
            d.getActivities().add(u.getUsername().toUpperCase()+" joined Discussion "+d.getName().toUpperCase());
            u.getD().add(d);
            em.merge(u);
            em.merge(d);
            r += this.getDiscussion(d);
            return r;
        }
    }
    public String discsub(String id){
        Discussion d = em.find(Discussion.class, id);
        if(d.getU().contains(u))
            return "You have already subscribed to this Discussion";
        else{
            d.getU().add(u);
            d.getActivities().add(u.getUsername().toUpperCase()+" joined Discussion "+d.getName().toUpperCase());
            u.getD().add(d);
            em.merge(u);
            em.merge(d);
            return "Subscribed";
        }
    }
    
    public void saveM(String dd,String v,String name,boolean a){
        User receiver = em.find(User.class, dd.toLowerCase());
        Message m = new Message(name,a);
        m.setBody(v);
        //em.persist(m);
        receiver.getMsgs().add(m);
        em.merge(receiver);
    }
    
    public String getMsg(){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><d>";
        List<Message> l = u.getMsgs();
        Collections.reverse(l);
        for(Message a:l){
            r += "<a>";
            r += "<id>"+a.getId()+"</id><msg>"+a.getBody()+"</msg>"
                    + "<name>"+a.getSender()+"</name>"+"<flag>"+a.getReadflag()+"</flag></a>" ;
        }
        r += "</d>";
        return r;
    }
    
    public void read(String id){
        Long i = Long.parseLong(id);
        List<Message> l = u.getMsgs();
        for(Message m : l){
            if(Objects.equals(i, m.getId())){
                m.setReadflag(true);
                Message me = em.find(Message.class, i);
                me.setReadflag(true);
                em.merge(me);
                break;
            }
        }
        em.merge(u);
    }
    
    public String getActivities(){
        String r = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><d>";
        Set<Discussion> l = u.getD();
        for(Discussion d:l){
            for(String a:d.getActivities())
                r += "<activities>" + a + "</activities>";
        }
        r += "</d>";
        return r;
    }
    
    public String delete(){
        s.remove(u);
        em.remove(u);
        return "true";
    }
    
    public String getTopics(){
        String r="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><d>";
        List<Discussion> l = em.createNamedQuery("Discussion.findAll").getResultList();
        for(Discussion d:l){
            if(d.getCreator().equalsIgnoreCase(u.getUsername())){
                r += "<topic>"+d.getName()+"</topic>";
            }
        }
        r += "</d>";
        return r;
    }
    
    public void dTopic(String t){
        Discussion d = em.find(Discussion.class, t);
        em.remove(d);
        u.getD().remove(d);
        em.merge(u);
    }
}