import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

	public static final long minecraft_server = 280458825L; // Beta1.2_01
	public static final long minecraft_servero = 2392702871L; // Beta1.2_01
																// processed w/
																// jarjar
	public static final long mysql = 3001390372L; // mysql connector
	public static final long jarjar = 233379283L; // jarjar decompiler
	public static final long rules = 2575805698L; // rules for jarjar (For
													// Beta1.2_01 obviously)
	public static final long VCRC = 1677797663L; // version.txt

	private static final String URL = "http://hackion.com/mirror/Minecraft/B1.2_01/";

	public static final Logger log = Logger.getLogger("Minecraft");

	// TODO
	// Change this.
	public static void main(String[] args) throws IOException {

		if (System.console() == null) {
			JFrame frame = new JFrame("VhMod");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent evt) {
					System.exit(0);
				}
			});
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.CENTER);
			frame.setLayout(layout);
			frame.getContentPane().add(new JLabel("This jar should be executed from a command processor."));
			frame.getContentPane().add(new JLabel("Command: \"java -jar VhMod-#.#.#.jar\""));
			frame.setSize(350, 90);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} else {

			if (!fileExists("version.txt")) {
				if (!fileExists("groups.txt")) {
					PrintWriter writer = new PrintWriter("version.txt", "UTF-8");
					writer.println("121-1");
					writer.close();
				}
			}

			if (!fileExists("plugins")) {
				File dir = new File("plugins");
				dir.mkdir();
				PrintWriter writer = new PrintWriter("plugins/readme.txt", "UTF-8");
				writer.println(
						"Place your plugins in here, then add the filename (WITHOUT the .jar) to plugins in server.properties like so:");
				writer.println("plugins=Plugin1,Plugin2,Plugin3,etc");
				writer.close();
			}

			if (!fileExists("mysql-connector-java-bin.jar")) {
				log("mysql-connector-java-bin.jar is missing, Downloading...");
				downloadFile(URL + "mysql-connector-java-bin.jar", "mysql-connector-java-bin.jar");
				checkCRC32("mysql-connector-java-bin.jar", mysql);
				log("Loading jarjar.jar...");
				dynamicLoadJar("mysql-connector-java-bin.jar");
				log("Finished downloading & loading mysql-connector-java-bin.jar.");
			}

			if (!fileExists("jarjar.jar")) {
				log("jarjar.jar is missing, Downloading...");
				downloadFile(URL + "jarjar.jar", "jarjar.jar");
				checkCRC32("jarjar.jar", jarjar);
				log("Loading jarjar.jar...");
				dynamicLoadJar("jarjar.jar");
				log("Finished downloading & loading jarjar.jar.");
			}

			if (!fileExists("rules.rules")) {
				log("rules.rules is missing, Downloading...");
				downloadFile(URL + "rules.rules", "rules.rules");
				checkCRC32("rules.rules", rules);
				log("Finished downloading rules.rules.");
			}

			if (!fileExists("minecraft_servero.jar")) {
				if (!fileExists("minecraft_server.jar")) {
					log("Vanilla server jar file is missing, Downloading...");
					downloadFile(URL + "minecraft_server.jar", "minecraft_server.jar");
					checkCRC32("minecraft_server.jar", minecraft_server);
					log("Finished downloading minecraft_server.jar.");
				} else
					log("Creating minecraft_servero.jar now...");

				try {
					com.tonicsystems.jarjar.Main.main(
							new String[] { "process", "rules.rules", "minecraft_server.jar", "minecraft_servero.jar" });
				} catch (Throwable t) {
					log.log(Level.SEVERE, null, t);
				}
				checkCRC32("minecraft_servero.jar", minecraft_servero);
				log("minecraft_servero.jar successfully created.");

				log("Loading minecraft server now...");
				dynamicLoadJar("minecraft_servero.jar");
			} else {
				checkCRC32("minecraft_servero.jar", minecraft_servero);
			}

			if (etc.getInstance().getDataSourceType().equalsIgnoreCase("mysql")) {
				checkCRC32("mysql-connector-java-bin.jar", mysql);
			}
			if (checkForUpdate()) {
				System.out.println("Update found.");
				// derp.
			}

			// My mod doesn't work with gui.
			try {
				net.minecraft.server.MinecraftServer.main(new String[] { "nogui" });
			} catch (Throwable t) {
				log.log(Level.SEVERE, null, t);
			}
			new DeadLockDetector();
		}
	}

	public static boolean fileExists(String filename) {
		return new File(filename).exists();
	}

	public static void checkCRC32(String fileName, long[] crcs) throws IOException {
		if (etc.getInstance().getTainted()) {
			return;
		}

		long checksum = getCRC32(fileName);
		for (long i : crcs) {
			if (i == checksum)
				return;
		}
		log("-----------------------------");
		log(fileName + " does not match checksum!");
		log("if you still want to run the server, delete version.txt to run the server in tainted mode.");
		log("This means some of your files are either corrupted,outdated or to new(minecraft got updated?).");
		log("You may also delete the file and the server will account for it.");
		log("-----------------------------");
		System.exit(0);

	}

	public static void checkCRC32(String fileName, long crc) throws IOException {
		if (etc.getInstance().getTainted()) {
			return;
		}

		long checksum = getCRC32(fileName);
		if (checksum != crc) {
			log("-----------------------------");
			log(fileName + " does not match checksum! Checksum found: " + checksum + ", required checksum: " + crc
					+ ".");
			log("This means some of your files are either corrupted,outdated or to new(minecraft got updated?).");
			log("If you still want to run the server, delete version.txt to run the server in tainted mode.");
			log("You may also delete the file and the server will account for it.");
			log("-----------------------------");
			System.exit(0);
		}
	}

	public static long getCRC32(String fileName) throws IOException {

		FileInputStream stream = new FileInputStream(fileName);
		CheckedInputStream cis = new CheckedInputStream(stream, new CRC32());
		byte[] buf = new byte[128];
		while (cis.read(buf) >= 0) {
		}

		long rt = cis.getChecksum().getValue();
		stream.close();
		cis.close();

		return rt;
	}

	public static void printCRC() throws IOException {
		log("Minecraft_server CRC32: \t" + getCRC32("minecraft_server.jar"));
		log("Minecraft_servero CRC32: \t" + getCRC32("minecraft_servero.jar"));
		log("Jarjar CRC32: \t\t\t" + getCRC32("jarjar.jar"));
		log("Rules CRC32: \t\t\t" + getCRC32("rules.rules"));
		log("Mysql CRC32: \t\t\t" + getCRC32("mysql-connector-java-bin.jar"));
	}

	public static void downloadFile(String website, String fileLocation) throws IOException {
		URL url = new URL(website);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(fileLocation);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}

	public static void log(String str) {
		System.out.println(str);
	}

	public static void dynamicLoadJar(String fileName) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { (new File(fileName)).toURI().toURL() });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}

	public static boolean checkForUpdate() {
		return false;
	}
}
