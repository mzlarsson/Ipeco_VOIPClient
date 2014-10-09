package se.chalmers.fleetspeak;

import android.test.ActivityInstrumentationTestCase2;

import se.chalmers.fleetspeak.activities.StartActivity;

/**
 * Created following the instructions at https://developer.android.com/training/activity-testing/activity-basic-testing.html
 *
 * Created by Fridgeridge on 2014-10-02.
 */
public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {

    private StartActivity startActivity;


    public StartActivityTest() {
    super(StartActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        startActivity = getActivity();
            }


    public void testTemplateMethod(){
        assertEquals(3, 3);
    }

}
