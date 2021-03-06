package com.group2.team.project2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.group2.team.project2.R;
import com.group2.team.project2.object.ReceiveDebt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class receiveViewAdapter extends BaseAdapter implements ListAdapter {

    private final ArrayList<ReceiveDebt> debts = new ArrayList<>();

    public receiveViewAdapter(JSONArray jsonArray) {
        assert jsonArray != null;

        int payedStart = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject pay_json = jsonArray.getJSONObject(i);
                JSONArray array = pay_json.getJSONArray("people");
                ArrayList<String> emailList = new ArrayList<>();
                ArrayList<String> nameList = new ArrayList<>();
                boolean[] payedList = new boolean[array.length()];
                boolean allpayed = true;
                for (int j = 0; j < array.length(); j++) {
                    emailList.add(array.getJSONObject(j).getString("email"));
                    nameList.add(array.getJSONObject(j).getString("name"));
                    payedList[j] = array.getJSONObject(j).getBoolean("payed");
                    if (!payedList[j])
                        allpayed = false;
                }

                ReceiveDebt debt = new ReceiveDebt(pay_json.getString("name"), pay_json.getString("account"),
                        pay_json.getString("amount"), pay_json.getString("time"), emailList, nameList, payedList, allpayed, pay_json.getString("timestamp"));
                if (debt.getAllPayed()) {
                    debts.add(payedStart, debt);
                } else {
                    debts.add(0, debt);
                    payedStart++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return debts.size();
    }

    @Override
    public ReceiveDebt getItem(int position) {
        return debts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(ReceiveDebt debt) {
        debts.add(0, debt);
        notifyDataSetChanged();
    }

    public void update(int position) {
        boolean allpayed = true;
        ReceiveDebt debt = debts.get(position);
        for (int j = 0; j < debt.getPayed().length; j++) {
            if (!debt.getPayed()[j]) {
                allpayed = false;
                break;
            }
        }
        if (allpayed) {
            debt.setAllpayed();
            if (debts.size() > 1) {
                debts.remove(debt);
                ReceiveDebt tempDebt = debts.get(0);
                int temp = 0;
                while (!tempDebt.getAllPayed() && temp < debts.size()) {
                    temp++;
                }
                debts.add(temp, debt);
            }
        }

        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.receive_item, null);
        }

        TextView receive_name = (TextView) convertView.findViewById(R.id.receive_name);
        TextView receive_amount = (TextView) convertView.findViewById(R.id.receive_amount);
        TextView receive_time = (TextView) convertView.findViewById(R.id.receive_time);
        ImageView receive_background = (ImageView) convertView.findViewById(R.id.receive_background);

        ReceiveDebt debt = debts.get(position);
        int payedCount = debt.getPayed().length;

        int index = -1;
        for (int i = 0; i < debt.getPayed().length; i++) {
            if (!debt.getPayed()[i]) {
                index = i;
                break;
            }
        }
        for (int i = 0; i < debt.getPayed().length; i++) {
            if (debt.getPayed()[i]) {
                payedCount--;
            }
        }
        if (payedCount == 0) {
            receive_name.setText("All Payed");
            debt.setAllpayed();
        } else if (payedCount == 1) {
            receive_name.setText(debt.getNames().get(index));
        } else {
            receive_name.setText(String.format("%s 외 %d", debt.getNames().get(index), payedCount - 1));
        }
        receive_amount.setText(debt.getAmount() + "원");
        receive_time.setText(debt.getTime());

        if (debt.getAllPayed()) {
            receive_background.setVisibility(View.VISIBLE);
        } else {
            receive_background.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}