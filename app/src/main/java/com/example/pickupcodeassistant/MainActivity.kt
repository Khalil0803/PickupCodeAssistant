package com.example.pickupcodeassistant

import android.app.Activity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.text.InputType

class MainActivity : Activity() {

    private lateinit var container: LinearLayout
    private lateinit var codeInput: EditText
    private lateinit var listText: TextView
    private val codes = mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMain()
    }

    private fun showMain() {
        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 36, 36, 36)
            gravity = Gravity.TOP
        }

        val title = TextView(this).apply {
            text = "取件码助手"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
        }
        container.addView(title)

        val desc = TextView(this).apply {
            text = "先体验手动添加与一键复制，下一步再接入短信和通知自动识别"
            textSize = 14f
            setPadding(0, 12, 0, 24)
        }
        container.addView(desc)

        codeInput = EditText(this).apply {
            hint = "输入取件码，例如 A123"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        container.addView(codeInput)

        val stationInput = EditText(this).apply {
            hint = "驿站名称（可选）"
        }
        container.addView(stationInput)

        val addButton = Button(this).apply {
            text = "添加取件码"
            setOnClickListener {
                val code = codeInput.text.toString().trim()
                val station = stationInput.text.toString().trim()
                if (code.isEmpty()) {
                    Toast.makeText(this@MainActivity, "请先输入取件码", Toast.LENGTH_SHORT).show()
                } else {
                    codes.add(code to station)
                    codeInput.text.clear()
                    stationInput.text.clear()
                    refreshList()
                }
            }
        }
        container.addView(addButton)

        listText = TextView(this).apply {
            textSize = 16f
            setPadding(0, 24, 0, 0)
        }
        container.addView(listText)

        setContentView(container)
        refreshList()
    }

    private fun refreshList() {
        if (!::listText.isInitialized) return

        listText.text = if (codes.isEmpty()) {
            "目前没有取件码"
        } else {
            codes.mapIndexed { index, item ->
                "${index + 1}. 取件码：${item.first}" +
                    if (item.second.isNotBlank()) "\n   驿站：${item.second}" else ""
            }.joinToString("\n\n")
        }

        container.removeViews(4, container.childCount - 4)

        codes.forEachIndexed { index, item ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val info = TextView(this).apply {
                text = "${item.first}  ${item.second}"
                textSize = 16f
            }
            row.addView(info, LinearLayout.LayoutParams(0, -2, 1f))

            val copyButton = Button(this).apply {
                text = "复制"
                setOnClickListener {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("取件码", item.first))
                    Toast.makeText(this@MainActivity, "已复制取件码", Toast.LENGTH_SHORT).show()
                }
            }
            row.addView(copyButton)

            val deleteButton = Button(this).apply {
                text = "删除"
                setOnClickListener {
                    codes.removeAt(index)
                    showMain()
                }
            }
            row.addView(deleteButton)

            container.addView(row)
        }
    }
}
