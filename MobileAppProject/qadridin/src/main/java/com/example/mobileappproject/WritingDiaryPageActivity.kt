package com.example.mobileappproject

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.view.*
import android.widget.*
import com.example.mobileappproject.databinding.ActivityWritingDiaryPageBinding
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import yuku.ambilwarna.AmbilWarnaDialog


class WritingDiaryPageActivity : AppCompatActivity() {
    private var diary: Diary? = null
    private val fontList = arrayOf<String?>("normal", "bold", "italic", "bold | italic")
    lateinit var binding: ActivityWritingDiaryPageBinding
    private lateinit var sizeAdapter: ArrayAdapter<String>
    private val sizeList = arrayOf<String?>("9.0", "12.0", "14.0", "16.0", "18.0", "20.0", "22.0", "24.0", "32.0", "36.0", "48.0", "72.0")
    private var titleFont = "normal"
    private var contentFont = "normal"
    private var mDefaultColor = 0
    var mDefaultTittleTextColor = -16777216
    var mDefaultContentTextColor = -16777216
    var mDefaultTittleBackColor = 0
    var mDefaultContentBackColor = 0
    private var titleSize: Float = 9.0F
    private var contentSize = 9.0F
    private var uri: Uri? = null
    private var uriString : String = R.drawable.image_empty.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingDiaryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.writingToolbar
        setSupportActionBar(toolbar)

        binding.backBt.setOnClickListener { finish() }

        val type = intent.getStringExtra("type")

        //sizePicker adapter
        sizeAdapter = ArrayAdapter<String>(this, R.layout.font_list, sizeList)
        sizeAdapter.setDropDownViewResource(R.layout.font_list)
        binding.textSizePicker.adapter = sizeAdapter

        //fontPicker adapter
        val fontAdapter = ArrayAdapter<Any?>(this, R.layout.font_list, fontList)
        fontAdapter.setDropDownViewResource(R.layout.font_list)
        binding.fontPicker.adapter = fontAdapter

        if (type.equals("ADD")) {
            // binding.btnSave.text = "추가하기"
            binding.fontPicker.setSelection(fontAdapter.getPosition(titleFont))
            println("IN ADD ------- ${fontAdapter.getPosition(titleFont)}")
            binding.textSizePicker.setSelection(sizeAdapter.getPosition(titleSize.toString()))
            binding.diaryPageDeleteBt.visibility = View.INVISIBLE
        } else {
            diary = intent.getSerializableExtra("item") as Diary?
            //   binding.btnSave.text = "수정하기"
            // color & text size attr
            diary?.let { binding.Title.setTextColor(it.tTextColor) }
            binding.Content.setTextColor(diary!!.cTextColor)
            binding.Title.setBackgroundColor(diary!!.tBackColor)
            binding.Content.setBackgroundColor(diary!!.cBackColor)
            titleSize = diary!!.titleTextSize
            contentSize = diary!!.contentTextSize
            binding.textSizePicker.setSelection(sizeAdapter.getPosition(titleSize.toString()))

            //get textStyle from db
            binding.fontPicker.setSelection(fontAdapter.getPosition(diary!!.titleFont))

            // title and content
            binding.Title.setText(diary!!.title)
            binding.Content.setText(diary!!.content)
            mDefaultTittleTextColor = diary!!.tTextColor
            mDefaultTittleBackColor = diary!!.tBackColor
            mDefaultContentTextColor = diary!!.cTextColor
            mDefaultContentBackColor = diary!!.cBackColor
            binding.imageView.setImageURI(Uri.parse(diary!!.image))
            binding.imageView.tag = diary!!.image
            println("diary image in open writing page----------------> ${diary!!.image}")
            println("diare image after Uri.parse -----> ${Uri.parse(diary!!.image)}")

            //set backBtn and deleteBtn
            binding.diaryPageDeleteBt.visibility = View.VISIBLE
            binding.diaryPageDeleteBt.setOnClickListener {
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show()
                DiaryViewModel().delete(this.diary!!)
                finish()
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.Title.text.toString()
            val content = binding.Content.text.toString()
            val date = CalendarUtil.today.toString()
            val titleTextSize =   binding.textSizePicker.selectedItem.toString().toFloat()
            val contentTextSize =   binding.textSizePicker.selectedItem.toString().toFloat()
            val titleFont = binding.fontPicker.selectedItem.toString()
            val contentFont = binding.fontPicker.selectedItem.toString()

            // get Image src
            uriString = binding.imageView.tag.toString()
            // println("=====================>${binding.imageView.toString()}")

            if (type.equals("ADD")) {  //추가하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val diary = Diary(0, title, content, date, uriString, mDefaultTittleTextColor, mDefaultTittleBackColor, mDefaultContentTextColor, mDefaultContentBackColor,  titleTextSize, contentTextSize, titleFont, contentFont)
                    val intent = Intent().apply {
                        putExtra("diary", diary)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else { // 수정하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val diary = Diary(diary!!.id, title, content, date, uriString, mDefaultTittleTextColor, mDefaultTittleBackColor, mDefaultContentTextColor, mDefaultContentBackColor, titleTextSize, contentTextSize, titleFont, contentFont)
                    val intent = Intent().apply {
                        putExtra("diary", diary)
                        putExtra("flag", 1)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

        //size Picker Listener
        binding.textSizePicker.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.Title.textSize = binding.textSizePicker.selectedItem.toString().toFloat()
                binding.Content.textSize = binding.textSizePicker.selectedItem.toString().toFloat()

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("on nothing selected")
            }
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("on item click text size")
            }
        }

        //font picker settings
        binding.fontPicker.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (binding.fontPicker.selectedItem) {
                    "bold" -> {binding.Content.setTypeface(null, Typeface.BOLD)
                        binding.Title.setTypeface(null, Typeface.BOLD)}
                    "normal" ->  {binding.Content.setTypeface(null, Typeface.NORMAL)
                        binding.Title.setTypeface(null, Typeface.NORMAL) }
                    "italic" -> { binding.Content.setTypeface(null, Typeface.ITALIC)
                        binding.Title.setTypeface(null, Typeface.ITALIC) }
                    "bold | italic" -> { binding.Content.setTypeface(null, Typeface.BOLD_ITALIC)
                        binding.Title.setTypeface(null, Typeface.BOLD_ITALIC) }
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

        //image select
        binding.imageView.setOnClickListener {

            //갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }
    }


    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        //결과 코드 OK , 결과값 null 아니면
        if(it.resultCode == RESULT_OK && it.data != null){
            //값 담기
            uri  = it.data!!.data!!
            uriString = uri.toString()

            binding.imageView.setImageURI(Uri.parse(uriString))
            binding.imageView.tag = uriString
            println("tag ========== ${binding.imageView.tag}")

            //화면에 보여주기
            //Glide.with(this)
            //    .load(uri) //이미지
            //    .into(binding.imageView) //보여줄 위치
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.colors_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            //0 - > textColor 1 -> backgroundColor
            R.id.title_text_color -> { openColorPickerDialogue(0, binding.Title, 1) }
            R.id.title_background_color -> { openColorPickerDialogue(1, binding.Title, 2) }
            R.id.content_text_color -> { openColorPickerDialogue(0, binding.Content, 3) }
            R.id.content_background_color -> { openColorPickerDialogue(1, binding.Content, 4)}

        }
        return super.onOptionsItemSelected(item)
    }

    private fun openColorPickerDialogue(type: Int, selectedField: EditText, saveColorVal: Int) {

        // the WarnDialog callback needs 3 parameters
        // one is the context, second is default color,
        val colorPickerDialogue = AmbilWarnaDialog(this, mDefaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // leave this function body as
                    // blank, as the dialog
                    // automatically closes when
                    // clicked on cancel button
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // change the mDefaultColor to
                    // change the GFG text color as
                    // it is returned when the OK
                    // button is clicked from the
                    // color picker dialog
                    mDefaultColor = color
                    when(saveColorVal) {
                        1 -> mDefaultTittleTextColor = mDefaultColor
                        2 -> mDefaultTittleBackColor = mDefaultColor
                        3 -> mDefaultContentTextColor = mDefaultColor
                        4 -> mDefaultContentBackColor = mDefaultColor
                    }

                    // now change the picked color
                    // preview box to mDefaultColor
                    if (type == 0)
                        selectedField.setTextColor(mDefaultColor)
                    else if (type == 1)
                        selectedField.setBackgroundColor(mDefaultColor)
                }
            })
        colorPickerDialogue.show()
    }
}