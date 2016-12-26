package com.imagine.trending.servlet;


import com.imagine.trending.beans.JustSessionBean;
import com.imagine.trending.entities.Media;
import com.imagine.trending.beans.Trending;
import com.imagine.trending.entities.Discussion;
import com.imagine.trending.entities.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author Alexander
 */
@MultipartConfig
@WebServlet(name="TrendingServlet", urlPatterns={"/Trending/welcome/*"}, asyncSupported=true)
public class TrendingServlet extends HttpServlet{
    
    @EJB
    private JustSessionBean j;
    @PersistenceContext
    private EntityManager em ;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
            final AsyncContext ac = request.startAsync();  
            final String qs = request.getQueryString();
            final String pi = request.getPathInfo();
            Runnable r = new MyRunnable(qs,pi,ac);
            ac.start(r);
    }
    class MyRunnable implements Runnable{
        InitialContext ic;    
        String d;
        Trending t=null;
        String qs,pi;
        AsyncContext ac;
        MyRunnable(String qs,String pi,AsyncContext ac) {
            try {
                this.ac = ac;
                this.qs = qs;
                this.pi = pi;
                this.ic = new InitialContext();
            } catch (NamingException ex) {
                Logger.getLogger(TrendingServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        @Override
        public void run(){
                    HttpServletRequest req = (HttpServletRequest) ac.getRequest();
                    HttpServletResponse res = (HttpServletResponse) ac.getResponse();
                    try{
                    if(pi!=null){    
                        if(pi.equals("/l")){
                            t = (Trending)ic.lookup("java:module/Trending");
                            String u = req.getParameter("u");
                            String p = req.getParameter("p");
                            User us = new User(u,p);
                            res.setContentType("text/plain"); 
                            res.getWriter().print(t.cLogin(us));
                        }else if(pi.equals("/s")){
                            t = (Trending)ic.lookup("java:module/Trending");
                            HttpSession hs = req.getSession();
                            String u = req.getParameter("u"); 
                            hs.setAttribute("t", t);
                            String p = req.getParameter("p");
                            User us = new User(u.toLowerCase(),p);
                            res.sendRedirect(t.signUp(us));
                        }else if(pi.equals("/login")){
                            t = (Trending)ic.lookup("java:module/Trending");
                            HttpSession hs = req.getSession();
                            String u = req.getParameter("u"); 
                            hs.setAttribute("t", t);
                            String p = req.getParameter("p");
                            User us = new User(u.toLowerCase(),p);
                            String s = t.login(us);
                            if(s.equalsIgnoreCase("/trending.html"))
                                res.sendRedirect(s);
                            else{res.setContentType("text/plain");
                                res.getWriter().print(s);}
                        }
                        else if(pi.equals("/o")){
                            List ll = new ArrayList();
                            List lm = new ArrayList();
                            String s = req.getParameter("js");
                            String f = req.getParameter("jss");
                            t = (Trending) req.getSession(false).getAttribute("t");
                            if(s==null&&f!=null){
                                if(f.contains(",")){
                                    String[] sd = f.split(",");
                                    for (String s2 : sd) {
                                        lm.add(s2.replaceAll("[{}\"]", ""));
                                    }
                                }else
                                lm.add(f.replaceAll("[{}\"]", ""));
                            }else if(f==null&&s!=null){
                                if(s.contains(",")){
                                    String[] sd = s.split(",");
                                    for (String s2 : sd) {
                                        ll.add(s2.replaceAll("[{}\"]", ""));
                                    }
                                }else
                                ll.add(s.replaceAll("[{}\"]", ""));
                            }else if(s!=null&&f!=null){
                                if(s.contains(",")&&f.contains(",")){
                                    String[] ss = s.split(",");
                                    String[] sd = f.split(",");
                                    for (String s1 : ss) {
                                        ll.add(s1.replaceAll("[{}\"]", ""));
                                    }
                                    for (String s2 : sd) {
                                        lm.add(s2.replaceAll("[{}\"]", ""));
                                    }
                                }else if(s.contains(",")&&!f.contains(",")){
                                    lm.add(f.replaceAll("[{}\"]", ""));
                                    String[] ss = s.split(",");
                                    for (String s1 : ss) {
                                        ll.add(s1.replaceAll("[{}\"]", ""));
                                    }
                                }else if(f.contains(",")&&!s.contains(",")){
                                    ll.add(f.replaceAll("[{}\"]", ""));
                                    String[] ss = f.split(",");
                                    for (String s1 : ss) {
                                        lm.add(s1.replaceAll("[{}\"]", ""));
                                    }
                               }else{
                                    ll.add(s.replaceAll("[{}\"]", ""));
                                    lm.add(f.replaceAll("[{}\"]", ""));
                               }
                          }else{}
                          t.logout(ll, lm);
                          req.getSession(false).invalidate();
                      }else if(pi.startsWith("/chat")){ 
                          t = (Trending)req.getSession(false).getAttribute("t");
                          res.getWriter().print(t.online(pi.substring(6)));
                      }else if(pi.startsWith("/off")){
                          String name = req.getParameter("n");    
                          String dd = req.getParameter("dd");
                          String v = req.getParameter("v");
                          t = (Trending) req.getSession(false).getAttribute("t");
                          t.saveM(dd,v,name,false);
                      }else if(pi.startsWith("/getMsg")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          res.getWriter().println(t.getMsg());
                      }else if(pi.startsWith("/acti")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          res.getWriter().println(t.getActivities());
                      }else if(pi.startsWith("/delete")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          res.getWriter().println(t.delete());
                      }
                      else if(pi.startsWith("/mtopics")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          res.getWriter().println(t.getTopics());
                      }else if(pi.startsWith("/dtopic")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          t.dTopic(pi.substring(8));
                      }else if(pi.equals("/z")){
                          Part disc = req.getPart("%");
                          Part desc = req.getPart("&");
                          Set<Media> l = new HashSet();
                          byte[] b = new byte[1024];
                          StringWriter sw = new StringWriter();
                          StringWriter k = new StringWriter();
                          int i,n,c,s;
                          InputStream iss = disc.getInputStream();
                          InputStream q = desc.getInputStream();
                          while((i=iss.read())!=-1)
                              sw.write(i);  
                          while((n=q.read())!=-1)
                              k.write(n);
                          for(Part m:req.getParts()){
                              if(!"&".equals(m.getName())&&!"%".equals(m.getName())){
                              if(m.getName().charAt(0)=='@'){
                                  InputStream is = m.getInputStream();
                                  String ct = m.getContentType();
                                  ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                  while((c=is.read(b))!=-1){
                                      bas.write(b, 0, c);
                                  }
                                  byte[] by = bas.toByteArray();
                                  Media me = new Media(m.getName(),ct,by);m=null;
                                  l.add(me);
                              }else{
                                  InputStream is = m.getInputStream();
                                  String ct = m.getContentType();
                                  ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                  while((s=is.read(b))!=-1){
                                      bas.write(b, 0, s);
                                  }
                                  byte[] by = bas.toByteArray();
                                  Media me = new Media(m.getName(),ct,by);
                                  me.setCaption(m.getName());m=null;
                                  l.add(me);
                              }
                          }}
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.createTot(sw.toString(),k.toString(),l);l.clear();
                          res.getWriter().println(d);
                      }else if(pi.equals("/b")){
                          Part disc = req.getPart("$");
                          Part desc = req.getPart("%");
                          Part name = req.getPart("*");
                          Set<Media> l = new HashSet();
                          Set<Media> ll = new HashSet();
                          StringWriter sw = new StringWriter();
                          StringWriter k = new StringWriter();
                          StringWriter na = new StringWriter();
                          int i,n,c,g;
                          InputStream iss = disc.getInputStream();
                          InputStream q = desc.getInputStream();
                          InputStream ng = name.getInputStream();
                          while((i=iss.read())!=-1)
                              sw.write(i);
                          disc = null;
                          while((g=ng.read())!=-1)
                              na.write(g);
                          name = null;
                          while((n=q.read())!=-1)
                              k.write(n);
                          desc = null;
                          for(Part m:req.getParts()){
                              if(!"$".equals(m.getName())&&!"%".equals(m.getName())&&!"*".equals(m.getName())){
                                  InputStream ist = m.getInputStream();
                                  Media me;
                                  ByteArrayOutputStream bas;
                                  switch (m.getName().charAt(0)) { 
                                      case '@':
                                          {  
                                              byte[] b = new byte[1024];
                                              String ct = m.getContentType();
                                              bas = new ByteArrayOutputStream();
                                              while((c=ist.read(b))!=-1){
                                                  bas.write(b, 0, c);
                                              }       
                                              byte[] by = bas.toByteArray();
                                              me = new Media(m.getName(),ct,by);
                                              ll.add(me);
                                              break;
                                          }
                                      case '&':
                                          StringWriter ss = new StringWriter();
                                          while((c=ist.read())!=-1){
                                              ss.write(c);
                                          } 
                                          me = new Media(ss.toString());
                                          l.add(me);
                                          break;
                                      default:
                                          {
                                              byte[] b = new byte[1024];
                                              String ct = m.getContentType();
                                              bas = new ByteArrayOutputStream();
                                              while((c=ist.read(b))!=-1){
                                                  bas.write(b, 0, c);
                                              }       
                                              byte[] by = bas.toByteArray();
                                              me = new Media(m.getName(),ct,by);
                                              me.setCaption(m.getName());
                                              ll.add(me);
                                              break;
                                          }
                                  }
                                  m = null;
                              }
                          }
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.saveChanges(sw.toString(),na.toString(), k.toString(), l,ll);l.clear();ll.clear();
                          res.getWriter().println(d);
                      }else if(pi.equals("/w")){
                          Part disc = req.getPart("$");
                          Part name = req.getPart("%");
                          Set<Media> l = new HashSet();
                          Set<Media> ll = new HashSet();
                          StringWriter sw = new StringWriter();
                          StringWriter k = new StringWriter();
                          int i,n,c;
                          InputStream iss = disc.getInputStream();
                          InputStream q = name.getInputStream();
                          while((i=iss.read())!=-1)
                              sw.write(i);
                          disc = null;
                          while((n=q.read())!=-1)
                              k.write(n);
                          name = null;
                          for(Part m:req.getParts()){
                              if(!"$".equals(m.getName())&&!"%".equals(m.getName())){
                                  InputStream ist = m.getInputStream();
                                  Media me;
                                  switch (m.getName().charAt(0)) {
                                      case '@':
                                          {
                                              byte[] b = new byte[1024];
                                              String ct = m.getContentType();
                                              ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                              while((c=ist.read(b))!=-1){
                                                  bas.write(b, 0, c);
                                              }       byte[] by = bas.toByteArray();
                                              me = new Media(m.getName(),ct,by);
                                              ll.add(me);
                                              break;
                                          }
                                      case '&':
                                          StringWriter ss = new StringWriter();
                                          while((c=ist.read())!=-1){
                                              ss.write(c);
                                          }   me = new Media(ss.toString());
                                          l.add(me);
                                          break;
                                      default:
                                          {
                                              byte[] b = new byte[1024];
                                              String ct = m.getContentType();
                                              ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                              while((c=ist.read(b))!=-1){
                                                  bas.write(b, 0, c);
                                              }       byte[] by = bas.toByteArray();
                                              me = new Media(m.getName(),ct,by);
                                              me.setCaption(m.getName());
                                              ll.add(me);
                                              break;
                                          }
                                  }
                                  m = null;
                              }
                          }
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.save(Double.parseDouble(sw.toString()), k.toString(), l,ll);l.clear();ll.clear();
                          res.getWriter().println(d);
                      }else if(pi.equals("/disc")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          String disc = req.getParameter("disc");
                          d = t.createTot(disc.toLowerCase(),"",new HashSet());
                          res.getWriter().println(d);
                      }else if(pi.equals("/q")){
                          Part z = req.getPart("%");
                          Part a = req.getPart("&");
                          int i,c;  
                          InputStream is =z.getInputStream();
                          InputStream iss = a.getInputStream();
                          StringWriter sw = new StringWriter();
                          StringWriter sww = new StringWriter();
                          while((i=is.read())!=-1)
                              sw.write(i);
                          z = null;
                          while((c=iss.read())!=-1)
                              sww.write(c);
                          a = null;
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.createTot(sw.toString().toLowerCase(), sww.toString(),new HashSet());
                          res.getWriter().println(d);
                      }else if(pi.equals("/i")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          String op = req.getParameter("id");
                          String opi = req.getParameter("opi");
                          d = t.addOpinion(op+"#"+opi,new HashSet());
                          res.getWriter().println(d);
                      }else if(pi.equals("/id")){
                          Part o = req.getPart("&");
                          Part ip = req.getPart("%");
                          Set<Media>l = new HashSet();
                          byte[] b = new byte[1024];
                          StringWriter sw = new StringWriter();
                          int i;
                          StringWriter sww = new StringWriter();
                          int ii;
                          InputStream iss = o.getInputStream();
                          InputStream ist = ip.getInputStream();
                          while((i=iss.read())!=-1)
                              sw.write(i);
                          o = null;
                          while((ii=ist.read())!=-1)
                              sww.write(ii);
                          ip = null;
                          for(Part m:req.getParts()){
                              if(!"&".equals(m.getName())&&!"%".equals(m.getName())){
                                  if(m.getName().charAt(0)=='@'){
                                      InputStream is = m.getInputStream();
                                      String ct = m.getContentType();
                                      int c;
                                      ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                      while((c=is.read(b))!=-1){
                                          bas.write(b, 0, c);
                                      }
                                      byte[] by = bas.toByteArray();
                                      Media me = new Media(m.getName(),ct,by);
                                      m = null;
                                      l.add(me);
                                  }else{
                                      InputStream is = m.getInputStream();
                                      String ct = m.getContentType();
                                      int c;
                                      ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                      while((c=is.read(b))!=-1){
                                          bas.write(b, 0, c);
                                      }
                                      byte[] by = bas.toByteArray();
                                      Media me = new Media(m.getName(),ct,by);
                                      me.setCaption(m.getName());
                                      m = null;
                                      l.add(me);
                                  }
                              }
                          }
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.addOpinion(sw.toString()+"#"+sww.toString(),l);
                          res.getWriter().println(d);
                      }else if(pi.equals("/c")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          String i = req.getParameter("id");
                          double o = Double.parseDouble(req.getParameter("o"));
                          String msg = req.getParameter("msg");
                          d = t.addOpinion(i,msg, o,new HashSet());
                          res.getWriter().println(d);
                      }else if(pi.equals("/m")){
                          Part id = req.getPart("id");
                          Part o = req.getPart("o");
                          Part ip = req.getPart("com");
                          Set<Media> l = new HashSet();
                          byte[] b = new byte[1024];
                          StringWriter sw = new StringWriter();
                          StringWriter stw = new StringWriter();
                          int it,i,ii;
                          StringWriter sww = new StringWriter();
                          InputStream iss = o.getInputStream();
                          InputStream ist = ip.getInputStream();
                          InputStream isn = id.getInputStream();
                          while((i=iss.read())!=-1)
                              sw.write(i);
                          while((ii=ist.read())!=-1)
                              sww.write(ii);
                          while((it=isn.read())!=-1)
                              stw.write(it);
                          for(Part m:req.getParts()){
                              if(!"id".equals(m.getName())&&!"o".equals(m.getName())&&!"com".equals(m.getName())){
                                  InputStream is = m.getInputStream();
                                  String ct = m.getContentType();
                                  int c;
                                  ByteArrayOutputStream bas = new ByteArrayOutputStream();
                                  while((c=is.read(b))!=-1){
                                      bas.write(b, 0, c);
                                  }
                                  byte[] by = bas.toByteArray();
                                  Media me = new Media(m.getName(),ct,by);
                                  l.add(me);
                              }
                          }
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.addOpinion(stw.toString(),sww.toString(),Double.parseDouble(sw.toString()),l);
                          res.getWriter().println(d);
                      }else if(pi.equals("/g")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.getComments(Double.parseDouble(req.getParameter("id")));
                          res.getWriter().println(d);
                      }else if(pi.startsWith("/j")){
                          List l = new ArrayList();
                          String s = req.getParameter("js");
                          if(s.contains(",")){
                              String f = s.replaceAll("[{}\"]", "");
                              String[] ss = f.split(",");
                              l.addAll(Arrays.asList(ss));
                          }else{
                              l.add(s.replaceAll("[{}\"]", ""));
                          }
                          t = (Trending) req.getSession(false).getAttribute("t");
                          d = t.comm(pi.substring(2),l);
                          res.getWriter().print(d);
                      }else if(pi.startsWith("/sub")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          res.getWriter().println(t.sub(pi.substring(5)));
                      }else if(pi.startsWith("/read")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          t.read(pi.substring(6));
                      }else if(pi.startsWith("/discsub")){
                          t = (Trending) req.getSession(false).getAttribute("t");
                          res.getWriter().println(t.discsub(pi.substring(9)));
                      }
                      else{
                          Discussion di = (Discussion)em.createNamedQuery("Discussion.findByName").setParameter("name", pi.substring(1)).getSingleResult();
                          t = (Trending) req.getSession(false).getAttribute("t");
                          if(di!=null){
                              res.setContentType("text/html");
                              res.getWriter().println(t.getDisc(di));
                          }else{
                              res.setContentType("text/plain");
                              res.getWriter().println("");
                          }
                      }
                   }else if(qs!=null){
                       if(qs.charAt(0)=='t'){
                           res.getWriter().println(j.trender());}
                       else if(qs.charAt(0)=='e'){
                           res.setContentType("text/plain");
                           String s = URLDecoder.decode(qs.substring(2),"UTF-8");
                           t = (Trending) req.getSession(false).getAttribute("t");
                           res.getWriter().println(t.edit(s));
                       }else if(qs.charAt(0)=='v'){
                           res.setContentType("text/plain");
                           String s = URLDecoder.decode(qs.substring(2),"UTF-8");
                           t = (Trending) req.getSession(false).getAttribute("t");
                           res.getWriter().println(t.edi(Double.parseDouble(s)));
                      }else if(qs.charAt(0)=='u'){
                           String s = URLDecoder.decode(qs.substring(2),"UTF-8");
                           res.setContentType("text/plain");
                           res.getWriter().println(j.checkUsername(s.toLowerCase()));
                      }else if(qs.charAt(0)=='s'){
                           String s = URLDecoder.decode(qs.substring(2), "UTF-8");
                           t = (Trending) req.getSession(false).getAttribute("t");
                           res.setContentType("text/plain");
                           t.unSub(s);
                      }else if(qs.charAt(0)=='n'){
                           t = (Trending)req.getSession(false).getAttribute("t");
                           res.setContentType("text/plain");
                           res.getWriter().println(t.getU());
                      }
                 }else{
                      t = (Trending) req.getSession(false).getAttribute("t");
                      d = t.getAll();
                      res.getWriter().println(d);
                 }
               }catch(Exception e){}
                    ic=null;
                    t=null;
             ac.complete();
             }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
