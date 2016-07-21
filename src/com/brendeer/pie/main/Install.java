package com.brendeer.pie.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * This class manages to install this whole application on the system
 * @author Erik
 */
public class Install {

	/**
	 * "Install" the program
	 *
	 * @return wether this program needed to install itself
	 */
	public static boolean installIfNeeded() {
		BufferedImage myIcon = null;
		try {
			myIcon = ImageIO.read(Install.class.getResourceAsStream("/com/brendeer/pie/main/smallicon.png"));
		} catch (IOException ex) {

		}

		try {
			if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
				error("WARNING: NON-Windows-OS detected! Installation may not work!");
			}

			File appData = new File(System.getenv("APPDATA"));
			File myFolder = new File(appData, "brendeer.pie");
			if (myFolder.exists()) {
				return false;
			}
			myFolder.mkdirs();

			//copy the programs jar into the folder
			File me = new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			if (me.isFile() && me.getName().endsWith(".jar")) {
				File installJar = new File(myFolder, "pie.jar");
				Files.copy(me.toPath(), installJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				error("Not starting from a jar, installation can't go like this!");
			}

			//create a batch file that starts the program
			File startbat = new File(myFolder, "start.bat");
			List<String> batLines = new ArrayList<>(1);
			batLines.add("java -jar %appdata%/brendeer.pie/pie.jar %*");
			Files.write(startbat.toPath(), batLines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);

			//now copy the starting exe
			//the only purpose of the exe is to call start.bat and have a nice icon
			InputStream exeStream = Install.class.getResourceAsStream("/com/brendeer/pie/main/pie.exe");
			if (exeStream == null) {
				error("No exe found!\nProgram will exit.");
				return false;
			}
			File exeFile = new File(myFolder, "pie.exe");
			Files.copy(exeStream, exeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			//also the debug exe
			exeStream = Install.class.getResourceAsStream("/com/brendeer/pie/main/pieDebug.exe");
			if (exeStream == null) {
				error("No debug exe found!");
			} else {
				exeFile = new File(myFolder, "pieDebug.exe");
				Files.copy(exeStream, exeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			//copy the sample .pieml to the desktop
			File desktop = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory();
			File exampleFolder = new File(desktop, "example pie");
			exampleFolder.mkdir();
			createExampleProject(exampleFolder);

			//show a nice message and exit
			String msg = "Installation Finished!\n"
					+ "\n"
					+ "The Progressive Image Editor (or as we like to \n"
					+ "call it - the PIE) is ready for you!\n"
					+ "\n"
					+ "In order to make it perfect, you just have to\n"
					+ "do one little last step:\n"
					+ "We've created an example pie project on your\n"
					+ "destop. \n"
					+ " => Right click the project.pieml\n"
					+ " => open with\n"
					+ " => choose a program \n"
					+ " => %AppData%/brendeer.pie/pie.exe  \n"
					+ " => set as default\n"
					+ "\n"
					+ "Thanks for choosing this program!\n"
					+ "\n"
					+ "Erik Brendel - CEO";
			JOptionPane.showMessageDialog(null, msg, "ProgressiveImageEditor installation", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(myIcon));

			//todo some manual file association
			//todo maybe create a desktop shortcut of the application
			System.exit(0);

		} catch (Exception ex) {
			error("Error in installation. See stack trace for more info");
			ex.printStackTrace();
		}
		return false;

	}

	/**
	 * create a small set of example data inside a given directory
	 *
	 * @param container the folder to paste all these files to
	 * @throws java.io.IOException
	 */
	public static void createExampleProject(File container) throws IOException {
		InputStream stream;
		File file;
		for (String name : new String[]{"background.png", "colors.png", "pieDiagramIcon.png", "project.pieml"}) {
			stream = Install.class.getResourceAsStream("/com/brendeer/pie/main/example/" + name);
			file = new File(container, name);
			Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static void error(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error in installation", JOptionPane.ERROR_MESSAGE);
	}
}
