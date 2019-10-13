import java.awt.List;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokemonApi {

	public static void main(String[] args) throws IOException {

		String rootURL = "https://pokeapi.co/api/v2/berry";

		String response = connectToApi(rootURL);

		JSONObject root = new JSONObject(response);

		JSONArray berryArray = arrayOfBerries(rootURL);

		ArrayList<String> berryURLs = listOfBerryURLs(berryArray);

		ArrayList<JSONObject> berryFactory = listOfBerries(berryURLs);

		int shortestGrowthTime = berryFactory.get(0).getInt("growth_time");
		int largestSize = berryFactory.get(0).getInt("size");
		String berryWithShortestGrowthTimeAndLargestSize = berryFactory.get(0).getString("name");

		for (int i = 0; i < berryFactory.size(); i++) {

			if (berryFactory.get(i).getInt("growth_time") < shortestGrowthTime
					&& berryFactory.get(i).getInt("size") > largestSize) {
				shortestGrowthTime = berryFactory.get(i).getInt("growth_time");
				largestSize = berryFactory.get(i).getInt("size");
				berryWithShortestGrowthTimeAndLargestSize = berryFactory.get(i).getString("name");

			}
		}
		System.out.println("Largest Berry you can grow in the shortest time is "
				+ berryWithShortestGrowthTimeAndLargestSize.toUpperCase() + " with the growth time of "
				+ shortestGrowthTime + " and size of " + largestSize + ".");

	}

	private static JSONArray arrayOfBerries(String rootURL) throws MalformedURLException, IOException {
		String berryRoot = connectToApi(rootURL + "?offset=0&limit=64");
		JSONObject allBerries = new JSONObject(berryRoot);
		JSONArray berryArray = (JSONArray) allBerries.get("results");
		return berryArray;
	}

	private static ArrayList<String> listOfBerryURLs(JSONArray berryArray) {
		ArrayList<String> berryURLs = new ArrayList<String>();

		berryArray.forEach(actor -> berryURLs.add(((JSONObject) actor).getString("url")));
		return berryURLs;
	}

	private static ArrayList<JSONObject> listOfBerries(ArrayList<String> berryURLs) {
		ArrayList<JSONObject> berryFactory = new ArrayList<JSONObject>();
		berryURLs.forEach(berry -> {
			try {
				berryFactory.add(new JSONObject(connectToApi(berry)));
			} catch (JSONException | IOException e) {

				e.printStackTrace();
			}
		});
		return berryFactory;
	}

	private static String connectToApi(String rootURL) throws MalformedURLException, IOException {
		URL request = new URL(rootURL);
		InputStream openStream = request.openStream();
		String response = IOUtils.toString(openStream);
		return response;
	}
}
