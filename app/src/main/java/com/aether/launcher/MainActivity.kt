package com.aether.launcher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var apps: LinearLayout
    private lateinit var search: EditText
    private val accent = Color.rgb(100, 220, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 11, 23)
        window.navigationBarColor = Color.rgb(8, 11, 23)
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 42, 32, 24)
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(8, 11, 23), Color.rgb(21, 19, 46), Color.rgb(5, 19, 30))
            )
        }
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            addView(root)
        }
        setContentView(scroll)
        label("A E T H E R   /   O S", 14, accent)
        label(SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()), 16, Color.LTGRAY)
        label(SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()), 58, Color.WHITE)
        label("YOUR SPACE. REIMAGINED.", 12, Color.LTGRAY)
        spacer(30)
        search = EditText(this).apply {
            hint = "Search your apps..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            setSingleLine(true)
            setPadding(22, 16, 22, 16)
            background = GradientDrawable().apply {
                setColor(Color.rgb(34, 39, 60))
                cornerRadius = 26f
            }
        }
        root.addView(search, LinearLayout.LayoutParams(-1, -2))
        spacer(24)
        label("APPLICATIONS", 14, accent)
        apps = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(apps)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                populate(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        populate("")
    }

    private fun populate(filter: String) {
        apps.removeAllViews()
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val installed = packageManager.queryIntentActivities(intent, 0)
            .sortedBy { it.loadLabel(packageManager).toString().lowercase() }
            .filter {
                it.activityInfo.packageName != packageName &&
                    it.loadLabel(packageManager).toString().contains(filter, ignoreCase = true)
            }
        if (installed.isEmpty()) labelIn(apps, "No matching apps", 15, Color.GRAY)
        for (entry in installed) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(12, 18, 12, 18)
            }
            val icon = ImageView(this).apply {
                setImageDrawable(entry.loadIcon(packageManager))
            }
            row.addView(icon, LinearLayout.LayoutParams(48, 48))
            val name = TextView(this).apply {
                text = entry.loadLabel(packageManager)
                textSize = 17f
                setTextColor(Color.WHITE)
                setPadding(20, 0, 0, 0)
            }
            row.addView(name)
            row.setOnClickListener {
                try {
                    startActivity(
                        Intent(Intent.ACTION_MAIN)
                            .addCategory(Intent.CATEGORY_LAUNCHER)
                            .setClassName(entry.activityInfo.packageName, entry.activityInfo.name)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } catch (_: Exception) {
                    Toast.makeText(this, "Unable to open app", Toast.LENGTH_SHORT).show()
                }
            }
            apps.addView(row)
        }
    }

    private fun label(t: String, size: Int, color: Int) = labelIn(root, t, size, color)

    private fun labelIn(parent: LinearLayout, t: String, size: Int, color: Int) {
        parent.addView(TextView(this).apply {
            text = t
            textSize = size.toFloat()
            setTextColor(color)
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            setPadding(0, 10, 0, 10)
        })
    }

    private fun spacer(h: Int) {
        root.addView(View(this), LinearLayout.LayoutParams(1, h))
    }
}
