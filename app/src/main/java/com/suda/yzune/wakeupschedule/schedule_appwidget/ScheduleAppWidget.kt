package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.suda.yzune.wakeupschedule.AppDatabase

import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlin.coroutines.experimental.coroutineContext

/**
 * Implementation of App Widget functionality.
 */
class ScheduleAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        try {
            widgetDao.insertAppWidget(AppWidgetBean(appWidgetIds[0], 0, 0))
        } catch (e: SQLiteConstraintException) {

        }

        for (appWidgetId in widgetDao.getIdsByTypes(0, 0)) {
            Log.d("小部件id", appWidgetId.toString())
            AppWidgetUtils.refreshScheduleWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        for (id in appWidgetIds) {
            widgetDao.deleteAppWidget(id)
        }
    }

}

