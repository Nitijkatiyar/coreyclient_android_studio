package com.coremobile.coreyhealth.mypatients;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.List;

public class MyPatientAdapter extends ArrayAdapter<MyPateintModel> {

    private ArrayList<MyPateintModel> originalList;
    private ArrayList<MyPateintModel> countryList;

    private CountryFilter filter;
    Context mcontext;

    static String filterName;
    public MyPatientAdapter(Context context, int textViewResourceId,
                            List<MyPateintModel> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<MyPateintModel>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<MyPateintModel>();
        this.originalList.addAll(countryList);
        this.mcontext = context;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public MyPateintModel getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder {
        TextView displayName;
        TextView name;
        TextView continent;
        TextView region;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) mcontext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.country_list, null);

            holder = new ViewHolder();
            holder.displayName = (TextView) convertView.findViewById(R.id.displayNameTextVw);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyPateintModel country = countryList.get(position);
        holder.displayName.setText(country.getDisplayName());

//        holder.displayName.setBackgroundColor(Color.parseColor(country.getDisplayColor()));

        return convertView;

    }

    private class CountryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String ss=getFilterName();

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<MyPateintModel> filteredItems = new ArrayList<MyPateintModel>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    MyPateintModel country = originalList.get(i);
                    if (country.toString().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            countryList = (ArrayList<MyPateintModel>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }


    public  String getFilterName(){

/*
        constraint = constraint.toString().toLowerCase();
        Filter.FilterResults result = new Filter.FilterResults();
        if (constraint != null && constraint.toString().length() > 0) {
            ArrayList<MyPateintModel> filteredItems = new ArrayList<MyPateintModel>();

            for (int i = 0, l = originalList.size(); i < l; i++) {
                MyPateintModel country = originalList.get(i);


                if(getFilterName().equalsIgnoreCase("DisplayName")) {
                    if (country.getDisplayName().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                else if(getFilterName().equalsIgnoreCase("OR")) {
                    if (country.getOR().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else if(getFilterName().equalsIgnoreCase("Modality")) {
                    if (country.getModality().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else if(getFilterName().equalsIgnoreCase("Specialty")) {
                    if (country.getModality().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else if(getFilterName().equalsIgnoreCase("AnonId")) {
                    if (country.getAnonId().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else if(getFilterName().equalsIgnoreCase("Surgeon")) {
                    if (country.getSurgeon().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else if(getFilterName().equalsIgnoreCase("DateOfProcedure")) {
                    if (country.getDateOfProcedure().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else if(getFilterName().equalsIgnoreCase("TimeOfProcedure")) {
                    if (country.getTimeOfProcedure().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                else if(getFilterName().equalsIgnoreCase("ProcedureRoom")) {
                    if (country.getTimeOfProcedure().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }

                else{
                    if (country.toString().toLowerCase().contains(constraint))
                        filteredItems.add(country);

                }
*/

                return filterName ;
    }

    public void setFilterName(String filter){
        this.filterName=filter;
    }

    public void updateitems( List<MyPateintModel> countryList){

        this.countryList.clear();
        this.countryList.addAll(countryList);
        notifyDataSetChanged();
    }
}
