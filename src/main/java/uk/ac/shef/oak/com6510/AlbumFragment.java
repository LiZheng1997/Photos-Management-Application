package uk.ac.shef.oak.com6510;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Fragment of album photos </h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class AlbumFragment extends Fragment {
    private View view;
    private RecyclerView albumsRecyclerView;
    private MyViewModel myViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_show_albums_content,container,false);
        albumsRecyclerView =  view.findViewById(R.id.albums_grid_recycler_view);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(ShowPhotosActivity.getActivity().getApplicationContext()));
        final AlbumAdapter albumAdapter = new AlbumAdapter();
        albumsRecyclerView.setAdapter(albumAdapter);
        myViewModel = ViewModelProviders.of(AlbumFragment.this).get(MyViewModel.class);
        // notify UI changes when data change
        myViewModel.getAll().observe(AlbumFragment.this, new Observer<List<ImageElement>>() {
            @Override
            public void onChanged(@Nullable List<ImageElement> imageElements) {
                List<ImageOnDate> emptyList = new ArrayList<>();
                //if no data, clear the view, nothing display
                if (imageElements.size() == 0 || imageElements == null){
                    albumAdapter.setImages(emptyList);
                }else{
                    int numberOfUniqueDate = Util.getNumberOfUniqueDate(imageElements);
                    ArrayList<String> uniqueDateList = Util.getUniqueDateList(imageElements);
                    List<ImageOnDate> imageOnDates = new ArrayList<>();
                    // store List of photos on same date in ImageOnDate
                    // store ImageOnDate in List
                    for (int i = 0; i<numberOfUniqueDate; i++){
                        String specificDate = uniqueDateList.get(i);
                        ImageOnDate imageOnDate = new ImageOnDate(Util.getListOnDate(imageElements, specificDate));
                        imageOnDates.add(imageOnDate);
                    }

//                    System.out.println("UniqueList SIZE" + imageOnDates.size());
                    List<ImageOnDate> groups = new ArrayList<>();
                    groups = imageOnDates;
                    // notify adapter to change
                    albumAdapter.setImages(groups);
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

