package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import androidx.core.content.edit
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

object UpdateUtils {

    @Throws(Exception::class)
    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionCode
    }

    @Throws(Exception::class)
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionName
    }

    suspend fun tranOldData(context: Context) {
        if (context.getPrefer().getBoolean("has_intro", false) &&
                !context.getPrefer().getBoolean("has_adjust", false)) {
            val tableData = TableBean(
                    tableName = "",
                    itemHeight = context.getPrefer().getInt("item_height", 56),
                    maxWeek = context.getPrefer().getInt("sb_weeks", 30),
                    itemTextSize = context.getPrefer().getInt("sb_text_size", 12),
                    showOtherWeekCourse = context.getPrefer().getBoolean("s_show", false),
                    showTime = context.getPrefer().getBoolean("s_show_time_detail", false),
                    showSat = context.getPrefer().getBoolean("s_show_sat", true),
                    showSun = context.getPrefer().getBoolean("s_show_weekend", true),
                    sundayFirst = context.getPrefer().getBoolean("s_sunday_first", false),
                    nodes = context.getPrefer().getInt("classNum", 11),
                    itemAlpha = context.getPrefer().getInt("sb_alpha", 60),
                    background = context.getPrefer().getString(Const.KEY_OLD_VERSION_BG_URI, "")!!,
                    startDate = context.getPrefer().getString(Const.KEY_OLD_VERSION_TERM_START, "2019-02-25")!!,
                    widgetItemAlpha = context.getPrefer().getInt("sb_widget_alpha", 60),
                    widgetItemHeight = context.getPrefer().getInt("widget_item_height", 56),
                    widgetItemTextSize = context.getPrefer().getInt("sb_widget_text_size", 12),
                    type = 1,
                    id = 1)

            if (!context.getPrefer().getBoolean("s_stroke", true)) {
                tableData.strokeColor = 0x00ffffff
            }

            if (context.getPrefer().getBoolean("s_color", false)) {
                tableData.textColor = 0xff000000.toInt()
            }

            if (context.getPrefer().getBoolean("s_widget_color", false)) {
                tableData.widgetTextColor = 0xff000000.toInt()
            }

            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()
            val timeDao = dataBase.timeDetailDao()
            val widgetDao = dataBase.appWidgetDao()

            try {
                tableDao.updateTable(tableData)
                widgetDao.updateFromOldVer()
                if (!context.getPrefer().getBoolean("isInitTimeTable", false)) {
                    val timeList = ArrayList<TimeDetailBean>().apply {
                        add(TimeDetailBean(1, "08:00", "08:45", 1))
                        add(TimeDetailBean(2, "08:50", "09:35", 1))
                        add(TimeDetailBean(3, "09:55", "10:40", 1))
                        add(TimeDetailBean(4, "10:45", "11:30", 1))
                        add(TimeDetailBean(5, "13:10", "13:55", 1))
                        add(TimeDetailBean(6, "14:00", "14:45", 1))
                        add(TimeDetailBean(7, "15:00", "15:45", 1))
                        add(TimeDetailBean(8, "15:50", "16:35", 1))
                        add(TimeDetailBean(9, "16:50", "17:35", 1))
                        add(TimeDetailBean(10, "17:40", "18:25", 1))
                        add(TimeDetailBean(11, "19:10", "19:55", 1))
                        add(TimeDetailBean(12, "20:00", "20:45", 1))
                        add(TimeDetailBean(13, "20:50", "21:35", 1))
                        add(TimeDetailBean(14, "00:00", "00:00", 1))
                        add(TimeDetailBean(15, "00:00", "00:00", 1))
                        add(TimeDetailBean(16, "00:00", "00:00", 1))
                        add(TimeDetailBean(17, "00:00", "00:00", 1))
                        add(TimeDetailBean(18, "00:00", "00:00", 1))
                        add(TimeDetailBean(19, "00:00", "00:00", 1))
                        add(TimeDetailBean(20, "00:00", "00:00", 1))
                        add(TimeDetailBean(21, "00:00", "00:00", 1))
                        add(TimeDetailBean(22, "00:00", "00:00", 1))
                        add(TimeDetailBean(23, "00:00", "00:00", 1))
                        add(TimeDetailBean(24, "00:00", "00:00", 1))
                        add(TimeDetailBean(25, "00:00", "00:00", 1))
                        add(TimeDetailBean(26, "00:00", "00:00", 1))
                        add(TimeDetailBean(27, "00:00", "00:00", 1))
                        add(TimeDetailBean(28, "00:00", "00:00", 1))
                        add(TimeDetailBean(29, "00:00", "00:00", 1))
                        add(TimeDetailBean(30, "00:00", "00:00", 1))
                    }
                    timeDao.insertTimeList(timeList)
                }

                context.getPrefer().edit {
                    remove("termStart")
                    remove("item_height")
                    remove("sb_weeks")
                    remove("sb_text_size")
                    remove("s_show")
                    remove("s_show_time_detail")
                    remove("s_show_sat")
                    remove("s_show_weekend")
                    remove("s_sunday_first")
                    remove("classNum")
                    remove("sb_alpha")
                    remove("pic_uri")
                    remove("sb_widget_alpha")
                    remove("widget_item_height")
                    remove("sb_widget_text_size")
                    remove("s_stroke")
                    remove("s_color")
                    remove("s_widget_color")
                }

                context.getPrefer().edit {
                    putBoolean(Const.KEY_HAS_ADJUST, true)
                }
            } catch (e: Exception) {

            }

        }

        if (!context.getPrefer().getBoolean("has_intro", false) &&
                !context.getPrefer().getBoolean("has_adjust", false)) {
            val tableData = TableBean(type = 1, id = 1, tableName = "")
            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()
            val timeDao = dataBase.timeDetailDao()
            val timeTableDao = dataBase.timeTableDao()
            if (timeTableDao.getTimeTable(1) == null) {
                timeTableDao.insertTimeTable(TimeTableBean(id = 1, name = "默认"))
            }
            val timeList = ArrayList<TimeDetailBean>().apply {
                add(TimeDetailBean(1, "08:00", "08:45", 1))
                add(TimeDetailBean(2, "08:50", "09:35", 1))
                add(TimeDetailBean(3, "09:55", "10:40", 1))
                add(TimeDetailBean(4, "10:45", "11:30", 1))
                add(TimeDetailBean(5, "13:10", "13:55", 1))
                add(TimeDetailBean(6, "14:00", "14:45", 1))
                add(TimeDetailBean(7, "15:00", "15:45", 1))
                add(TimeDetailBean(8, "15:50", "16:35", 1))
                add(TimeDetailBean(9, "16:50", "17:35", 1))
                add(TimeDetailBean(10, "17:40", "18:25", 1))
                add(TimeDetailBean(11, "19:10", "19:55", 1))
                add(TimeDetailBean(12, "20:00", "20:45", 1))
                add(TimeDetailBean(13, "20:50", "21:35", 1))
                add(TimeDetailBean(14, "00:00", "00:00", 1))
                add(TimeDetailBean(15, "00:00", "00:00", 1))
                add(TimeDetailBean(16, "00:00", "00:00", 1))
                add(TimeDetailBean(17, "00:00", "00:00", 1))
                add(TimeDetailBean(18, "00:00", "00:00", 1))
                add(TimeDetailBean(19, "00:00", "00:00", 1))
                add(TimeDetailBean(20, "00:00", "00:00", 1))
                add(TimeDetailBean(21, "00:00", "00:00", 1))
                add(TimeDetailBean(22, "00:00", "00:00", 1))
                add(TimeDetailBean(23, "00:00", "00:00", 1))
                add(TimeDetailBean(24, "00:00", "00:00", 1))
                add(TimeDetailBean(25, "00:00", "00:00", 1))
                add(TimeDetailBean(26, "00:00", "00:00", 1))
                add(TimeDetailBean(27, "00:00", "00:00", 1))
                add(TimeDetailBean(28, "00:00", "00:00", 1))
                add(TimeDetailBean(29, "00:00", "00:00", 1))
                add(TimeDetailBean(30, "00:00", "00:00", 1))
            }
            try {
                timeDao.insertTimeList(timeList)
                tableDao.insertTable(tableData)
            } catch (e: Exception) {

            }
            context.getPrefer().edit {
                putBoolean(Const.KEY_HAS_ADJUST, true)
            }
        }
    }
}