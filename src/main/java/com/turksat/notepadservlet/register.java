package com.turksat.notepadservlet;

import com.google.gson.Gson;
import com.turksat.notepadservlet.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class register extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email_regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
		String password_regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

		User user = new User();
		user.setErrorCode(1);

		if (req.getParameter("email").matches(email_regex)) {
			if (req.getParameter("password").matches(password_regex)) {
				try {
					Class.forName("org.postgresql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/notepad", "postgres", "pass");
					PreparedStatement pstmt = conn.prepareStatement("INSERT INTO project.\"user\" VALUES(?, ?, ?, ?, ?)");
					pstmt.setString(1, req.getParameter("email"));
					pstmt.setString(2, req.getParameter("name"));
					pstmt.setString(3, req.getParameter("surname"));
					pstmt.setString(4, getSHA(req.getParameter("password")));
					pstmt.setBoolean(5, Boolean.getBoolean(req.getParameter("check")));
					pstmt.executeUpdate();

					user.setErrorCode(0);
//					sendMail(req.getParameter("email"));
				} catch (ClassNotFoundException | NoSuchAlgorithmException | SQLException e) {
					if (e.toString().contains("duplicate")) {
						user.setErrorCode(3);
					}
				}
			} else {
				user.setErrorCode(2);
			}
		}

		insertLog(req.getRemoteAddr(), req.getParameter("email"), user.getErrorCode());
		PrintWriter out = resp.getWriter();
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
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
			pstmt.setString(3, "REGISTER");
			switch (errorCode) {
				case 0:
					pstmt.setString(4, "+ " + email + " registered successfully");
					break;
				case 1:
					pstmt.setString(4, "- " + email + " tried to register with invalid email");
					break;
				case 2:
					pstmt.setString(4, "- " + email + " tried to register with invalid password");
					break;
				case 3:
					pstmt.setString(4, "- " + email + " tried to register with the email that has already been registered");
					break;
			}
			pstmt.executeUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			throw new UnsupportedOperationException("register : insertLog");
		}
	}

	public void sendMail(String email) {
		try {
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.live.com");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.port", "587");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			properties.put("mail.smtp.port", "587");
			properties.put("mail.smtp.timeout", 1000);

			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("notepadapplication@outlook.com", "Deneme.123");
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("notepadapplication@outlook.com", "Notepad App"));
			message.setSubject("Registration");

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			String htmlText = "<font size=5><b>Your registration is completed. Welcome to Notepad App</b></font>";
			message.setContent(htmlText, "text/html");

			Transport.send(message);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
