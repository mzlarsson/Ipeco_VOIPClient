package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.util.Utils;

/**
 * A fragments that models the interaction for the user when user tries to disconnect from the
 * server
 * Created by David Gustafsson on 12/03/2015.
 */
public class DisconnectFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set up the theme for the fragment with the contextThemeWrapper
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.disconnect_confirm_fragment, container, false);
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID() ==
                R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));


        Button proceed = (Button) view.findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).disconnect();
                ((MainActivity) getActivity()).setFragment(FragmentHandler.FragmentName.START);
            }
        });
        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFragment(FragmentHandler.FragmentName.JOIN);
            }
        });
        setHasOptionsMenu(true);
        return view;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.day_night_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case  R.id.day_night_toggle:
                Utils.changeTheme((MainActivity) this.getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
