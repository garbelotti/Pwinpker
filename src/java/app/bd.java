/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Freak
 */
public class bd extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");

    }

    private boolean emailexistenoBanco(String param1) throws ClassNotFoundException {
        boolean retorno = false;
        Class.forName("com.mysql.jdbc.Driver");
        Connection bd = null;
        try {
            bd = DriverManager.getConnection("jdbc:mysql://localhost/pw", "root", "123456");
            PreparedStatement p = bd.prepareStatement("SELECT * FROM usuarios where email = ?");
            p.setString(1, param1);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                retorno = true;
            }
        } catch (SQLException ex) {
        }
        return retorno;
    }

    private int insereBanco(String nome, String email, String senha) throws ClassNotFoundException {
        int retorno = 0;
        Class.forName("com.mysql.jdbc.Driver");
        Connection bd = null;
        if (!emailexistenoBanco(email)) {
            try {
                bd = DriverManager.getConnection("jdbc:mysql://localhost/pw", "root", "123456");
                //PreparedStatement p = bd.prepareStatement("SELECT * FROM cidades " + param1 + ";");
                PreparedStatement p = bd.prepareStatement("insert into usuarios values(?,?,?,?)");
                p.setInt(1, 0);
                p.setString(2, nome);
                p.setString(3, senha);
                p.setString(4, email);
                retorno = p.executeUpdate();
            } catch (SQLException ex) {
                retorno = 5;
            }
        } else {
            return 99;
        }
        return retorno;
    }
    
    private boolean verificaSenha(String usuario, String senha) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection bd = null;
        try {
            bd = DriverManager.getConnection("jdbc:mysql://localhost/pw", "root", "123456");
            PreparedStatement p = bd.prepareStatement("SELECT * FROM usuarios where (email=?);");
            p.setString(1, usuario);
            ResultSet r = p.executeQuery();

            if (r.next()) {
                String pass = r.getString("senha");
                if (pass.equals(senha)) {
                    return true;
                }
            }
        } catch (SQLException ex) {

        }
        return false;
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
        try (PrintWriter out = response.getWriter()) {
        if (request.getSession().getAttribute("email")!=null){
            out.println("logado como: "+request.getSession().getAttribute("email"));
        }
        }
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
        //processRequest(request, response);
        try (PrintWriter out = response.getWriter()) {

            //login do usuario
            String log = request.getParameter("log");
            if (log != null){
                String usr = request.getParameter("usr");
                String psw = request.getParameter("psw");
                if (verificaSenha(usr, psw)) {
                    request.getSession().setAttribute("loged", "1");
                    request.getSession().setAttribute("email", usr);
                    doGet(request, response);
                } else {
                    out.println("usuario ou senha invalida");
                };
            }
            
            //cadastro de usuarios
            String c = request.getParameter("cad");
            if (c != null) {
                String nome = request.getParameter("nome");
                String email = request.getParameter("mail");
                String senha = request.getParameter("senha");
                int resultadoInsere = insereBanco(nome, email, senha);
                switch (resultadoInsere) {
                    case 1:
                        out.println("Cadastrado com sucesso");
                        break;
                    case 99:
                        out.println("Email existente");
                        break;
                    default:
                        out.println("erro: " + Integer.toString(resultadoInsere));
                        break;

                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(bd.class.getName()).log(Level.SEVERE, null, ex);
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
