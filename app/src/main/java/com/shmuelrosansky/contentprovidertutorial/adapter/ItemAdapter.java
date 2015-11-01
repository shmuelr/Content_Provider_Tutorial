package com.shmuelrosansky.contentprovidertutorial.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

import com.shmuelrosansky.contentprovidertutorial.R;
import com.shmuelrosansky.contentprovidertutorial.models.TodoItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 10/24/2015.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<TodoItem> items = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private int newItemPosition = -1;

    public ItemAdapter(ItemClickListener listener){
        itemClickListener = listener;
    }

    public void addItem(TodoItem item){
        if(!items.contains(item)){
            items.add(item);
            Collections.sort(items);
            notifyItemInserted(items.indexOf(item));
        }
    }

    public void addItems(Collection<TodoItem> newItems){

        for(TodoItem item : newItems){
            if(!items.contains(item)){
                items.add(item);
                newItemPosition = items.size() -1;
            }
        }
        Collections.sort(items);
        notifyDataSetChanged();
    }

    public TodoItem getItem(int position){
        return items.get(position);
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    // This listener listens for clicks to the viewholder. The viewholder passes back the position it is.
    private final ClickListener clickListener = new ClickListener() {
        @Override
        public void onItemClicked(int position) {
            // Get the item that was clicked
            TodoItem item = items.get(position);

            // Set the completed state
            item.setCompleted(!item.isCompleted());

            //Update the UI
            notifyItemChanged(position);

            // Pass the item back the the itemclicklistener
            itemClickListener.onItemClicked(item);
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        return new ViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TodoItem item = items.get(position);
        holder.textView.setText(item.getText());
        holder.checkBox.setChecked(item.isCompleted());

        if(position == newItemPosition){

            holder.textView
                    .animate()
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .alpha(.3f)
                    .setDuration(300)
                    .setStartDelay(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            holder.textView
                                    .animate()
                                    .setInterpolator(new DecelerateInterpolator())
                                    .alpha(1f)
                                    .setDuration(300)
                                    .start();
                        }
                    }).start();

            newItemPosition = -1;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    // The view holder listens to clicks on the
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        CheckBox checkBox;
        ClickListener clickListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClicked(getAdapterPosition());
        }
    }

    // Private listener for the adapter to know about view clicks
    private interface ClickListener{
        void onItemClicked(int position);
    }

    // Public listener to pass the item back to the activity
    public interface ItemClickListener{
        void onItemClicked(TodoItem item);
    }
}
