package uk.ac.shef.oak.com6510;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
/**
 * <h1>Adapter of ViewPager</h1>

 * <b>Note:</b>This is the adapter of view pager to change the UI when users slide the screen.
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    public ViewPagerAdapter(FragmentManager fragmentManager, int numOfTabs){
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PhotoFragment photoFragment = new PhotoFragment();
                return photoFragment;
            case 1:
                AlbumFragment albumFragment = new AlbumFragment();
                return albumFragment;
            case 2:
                DiscoverFragment discoverFragment = new DiscoverFragment();
                return discoverFragment;


        }
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container,
                               int position,
                               Object object){

    }


    @Override
    public int getCount() {
        return numOfTabs;
    }
}
