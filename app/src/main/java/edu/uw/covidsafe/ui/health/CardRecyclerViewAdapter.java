package edu.uw.covidsafe.ui.health;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.uw.covidsafe.utils.Constants;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();

    private Context mContext;
    private Activity av;

    public CardRecyclerViewAdapter(Context mContext, Activity av) {
        this.mContext = mContext;
        this.av = av;
        titles.add("Self-quarantine for 14 days");
        titles.add("Monitor Your Symptoms");
        titles.add("");
        titles.add("Request a test");
        titles.add("Contact your healthcare professional");
        titles.add("Isolate from those around you");
        Date dd = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        calendar.add(Calendar.DATE, Constants.QuarantineLengthInDays);
        long thresh = calendar.getTime().getTime();

        SimpleDateFormat format = new SimpleDateFormat("MMMM d");
        String ss = format.format(new Date(thresh));

        desc.add("If you start your self quarantine today, your 14 days will end "+ss+". Please check with your local Health Authorities for more guidance.");
        desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
        desc.add("Call 911 immediately if you are having a medical emergency.");
        desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
        desc.add("Please contact your healthcare professional for next steps.");
        desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");

        icons.add(mContext.getDrawable(R.drawable.icon_quarantine));
        icons.add(mContext.getDrawable(R.drawable.icon_symptoms));
        icons.add(mContext.getDrawable(R.drawable.icon_phone));
        icons.add(mContext.getDrawable(R.drawable.icon_test));
        icons.add(mContext.getDrawable(R.drawable.icon_phone2));
        icons.add(mContext.getDrawable(R.drawable.icon_quarantine));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_action, parent, false);
            return new CardRecyclerViewAdapter.ActionCard(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_call, parent, false);
            return new CardRecyclerViewAdapter.CallCard(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (titles.get(position).isEmpty()) {
            ((CallCard)holder).desc.setText(desc.get(position));
            ((CallCard)holder).icon.setImageDrawable(icons.get(position));
        }
        else {
            ((ActionCard)holder).title.setText(titles.get(position));
            ((ActionCard)holder).desc.setText(desc.get(position));
            ((ActionCard)holder).icon.setImageDrawable(icons.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (titles.get(position).isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public class ActionCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        ConstraintLayout parentLayout;

        ActionCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.textView7);
            this.desc = itemView.findViewById(R.id.textView5);
            this.parentLayout = itemView.findViewById(R.id.parent_layout2);
        }
    }

    public class CallCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView desc;
        ConstraintLayout parentLayout;

        CallCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.imageView7);
            this.desc = itemView.findViewById(R.id.textView7);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
