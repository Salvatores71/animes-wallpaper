package cn.animekid.animeswallpaper.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.adapter.ImageAdapter
import cn.animekid.animeswallpaper.api.ImageService
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.DataParcelable
import cn.animekid.animeswallpaper.data.ImageDataBean
import cn.animekid.animeswallpaper.ui.DetailActivity
import cn.animekid.animeswallpaper.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentPhone: BaseFFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_phone, container, false)
        this.adapter = ImageAdapter(view.context, imageList)
        this.imageGrid = view.findViewById(R.id.imagePhoneList)
        this.errorview = view.findViewById(R.id.noInternet)
        val reloadImageList = view.findViewById<Button>(R.id.reloadImageList)
        val swiperereshlayout = view.findViewById<SwipeRefreshLayout>(R.id.swiperereshlayout)
        this.imageGrid!!.adapter = adapter
        this.loadingMore(Requester.ImageService().getAnimephone(page = 1), 1)

        reloadImageList.setOnClickListener {
            this.loadingMore(Requester.ImageService().getAnimephone(page = 1), 1)
        }

        // 滚动条自动加载下一页
        this.imageGrid!!.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = imageList.size
                val page : Int = length / 10 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@FragmentPhone.loadingMore(Requester.ImageService().getAnimephone(page = page), page)
                }
            }
        })
        this.imageGrid!!.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = imageList.get(index.toInt())
            val request = DataParcelable(bean.image_album_id,bean.image_date,bean.image_date_gmt,bean.image_extension,bean.image_height,
                    bean.image_id,bean.image_likes,bean.image_medium,bean.image_name,bean.image_size,bean.image_source,bean.image_thumb,bean.image_width)
            val intent = Intent(this.context, DetailActivity::class.java)
            intent.putExtra("imagebean", request)
            intent.putExtra("type", "1")
            startActivity(intent)
        }

        // 下拉刷新颜色设置
        swiperereshlayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light)

        // 下拉刷新动作
        swiperereshlayout.setOnRefreshListener {
            //设置2秒的时间来执行以下事件
            Handler().postDelayed(Runnable {
                this.imageList.clear()
                this.loadingMore(Requester.ImageService().getAnimephone(page = 1), 1)
                this.adapter?.notifyDataSetChanged()
                swiperereshlayout.isRefreshing = false
            }, 2000)
        }
        return view
    }

    companion object {
        fun newInstance(): FragmentPhone = FragmentPhone()
    }

}