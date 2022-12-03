package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color.green
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.example.mobileappproject.databinding.ActivityWritingDiaryPageBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.*


class WritingDiaryPageActivity : AppCompatActivity() {
    private var diary: Diary? = null
    private val fontList = arrayOf<String?>("normal", "bold", "italic")
    lateinit var binding: ActivityWritingDiaryPageBinding
    private val sizeList = arrayOf<String?>("9", "12", "14", "16", "18", "20", "22", "24", "32", "36", "48", "72")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingDiaryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBt.setOnClickListener { finish() }

        val type = intent.getStringExtra("type")

        if (type.equals("ADD")) {
            binding.btnSave.text = "추가하기"
            binding.diaryPageDeleteBt.visibility = View.INVISIBLE
        } else {
            diary = intent.getSerializableExtra("item") as Diary?
            binding.btnSave.text = "수정하기"
            //제목 + 내용정보
            binding.etTodoTitle.setText(diary!!.title)
            binding.etTodoContent.setText(diary!!.content)

            //set backBtn and deleteBtn
            binding.diaryPageDeleteBt.visibility = View.VISIBLE
            binding.diaryPageDeleteBt.setOnClickListener {
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show()
                DiaryViewModel().delete(this.diary!!)
                finish()
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString()
            val content = binding.etTodoContent.text.toString()
            val date = CalendarUtil.today.toString()
            //should be developed
            val image_src = "Noting"

            if (type.equals("ADD")) {  //추가하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val diary = Diary(0, title, content, date, image_src)
                    val intent = Intent().apply {
                        putExtra("diary", diary)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else { // 수정하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val diary = Diary(diary!!.id, title, content, date, image_src)
                    val intent = Intent().apply {
                        putExtra("diary", diary)
                        putExtra("flag", 1)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

        // color picker settings
        val list: ArrayList<ItemHolder> = ArrayList()

        list.add(ItemHolder(R.color.black, "black"))
        list.add(ItemHolder(R.color.blue, "blue"))
        list.add(ItemHolder(R.color.red, "red"))
        list.add(ItemHolder(R.color.white, "white"))
        list.add(ItemHolder(R.color.light_blue, "light_blue"))
        list.add(ItemHolder(android.R.color.holo_green_dark, "green"))
        list.add(ItemHolder(android.R.color.darker_gray, "grey"))
        list.add(ItemHolder(R.color.purple_200, "purple"))
        list.add(ItemHolder(android.R.color.holo_orange_light, "yellow"))
        list.add(ItemHolder(android.R.color.holo_orange_dark, "orange"))
        list.add(ItemHolder(R.color.teal_700, "teal"))

        val adapter = SpinnerAdapter(this, list)

        binding.textColorPicker.adapter = adapter

        binding.textColorPicker.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    binding.textColorPicker.setSelection(0)
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    for (i in list.indices) {
                        if (binding.textColorPicker.selectedItemId == i.toLong()) {
                            binding.etTodoContent.setTextColor(list[i].image)
                            binding.etTodoTitle.setTextColor(list[i].image)
                        }
                    }
                }
            }
        }

        //size Picker settings
        val sizeAdapter = ArrayAdapter<String>(this, R.layout.font_list, sizeList)
        sizeAdapter.setDropDownViewResource(R.layout.font_list)
        binding.textSizePicker.adapter = sizeAdapter
        binding.textSizePicker.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.etTodoTitle.textSize =
                    binding.textSizePicker.selectedItem.toString().toFloat()
                binding.etTodoContent.textSize =
                    binding.textSizePicker.selectedItem.toString().toFloat()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("on nothing selected")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("on item click text size")
            }
        }

        //font picker settings
        val fontAdapter = ArrayAdapter<Any?>(this, R.layout.font_list, fontList)
        fontAdapter.setDropDownViewResource(R.layout.font_list)
        binding.fontPicker.adapter = fontAdapter

        println(binding.fontPicker.selectedItem)
        binding.fontPicker.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (binding.fontPicker.selectedItem == "bold") {
                    println("selected bold")
                    //    binding.etTodoTitle.se
                    // binding.etTodoContent.textStyle = "bold"
                } else if (binding.fontPicker.selectedItem == "normal") {
                    println("selected normal")
                    //  binding.etTodoTitle.textStyle = "bold"
                    // binding.etTodoContent.textStyle = "bold"
                } else if (binding.fontPicker.selectedItem == "italic") {
                    println("selected italic")
                    //  binding.etTodoTitle.textStyle = "bold"
                    // binding.etTodoContent.textStyle = "bold"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("normal")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //
                println("onItemClick  ")
            }

        }

    }
}


data class ItemHolder(val image: Int, val text: String)


class SpinnerAdapter(val context:Context, private val items:ArrayList<ItemHolder>): BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view:View?
        val holder:ViewHolder

        if(convertView==null){

            view=LayoutInflater.from(context).inflate(R.layout.color_list,parent,false)
            holder=ViewHolder
            holder.imageName=view.findViewById(R.id.image_view)
            holder.itemName=view.findViewById(R.id.text_view)

            view.tag = holder
        }else{
            view=convertView
            holder= view.tag as ViewHolder
        }
        holder.itemName.text=items[position].text
        holder.imageName.setImageResource(items[position].image)

        return view as View

    }

    override fun getItem(position: Int): Any =items[position]

    override fun getItemId(position: Int): Long =position.toLong()

    override fun getCount(): Int =items.size


    @SuppressLint("StaticFieldLeak")
    object ViewHolder{

        lateinit var imageName:ImageView
        lateinit var itemName:TextView
    }


}
