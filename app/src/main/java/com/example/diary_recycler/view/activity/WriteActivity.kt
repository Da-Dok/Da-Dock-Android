package com.example.diary_recycler.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diary_recycler.SqliteHelper
import com.example.diary_recycler.SwipeAdapter
import com.example.diary_recycler.WriteData
import com.example.diary_recycler.databinding.ActivityMainBinding
import com.example.diary_recycler.databinding.ActivityWriteBinding
import com.example.diary_recycler.view.fragment.HomeFragment

class WriteActivity : AppCompatActivity(){
    val helper = SqliteHelper(this,"memo",null,1)
    //var adapter = HomeFragment().swipeadapter
    //여기다시보기

    private val binding: ActivityWriteBinding by lazy {
        ActivityWriteBinding.inflate(
            layoutInflater
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //이부분에서 HomeFragment의 SwipeAdapter을 사용해야 함.
        var adapter = SwipeAdapter(this)
        Log.e("this is writeactivity", "1")
        adapter.datas.addAll(helper.selectArticle())
        //저장한 데이터 select해서 어뎁터에

        adapter.helper = helper

        val title_et= binding.titleBtn
        val content_et=binding.contentsBtn
        binding.back.setOnClickListener{
            finish()
        }

        //글 저장
        binding.button.setOnClickListener {
            title_et.text
            //helper에 데이터 insert
            if(content_et.text.toString().isNotEmpty()){
                val article = WriteData(null,title_et.text.toString(), content_et.text.toString(),System.currentTimeMillis())
                helper.insertArticle(article)
                Log.e("=======================", "datas are into helper successfully")
            }//put datas into helper
            adapter.datas.clear()
            adapter.datas.addAll(helper.selectArticle())
            //clear writing form

            adapter.notifyDataSetChanged()
            title_et.setText("")
            content_et.setText("")
            //  database.

            Log.e("=======================", "save button event is successful")

            finish()
        }
    }}