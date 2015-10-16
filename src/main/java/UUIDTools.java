import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class UUIDTools {

	private static String UUID_Cache = "UUID.db";
	private static Map<String, String> UUIDCache = new HashMap<String, String>();

	public static void saveUUIDCache() {
		File file = new File(UUID_Cache);
		if (file.exists()) {
			file.delete();
		}

		FileWriter writer = null;

		try {
			file.createNewFile();
			writer = new FileWriter(file);
			Iterator<String> var4 = UUIDTools.UUIDCache.keySet().iterator();

			while (var4.hasNext()) {
				String e = (String) var4.next();
				String password = (String) UUIDTools.UUIDCache.get(e);
				writer.write(e + ":" + password + "\r\n");
				writer.flush();
			}

			writer.close();
		} catch (Exception var6) {
			var6.printStackTrace();
		}

	}

	public static void loadUUIDCache() {
		File file = new File(UUID_Cache);
		if (file.exists()) {
			Scanner reader = null;
			int lineCount = 0;

			try {
				reader = new Scanner(file);

				while (reader.hasNextLine()) {
					++lineCount;
					reader.nextLine();
				}
			} catch (Exception var19) {
				var19.printStackTrace();
			} finally {
				if (reader != null) {
					reader.close();
				}

			}

			if (lineCount > 150) {
				UUIDTools.UUIDCache = new HashMap<String, String>(lineCount + (int) ((double) lineCount * 0.4D));
			}

			try {
				reader = new Scanner(file);

				while (reader.hasNextLine()) {
					String e = reader.nextLine();
					if (e.contains(":")) {
						String[] in = e.split(":");
						if (in.length == 2) {
							String username = in[0];
							String UUID = in[1];
							UUIDTools.addUUID(username, UUID);
						}
					}
				}
			} catch (Exception var17) {
				var17.printStackTrace();
			} finally {
				if (reader != null) {
					reader.close();
				}

			}

		}
	}

	private static void addUUID(String username, String UUID) {
		if (UUIDTools.UUIDCache.containsKey(username)) {
			UUIDTools.UUIDCache.remove(username);
		}

		UUIDTools.UUIDCache.put(username, UUID);
		UUIDTools.saveUUIDCache();
	}

	/**
	 * Returns the UUID of a username.
	 * 
	 * @param username
	 *            Username to fetch ID of.
	 * @return Username's UUID
	 */
	public static String getUUID(String username) {
		return getRawUUID(getRawUUID(username));
	}

	/**
	 * Returns the UUID of a player.
	 * 
	 * @param player
	 *            Player to fetch the ID of.
	 * @return Username's UUID
	 */
	public static String getUUID(Player player) {
		return getRawUUID(player.getName());
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
		String UUID;
		if (UUIDCache.containsKey(username)) {
			UUID = (String) UUIDCache.get(username);
			return UUID;
		}

		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				// TODO
				String[] array = line.split("\"");
				addUUID(username, array[3]);
				return array[3];
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
