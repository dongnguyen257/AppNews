package net.appnews.widgets.pullableRecyclerView;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DongNguyen on 10/15/16.
 */

public class PullableRecyclerView extends RecyclerView {
    private static final float DRAG_RATE = 2;
    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private ArrayList<View> headerViews = new ArrayList<>();
    private WrapAdapter wrapAdapter;
    private float mLastY = -1;
    private LoadingListener mLoadingListener;
    private static final int TYPE_REFRESH_HEADER = 10000;
    private static final int TYPE_FOOTER = 10001;
    private static final int HEADER_INIT_INDEX = 10002;
    private static List<Integer> sHeaderTypes = new ArrayList<>();
    private int mPageCount = 0;
    private View mEmptyView;
    private RefeshView refeshView;
    private FooterView footView;
    private final RecyclerView.AdapterDataObserver dataObserver = new DataObserver();
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;
    public PullableRecyclerView(Context context) {
        this(context, null);
        init();
    }

    public PullableRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        refeshView = new RefeshView(getContext());
        footView = new FooterView(getContext());
    }

    public void addHeaderView(View view) {
        sHeaderTypes.add(HEADER_INIT_INDEX + headerViews.size());
        headerViews.add(view);
    }

    private View getHeaderViewByType(int itemType) {
        if(!isHeaderType(itemType)) {
            return null;
        }
        return headerViews.get(itemType - HEADER_INIT_INDEX);
    }

    private boolean isHeaderType(int itemViewType) {
        return  headerViews.size() > 0 &&  sHeaderTypes.contains(itemViewType);
    }

    private boolean isReservedItemViewType(int itemViewType) {
        if(itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_FOOTER || sHeaderTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        footView.loadMoreComplete();
    }

    public void setNoMore(boolean noMore){
        isLoadingData = false;
        if (noMore)
            footView.noMore();
        else
            footView.loadMoreComplete();
    }

    public void reset(){
        setNoMore(false);
        loadMoreComplete();
        refreshComplete();
    }

    public void refreshComplete() {
        refeshView.refreshComplete();
        setNoMore(false);
    }

    public void setPullRefreshEnabled(boolean enabled) {
        if (!enabled)
            refeshView.noRefesh();
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        if (!enabled)
            footView.noMore();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        dataObserver.onChanged();
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        wrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(wrapAdapter);
        adapter.registerAdapterDataObserver(dataObserver);
        dataObserver.onChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    refeshView.onMove(deltaY / DRAG_RATE);
                    if (refeshView.getVisibleHeight() > 0 && refeshView.getState() < RefeshView.STATE_REFRESHING) {
                        return false;
                    }
                } else if (isDownBottom()){
                    float tmpDelta = deltaY / DRAG_RATE;
                    int tmp = (int)tmpDelta;
                    float remain = tmpDelta < 0f ? tmp - tmpDelta : tmpDelta - tmp;
                    int delta = tmpDelta < 0f ? remain >= 0.5f ? tmp - 1 : tmp : remain >= 0.5f ? tmp + 1 : tmp;
                    delta *= -1f;
                    footView.onMove(delta);
                    if (delta < 0 && footView.getVisibleHeight() > 0) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (refeshView.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                        }
                    }
                } else if (isDownBottom()){
                    if (footView.releaseAction()) {
                        if (mLoadingListener != null)
                            mLoadingListener.onLoadMore();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private boolean isOnTop() {
        if (refeshView.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDownBottom() {
        if (footView.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                int emptyCount = 2;
                if (adapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    PullableRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    PullableRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
            if (wrapAdapter != null) {
                wrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            wrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            wrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            wrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            wrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            wrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    public class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public boolean isHeader(int position) {
            return position >= 1 && position < headerViews.size() + 1;
        }

        public boolean isFooter(int position) {
            return position == getItemCount() - 1;
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            return headerViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(refeshView);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(footView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }



        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position) || isFooter(position)) {
                return;
            }
            int adjPosition = position - (getHeadersCount() + 1);
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            int count = adapter == null || adapter.getItemCount() == 0 ? 0 : 2;
            if (adapter != null) {
                return getHeadersCount() + adapter.getItemCount() + count;
            } else {
                return getHeadersCount() + count;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - (getHeadersCount() + 1);
            if(isReservedItemViewType(adapter.getItemViewType(adjPosition))) {
                throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 " );
            }
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - 1;
                return sHeaderTypes.get(position);
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }

            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount() + 1) {
                int adjPosition = position - (getHeadersCount() + 1);
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position)) ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams && (isHeader(holder.getLayoutPosition()) ||isRefreshHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && mLoadingListener != null) {
            refeshView.setState(RefeshView.STATE_REFRESHING);
            refeshView.onMove(refeshView.getMeasuredHeight());
            mLoadingListener.onRefresh();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if(p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if(child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout)child;
                    break;
                }
            }
            if(appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }
}
