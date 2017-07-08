package com.sample.androidarchitecture.ui.common;

import android.annotation.SuppressLint;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public abstract class DataBoundListAdapter<T, V extends ViewDataBinding>
        extends RecyclerView.Adapter<DataBoundViewHolder<V>> {

    @Nullable
    private List<T> items;

    private int dataVersion = 0;

    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        V binding = createBinding(parent);
        return new DataBoundViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, items.get(position));
        holder.binding.executePendingBindings();
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void replace(List<T> update) {

        dataVersion++;

        if (items == null) {
            if (update == null) {
                return;
            }

            items = update;
            notifyDataSetChanged();

        } else if (update == null) {

            int oldSize = items.size();
            items = null;
            notifyItemRangeRemoved(0, oldSize);

        } else {

            final int startVersion = dataVersion;
            final List<T> oldItems = items;

            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... params) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = oldItems.get(newItemPosition);

                            return DataBoundListAdapter.this.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = oldItems.get(newItemPosition);

                            return DataBoundListAdapter.this.areContentsTheSame(oldItem, newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {

                    if (startVersion != dataVersion) {
                        // ignore update
                        return;
                    }

                    items = update;
                    diffResult.dispatchUpdatesTo(DataBoundListAdapter.this);
                }
            };
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    protected abstract boolean areContentsTheSame(T oldItem, T newItem);

    protected abstract boolean areItemsTheSame(T oldItem, T newItem);

    protected abstract void bind(V binding, T t);

    protected abstract V createBinding(ViewGroup parent);

}