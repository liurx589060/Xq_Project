package com.cd.xq.module.util.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cd.xq_util.com.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2019/5/7.
 */

public abstract class BaseRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    public final static int LAYOUT_TYPE_EMPTY = 0;
    public final static int LAYOUT_TYPE_LOADING = 1;
    public final static int LAYOUT_TYPE_NET = 2;

    public enum ELayoutType {
        LAYOUT_EMPTY,  //空白页
        LAYOUT_LOADING,  //加载页面
        LAYOUT_NET,   //网络页面
    }

    private HashMap<ELayoutType,Boolean> mLayoutShowMap;
    private ELayoutType mCurrentLayoutType = ELayoutType.LAYOUT_LOADING;
    private Context mContext;
    private boolean mIsNoData;
    private IBaseLayoutListener mListener;

    public void setIBaseLayoutListener(IBaseLayoutListener listener) {
        mListener = listener;
    }

    /**
     * 是否一直开始显示加载页，默认是显示的
     * @param initShowLoading
     */
    public void setInitShowLoading(boolean initShowLoading) {
        mLayoutShowMap.put(ELayoutType.LAYOUT_LOADING,initShowLoading);
    }

    public interface IBaseLayoutListener {
        void onRetry();

        /**
         * 返回非null，则表示截获，不在进行默认处理
         * @param parent
         * @return
         */
        RecyclerView.ViewHolder onCreateLayout(ViewGroup parent, ELayoutType layoutType);

        /**
         * 返回true，则表示截获，不在进行默认处理
         * @param viewHolder
         * @param layoutType
         * @return
         */
        boolean onBindLayout(RecyclerView.ViewHolder viewHolder,ELayoutType layoutType);
    }


    public abstract T onRealCreateViewHolder(ViewGroup parent, int viewType);
    public abstract void onRealBindViewHolder(T holder, int position);
    public abstract int getRealItemCount();
    public int getRealItemViewType() {return 0;}

    /**
     * 设置LayoutType是否显示
     * @param layoutType
     * @param isShow
     */
    public void setIsShowLayout(ELayoutType layoutType,boolean isShow) {
        mLayoutShowMap.put(layoutType,isShow);
        notifyDataSetChanged();
    }

    public void showLayoutType(ELayoutType layoutType) {
        mCurrentLayoutType = layoutType;
        notifyDataSetChanged();
    }

    public BaseRecyclerAdapter(Context context) {
        mContext = context;
        mLayoutShowMap = new HashMap<>();
        mLayoutShowMap.put(ELayoutType.LAYOUT_EMPTY,true);
        mLayoutShowMap.put(ELayoutType.LAYOUT_LOADING,true);
        mLayoutShowMap.put(ELayoutType.LAYOUT_NET,true);
    }


    @Override
    public final T onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(isHasShowLayout()) {
            if(mListener != null) {
                viewHolder = mListener.onCreateLayout(parent,mCurrentLayoutType);
            }
            if(viewHolder == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                if(viewType == LAYOUT_TYPE_EMPTY) {
                    View view = inflater.inflate(R.layout.layout_base_recycleradapter_empty,parent
                            ,false);
                    viewHolder = new BEmptyViewHolder(view);
                }else if(viewType == LAYOUT_TYPE_LOADING) {
                    View view = inflater.inflate(R.layout.layout_base_recycleradapter_loading,parent
                            ,false);
                    viewHolder = new BLoadingViewHolder(view);
                }else if(viewType == LAYOUT_TYPE_NET) {
                    View view = inflater.inflate(R.layout.layout_base_recycleradapter_net,parent
                            ,false);
                    viewHolder = new BNetViewHolder(view);
                }
            }
        }else {
            viewHolder = onRealCreateViewHolder(parent,viewType);
        }
        return (T) viewHolder;
    }

    @Override
    public final void onBindViewHolder(T holder, int position) {
        if(isHasShowLayout()) {
            boolean isReturn = false;
            if(mListener != null) {
                isReturn = mListener.onBindLayout(holder,mCurrentLayoutType);
            }
            if(!isReturn) {
                if(mCurrentLayoutType == ELayoutType.LAYOUT_EMPTY) {
                    BEmptyViewHolder viewHolder = (BEmptyViewHolder) holder;
                }else if(mCurrentLayoutType == ELayoutType.LAYOUT_LOADING) {
                    BLoadingViewHolder viewHolder = (BLoadingViewHolder) holder;
//                    viewHolder.avLoading.show();
                }else if(mCurrentLayoutType == ELayoutType.LAYOUT_NET) {
                    BNetViewHolder viewHolder = (BNetViewHolder) holder;
                }
            }
        }else {
            onRealBindViewHolder(holder,position);
        }
    }

    @Override
    public final int getItemCount() {
        if(getRealItemCount() > 0) {
            mIsNoData = false;
            return getRealItemCount();
        }
        mIsNoData = true;
        return isHasShowLayout()?1:0;
    }

    @Override
    public final int getItemViewType(int position) {
        if(isHasShowLayout()) {
            if(mCurrentLayoutType == ELayoutType.LAYOUT_EMPTY) {
                return LAYOUT_TYPE_EMPTY;
            }
            if(mCurrentLayoutType == ELayoutType.LAYOUT_LOADING) {
                return LAYOUT_TYPE_LOADING;
            }
            if(mCurrentLayoutType == ELayoutType.LAYOUT_NET) {
                return LAYOUT_TYPE_NET;
            }
        }
        return getRealItemViewType();
    }

    private boolean isHasShowLayout() {
        if(!mIsNoData) return false;
        boolean isShow = false;
        Iterator<Map.Entry<ELayoutType,Boolean>> it = mLayoutShowMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ELayoutType,Boolean> entry = it.next();
            isShow = entry.getValue();
            if(isShow && entry.getKey() == mCurrentLayoutType) {
                break;
            }
        }
        return isShow;
    }

    /*************************************************************/
    /**
     * 空白页的ViewHolder
     */
    private class BEmptyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageLogo;
        public TextView textTip;

        public BEmptyViewHolder(View itemView) {
            super(itemView);

            imageLogo = itemView.findViewById(R.id.image_empty);
            textTip = itemView.findViewById(R.id.text_empty);
        }
    }

    /**
     * 加载页的ViewHolder
     */
    private class BLoadingViewHolder extends RecyclerView.ViewHolder {
        public AVLoadingIndicatorView avLoading;

        public BLoadingViewHolder(View itemView) {
            super(itemView);

            avLoading = itemView.findViewById(R.id.av_loading);
            avLoading.show();
        }
    }

    /**
     * 网络页的ViewHolder
     */
    private class BNetViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageLogo;
        public TextView textTip;

        public BNetViewHolder(View itemView) {
            super(itemView);

            imageLogo = itemView.findViewById(R.id.image_net);
            textTip = itemView.findViewById(R.id.text_net);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.onRetry();
                    }
                }
            });
        }
    }
}
