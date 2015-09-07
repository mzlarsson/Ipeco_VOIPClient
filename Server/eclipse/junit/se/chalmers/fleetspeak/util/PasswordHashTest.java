package se.chalmers.fleetspeak.util;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Before;
import org.junit.Test;

public class PasswordHashTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSamePassword() {
		try {
            // Same password should give different strings as hashed passwords.
			String[] str = new String[10];
            for(int i = 0; i < str.length; i++) {
                str[i] = PasswordHash.createHash("p\r\nassw0Rd!");
            }
            for(int i=0; i<(str.length-1); i++) {
            	for(int j=i+1; j<str.length; j++) {
            		assertNotEquals(str[i], str[j]);
            	}
            }
		} catch (InvalidKeySpecException e) {
			fail("Exception caught");
		} catch (NoSuchAlgorithmException e) {
			fail("Exception caught");
		}
	}
	
	@Test
	public void generalTest() {
		try {
	        // Test password validation
	        for(int i = 0; i < 100; i++) {
	            String password = ""+i;
	            String hash = PasswordHash.createHash(password);
	            String secondHash = PasswordHash.createHash(password);
	            if(hash.equals(secondHash)) {
	            	fail("FAILURE: TWO HASHES ARE EQUAL!");
	            }
	            String wrongPassword = ""+(i+1);
	            if(PasswordHash.validatePassword(wrongPassword, hash)) {
	                fail("FAILURE: WRONG PASSWORD ACCEPTED!");
	            }
	            if(!PasswordHash.validatePassword(password, hash)) {
	                fail("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
	            }
	        }
        } catch (InvalidKeySpecException e) {
			fail("Exception caught");
		} catch (NoSuchAlgorithmException e) {
			fail("Exception caught");
		}
    }
}
