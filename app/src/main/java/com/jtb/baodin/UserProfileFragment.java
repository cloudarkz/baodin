package com.jtb.baodin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnUserProfileFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnUserProfileFragmentInteraction mListener;

    private static TextView fragmentTextView;
    private static TextView usernameTextView;
    private static TextView accountIDTextView;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userprofile, container, false);

        fragmentTextView = (TextView) view.findViewById(R.id.fragmentTextView);
        usernameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        accountIDTextView = (TextView) view.findViewById(R.id.accountIDTextView);

        view.findViewById(R.id.fragmentCallbackButton).setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mParam1 = args.getString(ARG_PARAM1);
            mParam2 = args.getString(ARG_PARAM2);
        } else {
            mParam1 = savedInstanceState.getString(ARG_PARAM1);
            mParam2 = savedInstanceState.getString(ARG_PARAM2);
        }

        fragmentTextView.setText(mParam2 + " selected!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserProfileFragmentInteraction) {
            mListener = (OnUserProfileFragmentInteraction) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragmentCallbackButton:
                onButtonPressed(view);
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onUserProfileFragmentInteraction(mParam1 + ": Callback successful");
        }
    }

    public void populateUserProfile(String username, String accountID) {
        usernameTextView.setText("username: " + username);
        accountIDTextView.setText("accountID: " + accountID);
    }

    public interface OnUserProfileFragmentInteraction {
        void onUserProfileFragmentInteraction(String string);
    }
}
