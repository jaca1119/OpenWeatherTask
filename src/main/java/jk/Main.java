package jk;

import com.fasterxml.jackson.databind.ObjectMapper;
import jk.weather.WeatherApiResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main
{
	private static final String apiKey = System.getenv("apiKey");

	public static void main(String[] args)
	{
		//String location = "Krakow";

		new Thread(() -> getWeatherForLocation("Krakow")).start();

		new Thread(() -> getWeatherForLocation("Warszawa")).start();

	}

	private static void getWeatherForLocation(String location)
	{
		try
		{
			URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + location + "&units=metric" + "&appid=" + apiKey);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			//String resp = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining());
			ObjectMapper mapper = new ObjectMapper();
			WeatherApiResponse weatherApiResponse = mapper.readValue(urlConnection.getInputStream(), WeatherApiResponse.class);
			System.out.println(weatherApiResponse.main.temp);

			Thread.sleep(2000);
		} catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
