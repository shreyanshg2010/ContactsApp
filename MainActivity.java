package co.shrey.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;



public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();;
    private DatabaseReference rootref = database.getReference();
    private DatabaseReference childref = rootref.getRef();
    List<UserInformation>  list = new ArrayList<>();
    FloatingActionButton fab_plus;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    static Bundle mBundleRecyclerViewState;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager mLayoutmanager;
    DividerItemDecoration dividerItemDecoration;
    ListAdapter madapter;
    UserInformation userInfo;
    TextView name_iv,phone_iv,email_iv;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    private boolean ascending = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_layout);
        name_iv = (TextView) findViewById(R.id.name);
        phone_iv = (TextView) findViewById(R.id.phone);
        email_iv = (TextView) findViewById(R.id.email);
        fab_plus = (FloatingActionButton) findViewById(R.id.fab_plus);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        getdata();
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  startActivity(new );*/
                Intent intent = new Intent(MainActivity.this,StoringInDatabase.class);
                startActivity(intent);
            }
        });

    }



    protected void getdata() {

        childref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                /*uinfo=new UserInformation();*/
                    userInfo = data.getValue(UserInformation.class);
                    userInfo.setKey(data.getKey());
                    /*Log.e("TAG", userInfo.getAddress() + userInfo.getAge() + userInfo.getName());*/
                    list.add(userInfo);
                }
                //  setupList();
                madapter = new ListAdapter(MainActivity.this,list);
                mLayoutmanager = new LinearLayoutManager(MainActivity.this);
                dividerItemDecoration = new DividerItemDecoration(MainActivity.this, mLayoutmanager.getOrientation());
                recyclerView.setLayoutManager(mLayoutmanager);
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setAdapter(madapter);
               this.initializeViews();
            }

            private void initializeViews() {
                recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                madapter = new ListAdapter(MainActivity.this,list);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                Collections.sort(list, new Comparator<UserInformation>() {
                    @Override
                    public int compare(UserInformation o1, UserInformation o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                madapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //for search button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }
    //to recover the state when we press back in element from the list
    @Override
    protected void onPause()
    {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        if (mBundleRecyclerViewState != null && recyclerView != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            if (recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if (mBundleRecyclerViewState != null && recyclerView != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            if (recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
        }
    }
    //searching an element from the list
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<UserInformation> newList = new ArrayList<>();
        madapter.setfilter(newText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:

                        Intent intent = new Intent(MainActivity.this,StoringInDatabase.class);
                        startActivity(intent);
                        // User chose the "Settings" item, show the app settings UI...
                        return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}

