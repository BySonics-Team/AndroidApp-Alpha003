package com.example.basicviewer.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.basicviewer.BlankFragment3;
import com.example.basicviewer.ImageFragment;
import com.example.basicviewer.LastDataFragment;
import com.example.basicviewer.LiveDataFragment;
import com.example.login_nodejs.R;
import com.example.basicviewer.model.PasienStatus;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private PasienStatus pasienStatus = new PasienStatus();
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;


    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
            boolean onRecord = pasienStatus.isPasien_recordStat();
//            if(onRecord){
//                Toast.makeText(fragment.getActivity(),"Mohon Tunggu hingga Streaming Data Selesai",Toast.LENGTH_SHORT).show();
//            }else {
                switch (position) {
                    case 0:
                        //fragment = new BlankFragment3();
                        fragment = new LiveDataFragment(); /*COMMENT = TURN OFF BUAT TEST*/
                        break;
                    case 1:
                        //fragment = new BlankFragment3();
                        fragment = new LastDataFragment();
                        break;
                    case 2:
                        //fragment = new BlankFragment3();
                        fragment = new ImageFragment(); /*COMMENT = TURN OFF BUAT TEST*/
                        break;
                }
//            }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }


    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }

}