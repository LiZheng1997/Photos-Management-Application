package uk.ac.shef.oak.com6510;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.ArrayList;
import java.util.List;
/**
 * <h1>The fragment to show all photos</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class PhotoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<ImageElement> myPictureList = new ArrayList<>();
    private PhotosAdapter photosAdapter;
    View view;
    private int numberOfColumns = 3;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_show_photo_content,container,false);
        mRecyclerView = view.findViewById(R.id.photo_grid_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(ShowPhotosActivity.getActivity().getApplicationContext(), numberOfColumns));

        MyViewModel myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        photosAdapter = new PhotosAdapter();
        mRecyclerView.setAdapter(photosAdapter);

        // observe LiveData
        myViewModel.getAll().observe(PhotoFragment.this, new Observer<List<ImageElement>>() {
            @Override
            public void onChanged(@Nullable List<ImageElement> images) {
                if (images.size() == 0||images == null){
                    // if no data in database, clear gallery
                    photosAdapter.setImages(new ArrayList<ImageElement>());
                    System.out.println("Empty");
                    Log.i("PhotoFrament","No data");
                }else {
                    // notify Recycler to display photos
                    Log.i("PhotoFragment","GET DATA");
                    photosAdapter.setImages(images);
                    mRecyclerView.scrollToPosition(images.size() - 1);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
