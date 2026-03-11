package com.dongyang.foodpage.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.dongyang.foodpage.dao.IngredientDAO;
import com.dongyang.foodpage.dto.IngredientDTO;

@WebServlet("/ingredients")
public class IngredientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            IngredientDAO dao = new IngredientDAO();
            List<IngredientDTO> ingredientList = dao.getAll();

            request.setAttribute("ingredientList", ingredientList);
            request.getRequestDispatcher("/food/refrigerator.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}