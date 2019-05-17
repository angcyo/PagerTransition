package com.angcyo.pager.transition.demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    companion object {
        val imageList = listOf(
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3366295473,911032378&fm=26&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1724436057,3073006466&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1601453024,3094964372&fm=26&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552454340928&di=655882bc55efec1635cfefd5397fe6d0&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Fc995d143ad4bd113ece5c17351afa40f4bfb0542.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2896686573,3507295694&fm=26&gp=0.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1364237778,861189788&fm=26&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552454383106&di=f16e8eec4f9996a453bd5b2c88f81695&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fblog%2F201507%2F06%2F20150706130957_c4Pe4.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552454407855&di=bf0662a308f60fdd3fd445b3ec79df50&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201604%2F03%2F20160403055413_AzGjV.thumb.100_100_c.jpeg",
            "http://hbimg.b0.upaiyun.com/823d7bd817f69a3b3ce2f9720e2f6adfc11a04365d138-LDS1uE_fw658"
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.apply {
            layoutManager = GridLayoutManager(context, 3)

            adapter = object : RecyclerView.Adapter<RBaseViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): RBaseViewHolder {
                    return RBaseViewHolder(parent.inflate(R.layout.item_image_photo_layout, false))
                }

                override fun getItemCount(): Int {
                    return imageList.size
                }

                override fun onBindViewHolder(viewHolder: RBaseViewHolder, position: Int) {
                    viewHolder.v<ImageView>(R.id.image_view)?.apply {
                        load(imageList[position])

                        setOnClickListener {
                            RPager.pager(fragmentManager, R.id.frame_layout) {
                                startPagerIndex = position
                                pagerCount = imageList.size

                                onGetPagerImageUrl = {
                                    imageList[it]
                                }

                                onGetTargetView = {
                                    recycler_view.getChildAt(it).findViewById(R.id.image_view)
                                }
                            }
                        }
                    }


                }
            }
        }
    }
}
