package com.suda.yzune.wakeupschedule.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.dao.TableDao
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.settings.items.*
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.getPrefer
import splitties.activities.start
import splitties.resources.color
import splitties.snackbar.longSnack
import splitties.snackbar.snack

class SettingsActivity : BaseListActivity() {

    private lateinit var dataBase: AppDatabase
    private lateinit var tableDao: TableDao
    private val dayNightTheme by lazy(LazyThreadSafetyMode.NONE) {
        resources.getStringArray(R.array.day_night_setting)
    }
    private var dayNightIndex = 2

    private val mAdapter = SettingItemAdapter()

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            null
        } else {
            tvButton
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBase = AppDatabase.getDatabase(application)
        tableDao = dataBase.tableDao()
        dayNightIndex = getPrefer().getInt(Const.KEY_DAY_NIGHT_THEME, 2)

        val items = mutableListOf<BaseSettingItem>()
        onItemsCreated(items)
        mAdapter.data = items
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.itemAnimator?.changeDuration = 250
        mRecyclerView.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.anko_check_box)
        mAdapter.setOnItemChildClickListener { _, view, position ->
            when (val item = items[position]) {
                is SwitchItem -> onSwitchItemCheckChange(item, view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).isChecked)
            }
        }
        mAdapter.setOnItemClickListener { _, view, position ->
            when (val item = items[position]) {
                is HorizontalItem -> onHorizontalItemClick(item, position)
                is VerticalItem -> onVerticalItemClick(item)
                is SwitchItem -> view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).performClick()
            }
        }
    }

    private fun onItemsCreated(items: MutableList<BaseSettingItem>) {

        items.add(CategoryItem("??????", true))
        items.add(HorizontalItem("??????????????????", "????????????"))
        items.add(SwitchItem("???????????????????????????", getPrefer().getBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, true), ""))
        items.add(SwitchItem("???????????????", getPrefer().getBoolean(Const.KEY_SCHEDULE_PRE_LOAD, true), "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????App???????????????????????????"))
        items.add(SwitchItem("??????????????????????????????", getPrefer().getBoolean(Const.KEY_SCHEDULE_BLANK_AREA, true), "????????????????????????????????????????????????????????????????????????????????????????????????????????????"))
        items.add(SwitchItem("?????????????????????", getPrefer().getBoolean(Const.KEY_DAY_WIDGET_COLOR, false)))
        items.add(SwitchItem("?????????????????????", getPrefer().getBoolean(Const.KEY_SHOW_EMPTY_VIEW, true)))
        items.add(HorizontalItem("????????????", dayNightTheme[dayNightIndex]))
        items.add(VerticalItem("", "\n\n\n"))
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "???????????????" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_PRE_LOAD, isChecked)
                }
                mRecyclerView.snack("??????App????????????")
            }
            "??????????????????????????????" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_BLANK_AREA, isChecked)
                }
                mRecyclerView.snack("??????App????????????")
            }
            "???????????????????????????" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, isChecked)
                }
                mRecyclerView.snack("??????App????????????")
            }
            "?????????????????????" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SHOW_EMPTY_VIEW, isChecked)
                }
                mRecyclerView.snack("????????????????????????")
            }
            "?????????????????????" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_DAY_WIDGET_COLOR, isChecked)
                }
                mRecyclerView.longSnack("????????????????????????????????????????????????????????????~")
            }
        }
        item.checked = isChecked
    }

    private fun onHorizontalItemClick(item: HorizontalItem, position: Int) {
        when (item.title) {
            "??????????????????" -> {
                launch {
                    val table = tableDao.getDefaultTable()
                    startActivityForResult(
                            Intent(this@SettingsActivity, ScheduleSettingsActivity::class.java).apply {
                                putExtra("tableData", table)
                            }, 180)
                }
            }
            "????????????" -> {
                MaterialAlertDialogBuilder(this)
                        .setTitle("????????????")
                        .setPositiveButton("??????") { _, _ ->
                            getPrefer().edit {
                                putInt(Const.KEY_DAY_NIGHT_THEME, dayNightIndex)
                            }
                            item.value = dayNightTheme[dayNightIndex]
                            mAdapter.notifyItemChanged(position)
                            when (dayNightIndex) {
                                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                2 -> {
                                    when {
                                        Build.VERSION.SDK_INT >= 29 -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                                        }
                                        Build.VERSION.SDK_INT >= 23 -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                                        }
                                        else -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                        }
                                    }
                                }
                            }
                        }
                        .setSingleChoiceItems(dayNightTheme, dayNightIndex) { _, which ->
                            dayNightIndex = which
                        }
                        .show()
            }
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "??????????????????" -> {
                start<AdvancedSettingsActivity>()
            }
            "??????????????????????????????" -> {
                start<AdvancedSettingsActivity>()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 180) {
            setResult(RESULT_OK)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
