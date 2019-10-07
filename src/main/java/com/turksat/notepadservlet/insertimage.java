package com.turksat.notepadservlet;

import com.google.gson.Gson;
import com.turksat.notepadservlet.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class insertimage extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = new User();
		user.setErrorCode(1);

		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/notepad", "postgres", "pass");
			PreparedStatement pstmt = conn.prepareStatement("UPDATE project.note SET image = ? WHERE id = ?");

			if (req.getParameter("image").equals("")) {
				pstmt.setBytes(1, null);
			} else {
				pstmt.setBytes(1, Base64.getUrlDecoder().decode(req.getParameter("image")));
			}
			pstmt.setString(2, req.getParameter("id"));

			if (pstmt.executeUpdate() == 1) {
				user.setErrorCode(0);
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e);
			user.setErrorCode(1);
		}

		insertLog(req.getRemoteAddr(), req.getParameter("id"), user.getErrorCode());
		PrintWriter out = resp.getWriter();
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		out.print(new Gson().toJson(user));
		out.flush();
	}

	public void insertLog(String ip, String id, int errorCode) {
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/notepad", "postgres", "pass");
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO project.log VALUES(?, ?, ?, ?)");
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(2, ip);
			pstmt.setString(3, "INSERT IMAGE");
			switch (errorCode) {
				case 0:
					pstmt.setString(4, "+ " + "Image inserted: " + id);
					break;
				case 1:
					pstmt.setString(4, "- " + "Failed to insert image: " + id);
					break;
			}
			pstmt.executeUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			throw new UnsupportedOperationException("insertimage : insertLog");
		}
	}
}
