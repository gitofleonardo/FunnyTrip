package cn.huangchengxi.funnytrip.adapter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.route.BottomSheetHelperCallback;
import cn.huangchengxi.funnytrip.item.PositionItem;
import cn.huangchengxi.funnytrip.viewholder.BottomPosHolder;

public class BottomPosAdapter extends RecyclerView.Adapter<BottomPosHolder> implements BottomSheetHelperCallback.OnItemTouchMoveListener {
    private List<PositionItem> list;
    private OnDragListionListener onDragListionListener;
    private OnItemChangedListener onItemChangedListener;
    private List<BottomPosHolder> holders;
    private int index=-1;
    private MyHandler myHandler=new MyHandler();
    private ViewGroup highLightedGroup=null;

    public BottomPosAdapter(List<PositionItem> list) {
        this.list=list;
        holders=new ArrayList<>();
    }

    @NonNull
    @Override
    public BottomPosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BottomPosHolder holder=new BottomPosHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bottom_pos_item,parent,false));
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BottomPosHolder holder, int position) {
        PositionItem item=list.get(position);
        holder.name.setText(item.getName());
        holder.position.setText("经度"+item.getLatitude()+" 纬度"+item.getLongitude());
        holder.index.setText(String.valueOf(position+1));
        holder.touchFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked()==MotionEvent.ACTION_DOWN){
                    if (onDragListionListener!=null){
                        onDragListionListener.onDrag(holder);
                    }
                }
                return false;
            }
        });
        if (position==index){
            highLightedGroup=holder.touchFrame;
            //new Thread(new HighLighted()).start();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public boolean onItemRemove(int position) {
        list.remove(position);
        notifyDataSetChanged();
        if (onItemChangedListener!=null){
            onItemChangedListener.onChanged();
        }
        return true;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition<toPosition){
            for (int i=fromPosition;i<toPosition;i++){
                Collections.swap(list,i,i+1);
            }
        }else{
            for (int j=fromPosition;j>toPosition;j--){
                Collections.swap(list,j,j-1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
        if (onItemChangedListener!=null){
            onItemChangedListener.onChanged();
        }
        return true;
    }

    @Override
    public void onIdel() {
        notifyDataSetChanged();
    }
    public  interface OnDragListionListener{
        void onDrag(RecyclerView.ViewHolder holder);
    }

    public void setOnDragListionListener(OnDragListionListener onDragListionListener) {
        this.onDragListionListener = onDragListionListener;
    }
    public interface OnItemChangedListener{
        void onChanged();
    }

    public void setOnItemChangedListener(OnItemChangedListener onItemChangedListener) {
        this.onItemChangedListener = onItemChangedListener;
    }
    public void highLightedItem(int index){
        this.index=index;
        notifyDataSetChanged();
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                switch (msg.what){
                    case 0:
                        highLightedGroup.setScaleX(1.05f);
                        highLightedGroup.setScaleY(1.05f);
                        break;
                    case 1:
                        highLightedGroup.setScaleX(1.0f);
                        highLightedGroup.setScaleY(1.0f);
                        break;
                }
            }catch (Exception e){
                Log.e("error",e.toString());
            }
        }
    }
    private class HighLighted implements Runnable{
        @Override
        public void run() {
            try {
                for (int i=0;i<4;i++){
                    if (i%2==0){
                        Message msg=myHandler.obtainMessage();
                        msg.what=0;
                        myHandler.sendMessage(msg);
                    }else{
                        Message msg=myHandler.obtainMessage();
                        msg.what=1;
                        myHandler.sendMessage(msg);
                    }
                    Thread.sleep(1000);
                }
            }catch (Exception e){
                Log.e("error",e.toString());
            }
        }
    }
}
