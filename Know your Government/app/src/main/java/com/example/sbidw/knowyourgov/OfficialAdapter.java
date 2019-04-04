package com.example.sbidw.knowyourgov;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {


    private ArrayList<Person> personList;
    private MainActivity mainActivity;

    public OfficialAdapter(ArrayList<Person> stockList, MainActivity mainActivity) {
        this.personList = stockList;
        this.mainActivity = mainActivity;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_view, viewGroup, false);


        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);


        return new OfficialViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(OfficialViewHolder officialViewHolder, int position) {

        Person person = personList.get(position);


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(person.getPersonName());

        if(person.getParty() != null)
        {
            stringBuffer.append("( ");
            stringBuffer.append(person.getParty());
            stringBuffer.append(" )");
        }
        officialViewHolder.officialName.setText(stringBuffer);
        officialViewHolder.officialDesignation.setText(person.getPersonDesignation());

    }

    @Override
    public int getItemCount() {

        return personList.size();

    }
}
