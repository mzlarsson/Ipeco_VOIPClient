package se.chalmers.fleetspeak.test;

import java.util.Scanner;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.util.PasswordHash;


public class PasswordGenerator {

	public static void main(String[] args) {
		System.out.println("Write a password to generate the PasswordHash, or q to quit.");
		Scanner in = new Scanner(System.in);
		String pass;
		while(true) {
			pass = in.nextLine();
			if (pass.equals("q")) {
				break;
			} else {
				try {
					System.out.println(PasswordHash.createHash(pass));
				} catch (Exception e) {
					Logger.getLogger("Debug").warning("Could not create password hash.");
				}
			}
		}
		in.close();
	}

}
