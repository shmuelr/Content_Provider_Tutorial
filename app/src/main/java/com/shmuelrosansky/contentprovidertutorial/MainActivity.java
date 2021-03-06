package com.shmuelrosansky.contentprovidertutorial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shmuelrosansky.contentprovidertutorial.adapter.ItemAdapter;
import com.shmuelrosansky.contentprovidertutorial.dataUtils.TodoItemsContentProvider;
import com.shmuelrosansky.contentprovidertutorial.models.TodoItem;
import com.shmuelrosansky.contentprovidertutorial.utils.Tools;

import java.util.LinkedList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();

    // The loader's unique id. Loader ids are specific to the Activity or
    // Fragment in which they reside.
    private static final int LOADER_ID = 1;

    private ItemAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpGUI();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    private void setUpGUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recylerView);
        adapter = new ItemAdapter(new ItemAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(TodoItem item) {



                Uri uri = Uri.parse(TodoItemsContentProvider.CONTENT_URI + "/" + item.getId());

                getContentResolver().update(uri, item.toContentValues(), null, null);
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemPopup();
            }
        });

        emptyView = (TextView) findViewById(R.id.emptyView);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final TodoItem item = adapter.getItem(viewHolder.getAdapterPosition());
                Uri uri = Uri.parse(TodoItemsContentProvider.CONTENT_URI + "/" + item.getId());
                getContentResolver().delete(uri, null, null);

                adapter.removeItem(viewHolder.getAdapterPosition());

                Snackbar.make(fab, "Item removed", Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getContentResolver().insert(TodoItemsContentProvider.CONTENT_URI, item.toContentValues());
                    }
                }).show();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    private void showAddItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Todo Item");

        // Set up the input
        final AppCompatEditText input = new AppCompatEditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        final FrameLayout frameLayout = new FrameLayout(this);
        int padding16 = (int) Tools.convertDpToPixel(16, this);
        int padding8 = (int) Tools.convertDpToPixel(8, this);
        frameLayout.setPadding(padding16, padding8, padding16, padding8);
        frameLayout.addView(input);

        builder.setView(frameLayout);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoItem todoItem = new TodoItem(input.getText().toString(), System.currentTimeMillis());
                getContentResolver().insert(TodoItemsContentProvider.CONTENT_URI, todoItem.toContentValues());
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);


            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showEmptyView(boolean isEmpty) {
        if(isEmpty){
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loader Created");
        return new CursorLoader(this, TodoItemsContentProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Load Finished");
        if(data != null){
            Log.d(TAG, "We have " + data.getCount() + " items");
            if(data.moveToFirst()) {

                List<TodoItem> items = new LinkedList<>();
                do {
                    items.add(TodoItem.buildFromCursor(data));
                } while (data.moveToNext());

                adapter.addItems(items);

                showEmptyView(false);

            } else {
                showEmptyView(true);
            }

            data.close();
        } else {
            Log.w(TAG, "Data returned null!");
            showEmptyView(true);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
    }
}
