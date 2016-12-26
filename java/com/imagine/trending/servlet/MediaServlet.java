/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.imagine.trending.servlet;

import com.imagine.trending.entities.Media;
import com.imagine.trending.beans.Trending;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "MediaServlet", urlPatterns = {"/Trending/mediaservlet/*"},asyncSupported=true)
public class MediaServlet extends HttpServlet {

    @EJB
    private Trending t;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String pi = request.getPathInfo().substring(1);
        final AsyncContext ac = request.startAsync();
        final ServletOutputStream sos = response.getOutputStream();
            if(pi.charAt(0)=='$'){
                String[] s = pi.substring(1).split("_");
                final Media d = t.getOMedia(Double.parseDouble(s[0]),s[1]);
                final String ls = d.getCt();
                sos.setWriteListener(new WriteListener(){                              
                    byte[] l = d.getM();
                    final ByteArrayInputStream bis=new ByteArrayInputStream(l);
                    @Override
                    public void onWritePossible(){
                        ac.getResponse().setContentType(ls);  
                        int c;
                        byte[] b = new byte[l.length];
                        try {
                            do{
                                c = bis.read(b);
                                sos.write(b, 0, c);
                            }
                            while(sos.isReady());
                        } catch (IOException ex) {
                            Logger.getLogger(MediaServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                   @Override
                   public void onError(Throwable t) {}
                });
        }            
        else{
            String[] s = pi.split("_");
            Media d = t.getMedia(s[0],s[1]);
            final byte[] l = d.getM();
            final String ls = d.getCt();
            sos.setWriteListener(new WriteListener(){
                final ByteArrayInputStream bis=new ByteArrayInputStream(l);
                @Override
                public void onWritePossible() throws IOException {
                    ac.getResponse().setContentType(ls);
                    int c;
                    byte[] b = new byte[l.length];
                    try {
                        do{
                            c = bis.read(b);
                            sos.write(b, 0, c);
                        }
                        while(sos.isReady());
                    } catch (IOException ex){
                        Logger.getLogger(MediaServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }    
                }
                @Override
                public void onError(Throwable t) {
                }
                
            });
        }ac.complete();
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
        final String pi = request.getPathInfo().substring(1);
        if(pi.charAt(0)=='$'){
            String[] s = pi.substring(1).split("_");
            Media d = t.getOMedia(Double.parseDouble(s[0]),s[1]);
            String fc = d.getCaption();
            if(fc!=null){
                response.setContentType("text/plain");
                response.getWriter().write(fc);
            }
        }            
        else{
            String[] s = pi.split("_");
            Media d = t.getMedia(s[0],s[1]);
            String fc = d.getCaption();  
            if(fc!=null){
                response.setContentType("text/plain");
                response.getWriter().print(fc);
            }
        }
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
