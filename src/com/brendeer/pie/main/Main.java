package com.brendeer.pie.main;

import com.brendeer.pie.basePlugins.BasePlugins;
import com.brendeer.pie.basePlugins.ConverterPlugins;
import com.brendeer.pie.basePlugins.IOPlugins;
import com.brendeer.pie.basePlugins.ViewPlugins;
import com.brendeer.pie.core.FilterProgram;
import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.file.FileReader;
import com.brendeer.pie.gui.MainFrame;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * MAIN
 * @author Erik
 */
public class Main {

	public static void main(String[] args) {
		Install.installIfNeeded(); //first, if this program is not installed alreaady, do this please.

		//load all the plugins that exist
		BasePlugins.load();
		IOPlugins.load();
		ConverterPlugins.load();
		ViewPlugins.load();
		
		PluginContainer.loadExternalPlugins();

		//PluginContainer.testAllPlugins();
		System.out.println(PluginContainer.log);
		
		
		//try to open a file passed as argument to the application
		FilterProgram toOpen = new FilterProgram(null);
		
		for (String arg: args) {
			if (arg.endsWith(".pieml")) {
				File openFile = new File(arg);
				if (openFile.exists()) {
					toOpen = FileReader.decodeXML(openFile);
					break;
				}
			}
		}
		
		if (toOpen == null) {
			JOptionPane.showMessageDialog(null, "Cannot open file: Malformed .pieml", "PIE", JOptionPane.ERROR_MESSAGE);
			toOpen = new FilterProgram(null);
		}

		//display the editing window for that FilterProgram
		MainFrame editFrame = new MainFrame(toOpen, true);
		editFrame.setVisible(true);
	}
}
