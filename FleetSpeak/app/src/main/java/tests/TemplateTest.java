package tests;

import android.test.InstrumentationTestCase;

/**
 * A simple test template example from http://rexstjohn.com/unit-testing-with-android-studio/
 *
 * Remember to fix run configurations 
 *
 * Created by Fridgeridge on 2014-10-01.
 */
public class TemplateTest extends InstrumentationTestCase {


    //**IMPORTANT**
    //All methods must begin with 'test' in order for Android Studio to recognize them as testmethods
    public void test_OhShitSon() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);

    }



}


