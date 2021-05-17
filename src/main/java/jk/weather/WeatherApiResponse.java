package jk.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 *
 * @see <a href="https://openweathermap.org/current#current_JSON">https://openweathermap.org/current#current_JSON</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse
{
	public MainTemp main;
	public Coord coord;
	public List<Weather> weather;
	public String base;
	public int visibility;
	public Wind wind;
	public Clouds clouds;
	public int dt;
	public Sys sys;
	public int timezone;
	public int id;
	public String name;
	public int cod;
}
