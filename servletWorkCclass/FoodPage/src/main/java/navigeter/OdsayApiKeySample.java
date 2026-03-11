package navigeter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class OdsayApiKeySample
{
	private final String apiKey = "nPn/2rY6HOCTlTwwJ1XtSJ7GanbDX9ADcpYhd1r3EAE";
	
	// start position -> e position
	public String Search(float sx, float sy, float ex, float ey)
	{
		String result = "";
		try
		{
		String urlInfo = "https://api.odsay.com/v1/api/searchPubTransPathT?"
	            + "SX=" + String.valueOf(sx)
	            + "&SY=" + String.valueOf(sy)
	            + "&EX=" + String.valueOf(ex)
	            + "&EY=" + String.valueOf(ey)
	            + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
		
		System.out.println(urlInfo);

		// http 연결
				URL url = new URL(urlInfo);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-type", "application/json");

				BufferedReader bufferedReader = 
					new BufferedReader(new InputStreamReader(conn.getInputStream()));

				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
				bufferedReader.close();
				conn.disconnect();

				// 결과 출력
				System.out.println(result = sb.toString());
		}
		catch(IOException e) { e.printStackTrace(); }
		
		return result;
	}
}