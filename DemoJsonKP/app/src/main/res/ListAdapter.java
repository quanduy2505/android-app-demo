import com.example.quan.demojsonkp.SanPham;

/**
 * Created by quan on 06/06/2017.
 */
public class ListAdapter extends ArrayAdapter<SanPham> {

    public ListAdapter(Context context, int resource, List<SinhVien> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view =  inflater.inflate(R.layout.ten_file_xml_custom_layout, null);
        }
        SinhVien p = getItem(position);
        if (p != null) {
            // Anh xa + Gan gia tri
            TextView txt = (TextView) view.findViewById(R.id.textView);


        }
        return view;
    }

}