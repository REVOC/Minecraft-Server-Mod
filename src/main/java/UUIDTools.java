import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UUIDTools {

	/**
	 * Returns the UUID of a username.
	 * 
	 * @param username
	 *            Username to fetch ID of.
	 * @return Username's UUID
	 */
	public static String getUUID(String username) {
		String temp = getRawUUID(username);
		if (temp != null) {
			String[] array = temp.split("\"");
			return array[3];
		}
		return null;
	}

	/**
	 * Returns the UUID of a player.
	 * 
	 * @param player
	 *            Player to fetch the ID of.
	 * @return Username's UUID
	 */
	public static String getUUID(Player player) {
		String temp = getRawUUID(player.getName());
		if (temp != null) {
			String[] array = temp.split("\"");
			return array[3];
		}
		return null;
	}

	/**
	 * String hack thing so I don't have to work with JSON.
	 * 
	 * @param username
	 *            Username to fetch ID of.
	 * @return a UUID.
	 */
	public static String getRawUUID(String username) {
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line = null;

		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				return line;
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
			}
		}
		return null;
	}
}
