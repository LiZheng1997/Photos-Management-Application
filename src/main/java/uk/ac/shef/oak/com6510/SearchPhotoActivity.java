package uk.ac.shef.oak.com6510;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/**
 * <h1>The activity to search photos and show the results.</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */

public class SearchPhotoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String selectedDate="";
    MyViewModel myViewModel;
    private RecyclerView mRecyclerView;
    private Activity activity;
    final private PhotosAdapter mAdapter = new PhotosAdapter();
    List<ImageElement> resetList = new ArrayList<>();
    private Toolbar topNavigationBar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_photo);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        activity= this;
        initToolBar();
        final EditText descText = findViewById(R.id.descriptionEditText);
        final EditText titleText = findViewById(R.id.titleEditText);
        final EditText dateText = findViewById(R.id.dateEditText);
        dateText.setCursorVisible(false);
        final Button searchBtn = findViewById(R.id.searchButton);
        mDrawerLayout =findViewById(R.id.search_drawer_layout);

        Calendar calendar = Calendar.getInstance();

        mRecyclerView =  findViewById(R.id.search_grid_recycler_view);
        int numberOfColumns = 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mRecyclerView.setAdapter(mAdapter);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateText.setText("");
            }
        });

        mAdapter.setImages(resetList);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                SearchPhotoActivity.this, SearchPhotoActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ((FloatingActionButton) findViewById(R.id.dateButton))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDialog.show();
                    }
                });

        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String decs = descText.getText().toString();
                String title = titleText.getText().toString();
                // if title is not empty, description is empty, date is empty
                if (!title.isEmpty() && decs.isEmpty() && selectedDate.isEmpty()){
                    // search by title
                    subscribeUi(myViewModel.searchByTitle(title));
                }

                // if title is empty, description is not empty, date is empty
                if (title.isEmpty() && !decs.isEmpty() && selectedDate.isEmpty()){
                    // search by description
                    subscribeUi(myViewModel.searchByDecs(decs));
                }

                // if title is empty, description is empty, date is not empty
                if (title.isEmpty() && decs.isEmpty() && !selectedDate.isEmpty()){
                    // search by date
                    subscribeUi(myViewModel.searchByDate(selectedDate));
                }

                // if title is not empty, description is not empty, date is empty
                if (!title.isEmpty() && !decs.isEmpty() && selectedDate.isEmpty()){
                    // search by description and title
                    subscribeUi(myViewModel.searchByDecsTitle(decs, title));
                }

                // if title is empty, description is not empty, date is not empty
                if (title.isEmpty() && !decs.isEmpty() && !selectedDate.isEmpty()){
                    // search by description and date
                    subscribeUi(myViewModel.searchByDecsDate(decs, selectedDate));
                }

                // if title is not empty, description is empty, date is not empty
                if (!title.isEmpty() && decs.isEmpty() && !selectedDate.isEmpty()){
                    // search by title and date
                    subscribeUi(myViewModel.searchByTitleDate(title, selectedDate));
                }

                // if title is not empty, description is not empty, date is not empty
                if (!title.isEmpty() && !decs.isEmpty() && !selectedDate.isEmpty()){
                    // search by title, description and date
                    subscribeUi(myViewModel.searchByAll(decs, title, selectedDate));
                }

                // all of the conditions is empty, cannot search
                if (title.equals("") && decs.equals("") && selectedDate.equals("")){
                    Toast.makeText(SearchPhotoActivity.this, "Please select one of the conditions", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


   // get selected date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedDate = String.format("%s/%s/%s", String.valueOf(dayOfMonth), String.valueOf(month+1), String.valueOf(year));
        final EditText dateText = findViewById(R.id.dateEditText);
        dateText.setText(selectedDate);
    }

    /**
     *Select which methods subscribe to UI
     * @param liveData LiveData of ImageElement
     */
    private void subscribeUi(LiveData<List<ImageElement>> liveData) {
        // Update the list when the data changes
        liveData.observe(this, new Observer<List<ImageElement>>() {
            @Override
            public void onChanged(@Nullable List<ImageElement> imageElements) {
                if (imageElements.size() == 0||imageElements==null) {
                    Toast.makeText(SearchPhotoActivity.this, "No photos", Toast.LENGTH_LONG).show();
                    // if no data in database, clear the panel
                    mAdapter.setImages(resetList);
                } else {
                    // notify adapter to display photos
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    mAdapter.setImages(imageElements);
                }
            }
        });
    }

    /**
     *Initialize tool bar
     */
    // initial tool bar
    private void initToolBar() {
        topNavigationBar = findViewById(R.id.search_toolBar);
        topNavigationBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPhotoActivity.this.finish();
            }
        });
    }
}
