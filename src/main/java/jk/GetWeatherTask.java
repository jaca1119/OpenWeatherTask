package jk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class GetWeatherTask extends TimerTask
{
	private final LinkedBlockingQueue<String> waitingJsonMessages;
	private final String location;

	public GetWeatherTask(LinkedBlockingQueue<String> waitingJsonMessages, String location)
	{

		this.waitingJsonMessages = waitingJsonMessages;
		this.location = location;
	}

	@Override
	public void run()
	{
		new Thread(this::getWeatherForLocation).start();
	}

	private void getWeatherForLocation()
	{
		try
		{
			URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + location + "&units=metric" + "&appid=" + Main.API_KEY);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			String resp = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining());
			System.out.println("Api returned:" + resp);

			waitingJsonMessages.put(resp);
		} catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
