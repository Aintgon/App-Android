package edu.uw.covidsafe.ui.faq;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;

import com.example.covidsafe.R;

import java.util.ArrayList;
import java.util.List;

public class FaqFragment extends Fragment {

    ExpandableListView lv1;
    ExpandableListView lv2;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        Constants.menu.findItem(R.id.mybutton).setVisible(false);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.help_header_text)));

         lv1 = view.findViewById(R.id.faq);

        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        questions.add(getString(R.string.lipsum5));
        answers.add(getString(R.string.lipsum4));
        questions.add(getString(R.string.lipsum5));
        answers.add(getString(R.string.lipsum4));
        questions.add(getString(R.string.lipsum6));
        answers.add(getString(R.string.lipsum4));

        FaqListAdapter adapter = new FaqListAdapter(questions, answers);
        lv1.setAdapter(adapter);

        //////////////////////////////////////////////////////////////////

        lv2 = view.findViewById(R.id.faq2);

        List<String> questions2 = new ArrayList<>();
        List<String> answers2 = new ArrayList<>();
        questions2.add(getString(R.string.lipsum5));
        answers2.add(getString(R.string.lipsum4));
        questions2.add(getString(R.string.lipsum5));
        answers2.add(getString(R.string.lipsum4));
        questions2.add(getString(R.string.lipsum6));
        answers2.add(getString(R.string.lipsum4));

        FaqListAdapter adapter2 = new FaqListAdapter(questions2, answers2);
        lv2.setAdapter(adapter2);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter resAdapter = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        recyclerView.setAdapter(resAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                view.getHeight(); //height is ready

                setExpandableListViewHeight(lv1, -1);
                lv1.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int position, long id) {
                        setExpandableListViewHeight(parent, position);
                        return false;
                    }
                });
                setExpandableListViewHeight(lv2, -1);
                lv2.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int position, long id) {
                        setExpandableListViewHeight(parent, position);
                        return false;
                    }
                });
                lv1.setIndicatorBoundsRelative(lv1.getWidth() - GetDipsFromPixel(50),
                        lv1.getWidth() - GetDipsFromPixel(10));
                lv2.setIndicatorBoundsRelative(lv2.getWidth() - GetDipsFromPixel(50),
                        lv2.getWidth() - GetDipsFromPixel(10));
            }
        });

        return view;
    }

    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void setExpandableListViewHeight(ExpandableListView listView,
                                             int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
//        Log.e("state","desired width"+desiredWidth+","+listView.getWidth());
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();
//            Log.e("state","group height "+groupItem.getMeasuredHeight()+","+totalHeight);
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();
//                    Log.e("state","list height "+listItem.getMeasuredHeight()+","+totalHeight);
                }
            }
        }
//        Log.e("state","----------------------");

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        Log.e("STATE","count "+listAdapter.getCount());
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            Log.e("STATE","height "+i+","+listItem.getMeasuredHeight());
        }
//        totalHeight=800;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10) {
            height = 200;
        }
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("logme","HELP");
        Constants.FaqFragment = this;
        Constants.CurrentFragment = this;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.FaqFragment = this;
        Constants.CurrentFragment = this;
    }
}
