package com.turksat.notepadservlet;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class push extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			final String apiKey = "AAAATPAH1vY:APA91bFrt-csMsLmBhBcxpqal7vgmS-doB7BPtnoeh-C5omG3ZrV2v9gctwOn9Q2WnkGiDkFzZGyhCkO_E-gB9yCJtbxajzRgSIGGkoerWGfzhCcbiCP_RpPbW6smC2pqRt0kaLxt_8_";
			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "key=" + apiKey);

			JSONObject input = new JSONObject();
			input.put("to", "dhA6G_5uVAQ:APA91bHlTfMGbCpzhWWnnFx8T2ZoMVvklieMeUIKDweS70lCpZUSrSEy5f8vJPx1pZ9wtnuz6DE_DFL2qlh_owQkfhCAzpg6IsadvK7iCaTcbxcvKGkToGeFlJwu27u-lNsO-11kme5z");

			JSONObject notification = new JSONObject();
			notification.put("title", "Web Service");
			notification.put("body", "Push Notification Successful");
			notification.put("android_channel_id", "MyNotifications");
			notification.put("icon", "notification_icon");
			notification.put("color", "#08e8de");
			input.put("notification", notification);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(input.toString().getBytes("UTF-8"));
				os.flush();
			}

			int responseCode = conn.getResponseCode();
			System.out.println("Sending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + input);
			System.out.println("Response Code : " + responseCode);

			StringBuilder response;
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String inputLine;
				response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}

			PrintWriter out = resp.getWriter();
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			out.print(new Gson().toJson(response));
			out.flush();
		} catch (IOException | JSONException e) {
			throw new UnsupportedOperationException("push : doGet");
		}
	}

}
