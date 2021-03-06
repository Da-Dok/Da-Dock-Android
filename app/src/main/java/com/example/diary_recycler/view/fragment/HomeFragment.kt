package com.example.diary_recycler.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diary_recycler.*
import com.example.diary_recycler.adapter.SwipeAdapter
import com.example.diary_recycler.dataClass.PostData
import com.example.diary_recycler.dataClass.PostResponse
import com.example.diary_recycler.dataClass.SignUp
import com.example.diary_recycler.dataClass.WriteData
import com.example.diary_recycler.databinding.FragmentHomeBinding
import com.example.diary_recycler.view.RetrofitClient
import com.example.diary_recycler.view.activity.WriteActivity

class HomeFragment : Fragment() {
    lateinit var swipeadapter: SwipeAdapter
    var datas = mutableListOf<PostData>()
    lateinit var helper:SqliteHelper
    //val helper = SqliteHelper(this.context,"article",null,1)

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     //   initRecycler()

        binding.swipeLayout.setOnRefreshListener {
            postSelect()
            binding.swipeLayout.setRefreshing(false)
        }
        binding.floatingActionButton.setOnClickListener{
            activity?.let{
                Log.e("homeFrag.initRecycler", "1")
                //수정
                val writeActivity =  WriteActivity()
                //writeActivity.helper = helper
                val intent = Intent(context, writeActivity::class.java)
                startActivity(intent)
                Log.e("homeFrag.initRecycler", "2")
            }
        }
        return binding.root
    }


    private fun initRecycler() {//select
        swipeadapter = SwipeAdapter(requireContext())
    //    helper = SqliteHelper(getActivity(), "article", null, 1)
        swipeadapter.datas.addAll(datas)//helper의 select값을 swipeadater의 datas에 넣는다.
       // swipeadapter.helper = helper//helper 동기화
        binding.rvProfile.adapter = swipeadapter
        swipeadapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout =true
        layoutManager.stackFromEnd= true
        binding.rvProfile.layoutManager = layoutManager
    //initRecycler()//완료 버튼 누르면 데이터 바인딩

    }
    fun setArticle(){//insert//select
        var content = arguments?.getString("content")
        var title = arguments?.getString("title")
        var img = arguments?.getString("img")

        if (content != null) {

            val article = title?.let { WriteData(null, it, content, System.currentTimeMillis(), img ) }

            if (article != null) {
                helper.insertArticle(article)
            }//새로 입력한 데이터를 helper에 넣는다.

            swipeadapter.datas.clear()
            //swipeadapter.datas.addAll(helper.selectArticle())//swipeadapter 비우고 입력한 값 select 해서 추가
            swipeadapter.notifyDataSetChanged()
            Log.e("HomeFrag.setArticle", "finished")

        }else{
            Log.e("HomeFrag.setArticle", "failed")
        }

    }
    override fun onResume() {
        super.onResume()
        postSelect()
        Log.e("I'm at HomeFragment", "1")

    }

    fun postSelect(){
        val retrofit1 = RetrofitClient.getClient()
        var server = retrofit1?.create(ServerInterface::class.java)

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
                        datas= response.body()!!.data
                        Log.e(
                            "post",
                            "가져오기 성공" + datas[0].contentImg)
                        initRecycler()
                    } else if (flag == 308) { //이메일 중복
                        Log.e(
                            "post",
                            "이미 회원가입한 계정입니다", //로그인으로 넘어가기
                        )
                    }
                } else Log.e("post", response.toString())
            }


        }))
    }
}
