package net.appnews.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class BaseAdapter<V,VH extends BaseHolder> extends RecyclerView.Adapter<VH>{
    protected LayoutInflater inflater;
    protected List<V> dataSource = Collections.emptyList();

    public BaseAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bindData(dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public void setDataSource(List<V> dataSource) {
        try{
            this.dataSource =new ArrayList<>(dataSource);
            notifyDataSetChanged();
        }catch (IllegalStateException  e){

        }
    }

    public List<V> getDataSource() {
        return this.dataSource;
    }
}
