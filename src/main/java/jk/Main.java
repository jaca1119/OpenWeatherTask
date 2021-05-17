package jk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jk.weather.WeatherApiResponse;

import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

public class Main
{
	public static final String API_KEY = System.getenv("apiKey");

	public static void main(String[] args)
	{
		LinkedBlockingQueue<String> waitingJsonMessages = new LinkedBlockingQueue<>();

		GetWeatherTask getWeatherTask1 = new GetWeatherTask(waitingJsonMessages, "Krakow");
		GetWeatherTask getWeatherTask2 = new GetWeatherTask(waitingJsonMessages, "Warsaw");
		GetWeatherTask getWeatherTask3 = new GetWeatherTask(waitingJsonMessages, "Gdansk");

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(getWeatherTask1, 0, 1000);
		timer.scheduleAtFixedRate(getWeatherTask2, 0, 2 * 1000);
		timer.scheduleAtFixedRate(getWeatherTask3, 0, 3 * 1000);

		Thread consumer = new Thread(() -> {
			while (true)
			{
				try
				{
					String jsonWeather = waitingJsonMessages.take();
					ObjectMapper mapper = new ObjectMapper();
					WeatherApiResponse weatherApiResponse = mapper.readValue(jsonWeather, WeatherApiResponse.class);

					System.out.println(weatherApiResponse.name + ", temp: " + weatherApiResponse.main.temp);

				} catch (InterruptedException | JsonProcessingException e)
				{
					e.printStackTrace();
				}
			}
		});

		consumer.start();
	}
}
