package jk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jk.weather.WeatherApiResponse;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

public class Main
{
	public static final String API_KEY = System.getenv("apiKey");

	private static final ArrayList<String> locations = new ArrayList<>();
	private static boolean isRunning = true;
	private static Timer timer = new Timer();
	private static LinkedBlockingQueue<String> waitingJsonMessages = new LinkedBlockingQueue<>();
	private static Thread consumer;

	public static void main(String[] args)
	{
		setInitialLocations();

		GetWeatherTask getWeatherTask1 = new GetWeatherTask(waitingJsonMessages, locations);
		timer.scheduleAtFixedRate(getWeatherTask1, 0, 10 * 1000);

		ObjectMapper mapper = new ObjectMapper();
		consumer = new Thread(() -> {
			while (isRunning)
			{
				try
				{
					String jsonWeather = waitingJsonMessages.take();
					WeatherApiResponse weatherApiResponse = mapper.readValue(jsonWeather, WeatherApiResponse.class);

					System.out.println(weatherApiResponse.name + ", temp: " + weatherApiResponse.main.temp);

				} catch (InterruptedException | JsonProcessingException e)
				{
					break;
				}
			}
		});

		consumer.start();

		userInputLoop();
	}

	private static void setInitialLocations()
	{
		locations.add("Krakow");
		locations.add("Warsaw");
		locations.add("Gdansk");
	}

	private static void userInputLoop()
	{
		try (Scanner scanner = new Scanner(System.in))
		{
			while (true)
			{
				System.out.println("1. Add location\t2. Remove location\n3. Print locations\tq. quit");
				String line = scanner.nextLine();

				switch (line)
				{
					case "1":
						System.out.println("Add location:");
						line = scanner.nextLine();
						addLocation(line);
						break;
					case "2":
						System.out.println("Remove location:");
						line = scanner.nextLine();
						removeLocation(line);
						break;
					case "3":
						synchronized (locations)
						{
							System.out.println(String.join(" ", locations));
						}
						break;

					case "q":
						stop();
						return;
				}
			}
		}
	}

	private static void removeLocation(String location)
	{
		synchronized (locations)
		{
			locations.remove(location);
		}
	}

	private static void addLocation(String location)
	{
		synchronized (locations)
		{
			locations.add(location);
		}
	}

	private static void stop()
	{
		isRunning = false;
		timer.cancel();

		consumer.interrupt();
	}
}
