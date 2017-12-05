package com.assassin.origins.ui.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.assassin.origins.R;


/**
 * 分页Adapter，使用ProgressBar样式的Footer
 *
 * Created by YangChao on 14-12-23.
 */
public abstract class PageAdapter<T> extends HeaderFooterRecyclerAdapter<T> {

    private boolean mUseFooter = true;
    private boolean mShowNoMore = false;

    public PageAdapter(Context context) {
        super(context);
    }

    void showFooter(boolean show) {
        mUseFooter = show;
    }

    void showNoMore(boolean show) {
        mShowNoMore = show;
    }

    @Override
    public boolean useHeader() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getBasicItemViewType(int position) {
        return 0;
    }

    @Override
    public boolean useFooter() {
        return mUseFooter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.adapter_footer, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new FooterHolder(view);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
        FooterHolder footerHolder = (FooterHolder) holder;
        if (mShowNoMore) {
            footerHolder.progressBar.setVisibility(View.GONE);
            footerHolder.noMore.setVisibility(View.VISIBLE);
        } else {
            footerHolder.progressBar.setVisibility(View.VISIBLE);
            footerHolder.noMore.setVisibility(View.GONE);
        }
    }

    private class FooterHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        View noMore;

        FooterHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(android.R.id.progress);
            noMore = itemView.findViewById(R.id.no_more);
        }
    }
}
