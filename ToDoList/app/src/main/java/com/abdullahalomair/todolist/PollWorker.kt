package com.abdullahalomair.todolist
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*
private const val TAG = "PollWorker"
class PollWorker(val context: Context,
                 workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val tasksRepository = TasksRepository.get()
        val tasks = tasksRepository.getTasksBackground()

        for (task in tasks){
            val duration = Date().time - task.date.time
            val hours: Long = ((duration / (1000 * 60 * 60)) % 24)
            val days: Long = ((duration / (1000 * 60 * 60 * 24)) % 365)
            val dayApartNot = (days.equals(1))
            val hourApart = (hours.equals(0))
            if (dayApartNot) {
                val time = SimpleDateFormat("hh:mm aa").format(task.date)
                createNotification("${context.getString(R.string.day_apart)}${task.title}",
                "${task.description} <br> " +
                        "${context.getString(R.string.day_apart_description)} $time")
            }
            else if (hourApart){
                val time = SimpleDateFormat("hh:mm aa").format(task.date)
                createNotification("${task.title} ${context.getString(R.string.hour_apart)}",
                        "${task.description} \n" +
                                "${context.getString(R.string.day_apart_description)} $time")
            }
        }




        return Result.success()
    }

    private fun createNotification(title: String, description: String) {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notification = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker("Task ")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, notification)

    }
}