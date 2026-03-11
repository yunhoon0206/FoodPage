package com.dongyang.foodpage.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.dongyang.foodpage.dao.FridgeDAO;
import com.dongyang.foodpage.dao.IngredientDAO;
import com.dongyang.foodpage.dto.FridgeItemDTO;
import com.dongyang.foodpage.dto.IngredientDTO;

@WebServlet("/fridge")
public class FridgeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute("id");

        if (memberId == null) {
            response.sendRedirect(request.getContextPath() + "/loginForm.do");
            return;
        }

        List<FridgeItemDTO> fridgeList = List.of(); 
        List<IngredientDTO> ingredientList = List.of(); 

        try {
            FridgeDAO fridgeDAO = new FridgeDAO();
            IngredientDAO ingredientDAO = new IngredientDAO();

            ingredientList = ingredientDAO.getAll();
            fridgeList = fridgeDAO.getFridgeList(memberId);

            request.setAttribute("memberId", memberId);
            request.setAttribute("fridgeList", fridgeList);
            request.setAttribute("ingredientList", ingredientList);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/food/refrigerator.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("DB 접속 또는 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute("id");

        if (memberId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

        String action = request.getParameter("action");
        int itemId = Integer.parseInt(request.getParameter("itemId"));

        try {
            FridgeDAO fridgeDAO = new FridgeDAO();

            if ("delete".equals(action)) {
                fridgeDAO.delete(memberId, itemId);

            } else {
                float quantity = Float.parseFloat(request.getParameter("quantity")); // 소수점 대응

                if ("add".equals(action)) {
                    String note = request.getParameter("note");
                    String expiryDateStr = request.getParameter("expiryDate");
                    Date expiryDate = (expiryDateStr != null && !expiryDateStr.isEmpty())
                            ? Date.valueOf(expiryDateStr) : null;
                    fridgeDAO.addOrUpdate(memberId, itemId, quantity, expiryDate, note);

                } else if ("decrease".equals(action)) {
                    float changeQty = -quantity;
                    fridgeDAO.updateQuantity(memberId, itemId, changeQty);
                    FridgeItemDTO currentItem = fridgeDAO.getByMemberAndItem(memberId, itemId);
                    if (currentItem != null && currentItem.getQuantity() <= 0) {
                        fridgeDAO.delete(memberId, itemId);
                    }
                }
            }

            response.sendRedirect(request.getContextPath() + "/fridge");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("DB 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
