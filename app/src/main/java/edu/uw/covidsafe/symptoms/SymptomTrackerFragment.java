package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;

import com.example.covidsafe.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SymptomTrackerFragment extends Fragment {

    View view;
    SymptomHistoryRecyclerViewAdapter symptomHistoryAdapter;
    boolean symptomDbChanged = false;
    List<SymptomsRecord> changedRecords;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("health","symptom tracker fragment oncreate");
        view = inflater.inflate(R.layout.health_symptom_tracker, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.health_header_text)));

        RecyclerView rview = view.findViewById(R.id.recyclerViewSymptomHistory);
        symptomHistoryAdapter = new SymptomHistoryRecyclerViewAdapter(getActivity(),getActivity(), view);
        rview.setAdapter(symptomHistoryAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.e("symptom","init constants cal");
        initCal();

        SymptomDbModel smodel = ViewModelProviders.of(getActivity()).get(SymptomDbModel.class);
        smodel.getAllSorted().observe(getActivity(), new Observer<List<SymptomsRecord>>() {
            @Override
            public void onChanged(List<SymptomsRecord> symptomRecords) {
                //something in db has changed, update
                symptomDbChanged = true;
                changedRecords = symptomRecords;
                Log.e("symptom", "symptomtracker - symptom list changed");
                if (Constants.CurrentFragment.toString().toLowerCase().contains("symptom")) {
                    Log.e("symptom", "symptomtracker - symptom list changing");
                    updateFeaturedDate();
                    symptomDbChanged = false;
                }
            }
        });

        return view;
    }

    public void initCal() {
        Constants.cal = view.findViewById(R.id.calendarView);
        /////////////////////////////////////////////////////////////////
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -Constants.DefaultInfectionWindowInDays);
        /////////////////////////////////////////////////////////////////
        Log.e("health","minimum "+calendar.get(Calendar.YEAR)+","+(calendar.get(Calendar.MONTH)+1)+","+calendar.get(Calendar.DAY_OF_MONTH));
        CalendarDay d1 = CalendarDay.from(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
        Constants.cal.setSelectedDate(CalendarDay.today());

        Constants.cal.state().edit()
                .setFirstDayOfWeek(DayOfWeek.SUNDAY)
                .setMinimumDate(d1)
                .setMaximumDate(CalendarDay.today())
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        Constants.cal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Log.e("symptom","on date selected "+date.toString());
                updateFeaturedDate();
            }
        });
    }

    public void updateFeaturedDate() {
        Log.e("symptom","update featured date");
        CalendarDay calDay = Constants.cal.getSelectedDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            String dateStr = calDay.getYear() + "/" + calDay.getMonth() + "/" + calDay.getDay();
            Date dd = format.parse(dateStr);
            Log.e("symptom","updating "+dateStr);
            SymptomUtils.updateTodaysLogs(view, changedRecords, getContext(),getActivity(), dd, "tracker");
            symptomHistoryAdapter.setRecords(changedRecords, view);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("health","symptom tracker fragment onresume");

        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
//        Constants.CurrentFragment = this;
        if (symptomDbChanged) {
            Log.e("symptoms","db changed ");
            updateFeaturedDate();
            symptomDbChanged = false;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
    }
}
