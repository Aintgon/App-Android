package edu.uw.covidsafe.ui.health;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import com.google.android.material.tabs.TabLayout;

public class HealthFragment extends Fragment {

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_main, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.health_header_text)));
        Button reportButton = (Button)view.findViewById(R.id.button3);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                tx.replace(R.id.fragment_container, Constants.SubmitFragment).commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.HealthFragment = this;
        Constants.ReportFragmentState = this;
    }
}
