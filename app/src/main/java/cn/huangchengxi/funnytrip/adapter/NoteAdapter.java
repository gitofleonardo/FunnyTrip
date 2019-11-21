package cn.huangchengxi.funnytrip.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.viewholder.NoteHolder;

public class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {
    private List<NoteItem> list;
    private int[] colors={Color.rgb(255,87,34),Color.rgb(0,188,212),Color.rgb(255,235,59),Color.rgb(76,175,80),Color.rgb(103,58,183),Color.rgb(3,169,244)};
    private OnNoteClickListener onNoteClickListener;
    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, final int position) {
        NoteItem item=list.get(position);
        holder.title.setText(item.getFormattedTime());
        holder.content.setText(item.getContent());
        int colorIndex=(int)(Math.random()*(colors.length-1));
        holder.noteHeader.setBackgroundColor(colors[colorIndex]);
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNoteClickListener!=null){
                    onNoteClickListener.onClick(v,list.get(position));
                }
            }
        });
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_note_item,parent,false);
        NoteHolder holder=new NoteHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public NoteAdapter() {
        list=new ArrayList<>();
    }
    public void add(NoteItem item){
        list.add(item);
        notifyDataSetChanged();
    }
    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }
    public interface OnNoteClickListener{
        void onClick(View view,NoteItem item);
    }

    public void setOnNoteClickListener(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }
}
