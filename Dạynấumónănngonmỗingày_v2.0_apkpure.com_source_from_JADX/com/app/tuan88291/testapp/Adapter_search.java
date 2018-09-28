package com.app.tuan88291.testapp;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.TextView;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import java.util.ArrayList;
import java.util.Iterator;

public class Adapter_search extends ArrayAdapter<Data_search> {
    private final String MY_DEBUG_TAG;
    private ArrayList<Data_search> items;
    private ArrayList<Data_search> itemsAll;
    Filter nameFilter;
    private ArrayList<Data_search> suggestions;
    private int viewResourceId;

    /* renamed from: com.app.tuan88291.testapp.Adapter_search.1 */
    class C02861 extends Filter {
        C02861() {
        }

        public String convertResultToString(Object resultValue) {
            return ((Data_search) resultValue).name;
        }

        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null) {
                return new FilterResults();
            }
            Adapter_search.this.suggestions.clear();
            Iterator it = Adapter_search.this.itemsAll.iterator();
            while (it.hasNext()) {
                Data_search customer = (Data_search) it.next();
                if (customer.name.toLowerCase().startsWith(constraint.toString().toLowerCase()) || customer.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    Adapter_search.this.suggestions.add(customer);
                } else {
                    String[] words = customer.name.split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
                    int wordCount = words.length;
                    int k = 0;
                    while (k < wordCount) {
                        if (words[k].toLowerCase().startsWith(constraint.toString().toLowerCase()) || words[k].toLowerCase().contains(constraint.toString().toLowerCase())) {
                            Adapter_search.this.suggestions.add(customer);
                            break;
                        }
                        k++;
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = Adapter_search.this.suggestions;
            filterResults.count = Adapter_search.this.suggestions.size();
            return filterResults;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Data_search> filteredList = results.values;
            if (results != null && results.count > 0) {
                Adapter_search.this.clear();
                Iterator it = filteredList.iterator();
                while (it.hasNext()) {
                    Adapter_search.this.add((Data_search) it.next());
                }
                Adapter_search.this.notifyDataSetChanged();
            }
        }
    }

    public Adapter_search(Context context, int viewResourceId, ArrayList<Data_search> items) {
        super(context, viewResourceId, items);
        this.MY_DEBUG_TAG = "CustomerAdapter";
        this.nameFilter = new C02861();
        this.items = items;
        this.itemsAll = (ArrayList) items.clone();
        this.suggestions = new ArrayList();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(this.viewResourceId, null);
        }
        Data_search customer = (Data_search) this.items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(C0336R.id.item);
            if (VERSION.SDK_INT >= 24) {
                customerNameLabel.setText(Html.fromHtml(customer.name, 0));
            } else {
                customerNameLabel.setText(Html.fromHtml(customer.name));
            }
            customerNameLabel.setSelected(true);
        }
        return v;
    }

    public Filter getFilter() {
        return this.nameFilter;
    }
}
