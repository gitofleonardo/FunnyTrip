package cn.huangchengxi.funnytrip.activity.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.FriendSearchResultAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.SearchResultBean;
import cn.huangchengxi.funnytrip.item.FriendSearchResultItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchFriendActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView search;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private EditText searchBar;
    private List<FriendSearchResultItem> list;
    private FriendSearchResultAdapter adapter;

    private MyHandler myHandler=new MyHandler();

    private String currentKeyword;

    private final int CONNECTION_FAILED=0;
    private final int SEARCH_OK=1;
    private final int SEARCH_FAILED=2;
    private final int SEARCH_MORE_SPECIFIC=3;
    private final int NOT_LOGIN=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        back=findViewById(R.id.back);
        search=findViewById(R.id.search);
        recyclerView=findViewById(R.id.search_result_rv);
        srl=findViewById(R.id.srl);
        setSupportActionBar(toolbar);
        searchBar=findViewById(R.id.search_et);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do search process
                if (searchBar.getText().toString().equals("")){
                    Toast.makeText(SearchFriendActivity.this, "搜索信息不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    currentKeyword=searchBar.getText().toString();
                    commitSearch(currentKeyword);
                    if (!srl.isRefreshing()){
                        srl.setRefreshing(true);
                    }
                }
            }
        });
        list=new ArrayList<>();
        adapter=new FriendSearchResultAdapter(list,this);
        adapter.setOnResultClickListener(new FriendSearchResultAdapter.OnResultClickListener() {
            @Override
            public void onClick(View view, int position) {
                FriendDetailActivity.startDetailActivity(SearchFriendActivity.this,list.get(position).getId());
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (currentKeyword==null || currentKeyword.equals("")){
                    srl.setRefreshing(false);
                }else{
                    commitSearch(currentKeyword);
                }
            }
        });
    }
    private void commitSearch(String keyword){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchSearchResult(uid, keyword, this, new HttpHelper.OnSearchDataBack() {
            @Override
            public void onReturnMoreSpecific() {
                sendMessage(SEARCH_MORE_SPECIFIC);
            }
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnResultList(SearchResultBean bean) {
                list=bean.getList();
                sendMessage(SEARCH_OK);
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            srl.setRefreshing(false);
            switch (msg.what){
                case SEARCH_OK:
                    adapter.notifyDataSetChanged();
                    if (list.size()==0){
                        Toast.makeText(SearchFriendActivity.this, "搜索不到任何用户", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SEARCH_FAILED:
                    srl.setRefreshing(false);
                    Toast.makeText(SearchFriendActivity.this, "查找失败", Toast.LENGTH_SHORT).show();
                    break;
                case SEARCH_MORE_SPECIFIC:
                    adapter.notifyDataSetChanged();
                    Toast.makeText(SearchFriendActivity.this, "搜索内容过多，请缩小搜索范围", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_FAILED:
                    Toast.makeText(SearchFriendActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
