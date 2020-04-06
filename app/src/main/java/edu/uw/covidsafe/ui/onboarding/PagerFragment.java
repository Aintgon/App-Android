package edu.uw.covidsafe.ui.onboarding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.covidsafe.R;

import edu.uw.covidsafe.utils.Constants;

public class PagerFragment extends Fragment {

    ViewPager viewPager;
    View view;
    OnboardingStateAdapter adapter;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("state","PAGER VIEW "+Constants.pageNumber);
        ((OnboardingActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().hide();

        view = inflater.inflate(R.layout.onboarding_story, container, false);
        viewPager = view.findViewById(R.id.pager);
        adapter = new OnboardingStateAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        if (Constants.pageNumber != -1) {
            viewPager.setCurrentItem(Constants.pageNumber);
        }
        return view;
    }
}
