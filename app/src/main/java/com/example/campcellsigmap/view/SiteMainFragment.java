package com.example.campcellsigmap.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.campcellsigmap.R;
import com.example.campcellsigmap.utils.CSVHelper;

import java.util.LinkedList;
import java.util.List;

import static com.example.campcellsigmap.utils.Config.FILENAME;
import static com.example.campcellsigmap.utils.Config.SITE_FOLDER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SiteMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SiteMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SiteMainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<String> mArrayAdapter;

    public SiteMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SiteMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SiteMainFragment newInstance(String param1, String param2) {
        SiteMainFragment fragment = new SiteMainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void initView(View view) {
        TextView tipTv = view.findViewById(R.id.tip);
        tipTv.setText(R.string.site_tip);

        Button startBtn = view.findViewById(R.id.start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpInputWindow(v);
            }
        });

        ListView listView = view.findViewById(R.id.list);
        mArrayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_list_item_1, new LinkedList<String>()
        );
        listView.setAdapter(mArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String)parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), SiteShowActivity.class);
                intent.putExtra(FILENAME, selectedItem);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                CSVHelper.deleteFile(getContext(), SITE_FOLDER, selectedItem);
                reload();
                return false;
            }
        });
    }

    private void popUpInputWindow(View v) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpWindowView = inflater.inflate(R.layout.popup_input_filename, null);
        EditText hintEt = popUpWindowView.findViewById(R.id.input);
        hintEt.setHint(R.string.site_filename_input_hint);

        final PopupWindow popupWindow = new PopupWindow(
                popUpWindowView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        final EditText inputEt = popUpWindowView.findViewById(R.id.input);
        Button cancelBtn = popUpWindowView.findViewById(R.id.cancel);
        Button okBtn = popUpWindowView.findViewById(R.id.ok);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputEt.getText().toString();
                if (null != name && !name.isEmpty()) {
                    popupWindow.dismiss();
                    Intent intent = new Intent(getActivity(), SiteRecordActivity.class);
                    intent.putExtra(FILENAME, name);
                    startActivity(intent);
                }
            }
        });

    }

    private void reload() {
        List<String> samples = CSVHelper.getFiles(getContext(), SITE_FOLDER);
        mArrayAdapter.clear();
        mArrayAdapter.addAll(samples);
        mArrayAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
