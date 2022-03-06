package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.text.TextWatcher
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_QZ
import com.suda.yzune.wakeupschedule.schedule_import.bean.CourseHtmlApi
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_html_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class HtmlImportFragment : BaseFragment() {

    private val viewModel by activityViewModels<ImportViewModel>()
    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_html_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pb_loading.visibility = View.INVISIBLE
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)

        ib_back.setOnClickListener {
            activity!!.finish()
        }

        login_uid.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.uid = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        login_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.pwd = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        fab_import.setOnClickListener {
            viewModel.importType = TYPE_QZ
            val uid = viewModel.uid.toString()
            val pwd = viewModel.pwd.toString()
            pb_loading.visibility = View.VISIBLE
            if (login_uid.text.isNullOrEmpty() || login_pwd.text.isNullOrEmpty()) {
                Toasty.error(activity!!,
                    "请先输入账号密码", Toast.LENGTH_LONG).show()
                pb_loading.visibility = View.INVISIBLE
                return@setOnClickListener
            }
            launch {
                try {
                    val formBody = FormBody.Builder()
                        .add("uid", uid)
                        .add("pwd", pwd)
                        .build()

                    val request = Request.Builder()
                        .url(viewModel.apiUrl)
                        .post(formBody)
                        .build()
                    val response = withContext(Dispatchers.IO) {
                        httpClient.newCall(request).execute()
                    }
                    if (response.isSuccessful) {
                        pb_loading.visibility = View.INVISIBLE
                        val result = withContext(Dispatchers.IO) { response.body()?.string() }
                        if (result != null) {
                            val gson = Gson()
                            val info = gson.fromJson(result,CourseHtmlApi::class.java)
                            if (info.code == 200) {
                                val result = viewModel.importSchedule(info.html)
                                Toasty.success(activity!!, "成功导入 $result 门课程(ﾟ▽ﾟ)/\n请在右侧栏切换后查看").show()
                                activity!!.setResult(RESULT_OK)
                                activity!!.finish()
                            } else {
                                throw Exception("error")
                            }
                        } else {
                            throw Exception("error")
                        }
                    } else {
                        throw Exception("error")
                    }
                } catch (e: Exception) {
                    pb_loading.visibility = View.INVISIBLE
                    Toasty.error(activity!!,
                        "导入失败>_<\n${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_IMPORT_HTML && resultCode == RESULT_OK) {
            viewModel.htmlUri = data?.data
        }
        if (requestCode == Const.REQUEST_CODE_CHOOSE_SCHOOL && resultCode == RESULT_OK) {
            viewModel.importType = data!!.getStringExtra("type")
        }
    }

}