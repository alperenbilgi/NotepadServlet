package com.turksat.notepadservlet;

import com.google.gson.Gson;
import com.turksat.notepadservlet.model.Note;
import com.turksat.notepadservlet.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class login extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email_regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
		String password_regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

		User user = new User();
		user.setErrorCode(1);

		if (req.getParameter("email").matches(email_regex) && req.getParameter("password").matches(password_regex)) {
			try {
				Class.forName("org.postgresql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/notepad", "postgres", "pass");
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM project.\"user\" WHERE email = ?");
				pstmt.setString(1, req.getParameter("email"));
				ResultSet res = pstmt.executeQuery();

				if (res.next() && res.getString("password").equals(getSHA(req.getParameter("password")))) {
					user.setEmail(res.getString("email"));
					user.setName(res.getString("name"));
					user.setSurname(res.getString("surname"));

					pstmt = conn.prepareStatement("SELECT * FROM project.note WHERE user_email = ?");
					pstmt.setString(1, req.getParameter("email"));
					res = pstmt.executeQuery();

					List noteList = new ArrayList();

					while (res.next()) {
						Note note = new Note();
						note.setId(res.getString("id"));
						note.setUser_email(res.getString("user_email"));
						note.setSubject(res.getString("subject"));
						note.setText(res.getString("text"));
						note.setPriority(res.getInt("priority"));
						note.setReminder(res.getTimestamp("reminder"));
						if (res.getArray("coordinate") != null) {
							note.setCoordinate((String[]) res.getArray("coordinate").getArray());
						}
						note.setImage(res.getBytes("image"));
						noteList.add(note);
					}

					if (noteList.isEmpty()) {
						noteList = null;
					}

					user.setNotes(noteList);
					user.setErrorCode(0);
				}
			} catch (ClassNotFoundException | NoSuchAlgorithmException | SQLException e) {
				user.setErrorCode(1);
			}
		}

		insertLog(req.getRemoteAddr(), req.getParameter("email"), user.getErrorCode());

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

		PrintWriter out = resp.getWriter();
		out.print(new Gson().toJson(user));
		out.flush();
	}

	public static String getSHA(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		BigInteger number = new BigInteger(1, md.digest(input.getBytes(StandardCharsets.UTF_8)));
		StringBuilder hexString = new StringBuilder(number.toString(16));

		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}

	public void insertLog(String ip, String email, int errorCode) {
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/notepad", "postgres", "pass");
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO project.log VALUES(?, ?, ?, ?)");
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(2, ip);
			pstmt.setString(3, "LOGIN");
			switch (errorCode) {
				case 0:
					pstmt.setString(4, "+ " + email + " logged in");
					break;
				case 1:
					pstmt.setString(4, "- " + email + " tried to log in with invalid email or password");
					break;
			}
			pstmt.executeUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			throw new UnsupportedOperationException("login : insertLog");
		}
	}
}
