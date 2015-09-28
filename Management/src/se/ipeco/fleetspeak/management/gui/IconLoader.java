package se.ipeco.fleetspeak.management.gui;

import java.io.InputStream;
import java.util.HashMap;

import javafx.scene.image.Image;

public class IconLoader {

	private static HashMap<String, Image> images = new HashMap<String, Image>();
	
	public static Image loadImage(String name){
		Image im = images.get(name);
		if(im == null){
			InputStream in = IconLoader.class.getClassLoader().getResourceAsStream(name);
			System.out.println(in);
			if(in != null){
				im = new Image(in);
				if(im != null){
					images.put(name, im);
				}
			}
		}
		
		return im;
	}
}
