package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.button)

        val constraint = Constraints.Builder().setRequiresCharging(true).build()
        val data: Data = Data.Builder().putString("key1","Hello from activity!").build()


        val worker3 = PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES).build()

        // запускайся каждые 20 минут в пределах часа
        val worker4 = PeriodicWorkRequestBuilder<MyWorker2>(1, TimeUnit.HOURS, 20, TimeUnit.MINUTES);

        val worker = OneTimeWorkRequestBuilder<MyWorker>()
            .setInputData(data)
            .build()
        val worker2 = OneTimeWorkRequestBuilder<MyWorker2>()
            .setConstraints(constraint)
            .build()

        val list: ArrayList<WorkRequest> = ArrayList()
        list.add(worker)
        list.add(worker2)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(worker.id).observe(
            this, Observer { data: WorkInfo? ->
                if(data != null) {
                    Log.d("RRR", "Worker1: ${data.id} => ${data.state}")
                    data.outputData.getString("key2")?.let { Log.d("RRR", it) }
                }
            }
        )

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(worker2.id).observe(
            this, Observer { data: WorkInfo? ->
                if(data != null) {
                    Log.d("RRR", "Worker2: ${data.id} => ${data.state}")
                }
            }
        )


        btn.setOnClickListener {
            // парал. запуск воркеров
            WorkManager.getInstance(this).enqueue(list)

            // последовательный запуск воркеров
           /* WorkManager.getInstance(this)
                .beginWith(worker)
                .then(worker2)
                .enqueue()

            */
        }
    }
}