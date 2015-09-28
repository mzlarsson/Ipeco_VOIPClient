package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DocMarkupUtility {

	public static void main(String[] args){
		//Open file
		File docSource = new File("../../Documents/commands.txt");
		Scanner sc = null;
		try {
			sc = new Scanner(docSource);
			sc.nextLine();			//Skip first line (contains only info)
		} catch (FileNotFoundException e) {
			System.out.println("Could not find doc file. Exiting...");
			System.exit(1);
		}
		
		//Parse
		StringBuffer out = new StringBuffer();
		out.append("<html>\n");
		out.append("\t<head>\n\t\t<link href='cmd_formatted.css' type='text/css' rel='stylesheet'/>\n\t\t<title>Commands</title>\n\t</head>\n");
		out.append("\t<body>\n");
		out.append("\t\t<table id = 'dataTable'>\n");
		String input;
		boolean info = true, cmdContent = false;
		int counter = 0;
		int maxArguments = 5, currentArguments = 0;
		while(sc.hasNextLine()){
			input = sc.nextLine();
			if(input.trim().length()>0 && !input.startsWith("//")){
				if(info){
					out.append("\t\t\t<tr class='"+(counter%2==0?"light":"dark")+"'>\n\t\t\t\t<td class='info'>").append(input).append("</td>\n");
					info = false;
				}else{
					if(input.trim().equals("{")){
						cmdContent = true;
					}else if(input.trim().equals("}")){
						out.append("\t\t\t\t<td colspan='"+(maxArguments-currentArguments)+"'>&nbsp;</td>\n");
						out.append("\t\t\t</tr>\n");
						cmdContent = false;
						info = true;
						counter++;
						currentArguments = 0;
					}else if(cmdContent){
						String[] cmd = input.split(":");
						cmd[0] = cleanUp(cmd[0]);
						cmd[1] = cleanUp(cmd[1]);
						out.append("\t\t\t\t<td>");
						out.append("<b>").append(cmd[0]).append("</b>").append("<br>").append(cmd[1]);
						out.append("</td>\n");
						currentArguments++;
					}
				}
			}
		}
		out.append("\t\t</table>\n\t</body>\n</html>");
		sc.close();
		
		//Write to file
		File docOut = new File("../../Documents/command_formatted.html");
		try {
			FileWriter writer = new FileWriter(docOut);
			writer.write(out.toString());
			writer.close();
		} catch (IOException e) {
			System.out.println("Could not print file.");
		}
	}
	
	private static String cleanUp(String s){
		if(s.startsWith("\"")){
			s = s.substring(1);
		}
		if(s.endsWith(",")){
			s = s.substring(0, s.length()-1);
		}
		if(s.endsWith("\"")){
			s = s.substring(0, s.length()-1);
		}
		
		return s;
	}
}
