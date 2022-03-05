package com.suda.yzune.wakeupschedule.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.*
import com.suda.yzune.wakeupschedule.utils.Const
import kotlinx.android.synthetic.main.fragment_import_choose.*
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy


class ImportChooseFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_import_choose

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build()
        )
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build()
        )

        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_school.setOnClickListener {
            activity!!.startActivityForResult(Intent(activity, LoginWebActivity::class.java).apply {
                putExtra("school_name", "北京科技大学天津学院")
                putExtra("import_type", Common.TYPE_QZ)
                putExtra("url", "http://117.131.241.67:89/jsxsd/")
            }, Const.REQUEST_CODE_IMPORT)
            dismiss()
        }
    }
}