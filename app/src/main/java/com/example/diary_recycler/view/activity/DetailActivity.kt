package com.example.diary_recycler.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.diary_recycler.R
import com.example.diary_recycler.ServerInterface
import com.example.diary_recycler.SqliteHelper
import com.example.diary_recycler.dataClass.ChatListResponse
import com.example.diary_recycler.dataClass.PostData
import com.example.diary_recycler.dataClass.PostResponse
import com.example.diary_recycler.dataClass.WriteData
import com.example.diary_recycler.databinding.ActivityDetailBinding
import com.example.diary_recycler.view.RetrofitClient


class DetailActivity : AppCompatActivity(){
    var helper: SqliteHelper? = null
    var data : PostData?= null

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(
            layoutInflater
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
     //   supportActionBar!!.setHomeAsUpIndicator(R.drawable.menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.apply {
            setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN) //뒤로가기 아이콘 색 설정
        }

        val idx = intent.getIntExtra("id",0)
        helper = SqliteHelper(this, "article", null, 1)
        initDetail(idx)
//        data= helper?.selectArticle()?.get(idx)

        //채팅하기 버튼 누르면!
        binding.button2.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
            Log.e("detailActivity name:", binding.tvName.text.toString())

            insertChatRoomList((idx+1).toString())//먼저 채팅목록에 있는지 확인 하고 없으면 insert(token이랑 groupId 보내서 userId로 채팅방 확인-> 있으면 그냥 반환, 없으면 insert하고 반환

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        menu?.apply {
            for(index in 0 until this.size()){
                val item = this.getItem(index)
                val s = SpannableString(item.title)
                s.setSpan(ForegroundColorSpan(Color.BLACK),0,s.length,0)
                item.title = s
            }
        }
        return true
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_modify -> {
           // msgShow("Search")
            true
        }
        android.R.id.home -> {
            finish()
            true
        }
        R.id.menu_delete -> {
          //  msgShow("Profile")
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun detailset(){
        binding.tvTitle.setText(data?.title)
        binding.tvContent.setText(data?.content)
        binding.tvDate.setText(data?.created.toString())

        var title = binding.tvTitle.text.toString()
        Log.e("title out: ", title);
        if(data?.contentImg=="")
            Glide.with(this).load(R.drawable.placeholder).into(binding.imageView4)
        else Glide.with(this).load(data?.contentImg).centerCrop().into(binding.imageView4)
    }
    fun initDetail(idx:Int){
        val retrofit1 = RetrofitClient.getClient()
        var server = retrofit1?.create(ServerInterface::class.java)

       server?.getdetail(idx)?.enqueue((object: retrofit2.Callback<PostResponse> {
            override fun onFailure(call: retrofit2.Call<PostResponse>, t: Throwable?) {
                //  swipeadapter.datas.addAll(helper.selectArticle())
                Log.e(
                    "post",
                    "가져오기 실패")
            }

            override fun onResponse(call: retrofit2.Call<PostResponse>, response: retrofit2.Response<PostResponse>){

                if (response.isSuccessful()) {
                    val post: PostResponse? = response.body()
                    val flag = post?.code
                    if (flag == 200) { //보내기 성공
                        data= response.body()!!.data[0]
                        Log.e(
                            "post",
                            "가져오기 성공" + data?.title)
                        detailset()
                    } else if (flag == 308) { //이메일 중복
                        Log.e(
                            "post",
                            "이미 회원가입한 계정입니다", //로그인으로 넘어가기
                        )
                    }
                } else Log.e("post", response.toString())
            }


        }))/*

        server?.getPostRequest(1)?.enqueue((object: retrofit2.Callback<PostResponse> {
            override fun onFailure(call: retrofit2.Call<PostResponse>, t: Throwable?) {
                //  swipeadapter.datas.addAll(helper.selectArticle())
                Log.e(
                    "post",
                    "가져오기 실패")
            }

            override fun onResponse(call: retrofit2.Call<PostResponse>, response: retrofit2.Response<PostResponse>){

                if (response.isSuccessful()) {
                    val post: PostResponse? = response.body()
                    val flag = post?.code
                    if (flag == 200) { //보내기 성공
                        data= response.body()!!.data[idx]
                        Log.e(
                            "post",
                            "가져오기 성공" + data!!.contentImg)
                        detailset()

                    } else if (flag == 308) { //이메일 중복
                        Log.e(
                            "post",
                            "이미 회원가입한 계정입니다", //로그인으로 넘어가기
                        )
                    }
                } else Log.e("post", response.toString())
            }


        }))
  */  }

    fun insertChatRoomList(groupId: String){//select 하고 있으면? 중복 반환
        val retrofit1 = RetrofitClient.getClient()
        var server = retrofit1?.create(ServerInterface::class.java)
        Log.e("groupId: ", groupId)

        /*val preferences = requireActivity().getPreferences(0)
        preferences.getString("token", "")?.let { Log.e("token Chat: ", it) }
        val keys: Map<String, *> = preferences.getAll()
        for ((key, value) in keys) {
            Log.d(
                "map values2", key + ": " +
                        value.toString()
            )
        }*/

        server?.insertChatRoomList("토큰", groupId)?.enqueue((object: retrofit2.Callback<ChatListResponse> {
            override fun onFailure(call: retrofit2.Call<ChatListResponse>, t: Throwable?) {
                Log.e("ChatList", "가져오기 실패")
            }
            override fun onResponse(call: retrofit2.Call<ChatListResponse>, response: retrofit2.Response<ChatListResponse>){
                if (response.isSuccessful()) {
                    val post: ChatListResponse? = response.body()
                    val flag = post?.code
                    if (flag == 200) {
                        Log.e("ChatList", "가져오기 성공")
                    } else Log.e("ChatList", "알수 없는 에러",)
                } else Log.e("post", response.toString())
            }
        }))
    }
}