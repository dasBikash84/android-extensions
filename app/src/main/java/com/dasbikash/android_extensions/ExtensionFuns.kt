package com.dasbikash.android_extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.text.NumberFormat

/**
 * Extension function to hide view
 * */
fun View.hide(){
    visibility = View.GONE
}

/**
 * Extension function to show view
 * */
fun View.show(){
    visibility = View.VISIBLE
}

/**
 * Extension function to make view invisible
 * */
fun View.makeInVisible(){
    visibility = View.INVISIBLE
}

/**
 * Extension function to toggle view visibility
 * */
fun View.toggle(){
    if (visibility== View.GONE) {
        visibility = View.VISIBLE
    }else{
        visibility = View.GONE
    }
}

/**
 * ```
 * Extension function to run task with fragment context.
 * Task will run only if fragment is active i.e. not destroyed.
 *```
 * @param task subject task into which fragment context will be injected as parameter
 * @return Generic return value of task
 * */
fun <R> Fragment.runWithContext(task:(Context)->R){
    runIfNotDestroyed {
        context?.let { task(it) }
    }
}

/**
 * ```
 * Extension function to run task with fragment activity.
 * Task will run only if fragment is is active i.e. not destroyed.
 *```
 *
 * @param task subject task into which fragment activity will be injected as parameter
 * @return Generic return value of task
 * */
fun <R> Fragment.runWithActivity(task:(Activity)->R){
    runIfNotDestroyed {
        activity?.let { task(it) }
    }
}

/**
 * Extension function on Activity class to start activity of given type.
 *
 * @param type java Class type of given activity class
 * */
fun <T: Activity> Activity.startActivity(type:Class<T>){
    val intent = Intent(this.applicationContext,type)
    startActivity(intent)
}

/**
 * Extension function on Activity class to start activity of given type for result.
 *
 * @param type java Class type of given activity class
 * */
fun <T: Activity> Activity.startActivityForResult(type:Class<T>,
                                                  requestCode:Int){
    val intent = Intent(this.applicationContext,type)
    startActivityForResult(intent,requestCode)
}
/**
 * Extension function on AppCompatActivity class to start activity of given type.
 *
 * @param type java Class type of given activity class
 * */
fun <T: AppCompatActivity> AppCompatActivity.startActivity(type:Class<T>){
    val intent = Intent(this.applicationContext,type)
    startActivity(intent)
}
/**
 * Extension function on AppCompatActivity class to start activity of given type for result.
 *
 * @param type java Class type of given activity class
 * */
fun <T: AppCompatActivity> AppCompatActivity.startActivityForResult(type:Class<T>,
                                                                    requestCode:Int){
    val intent = Intent(this.applicationContext,type)
    startActivityForResult(intent,requestCode)
}

/**
 * ```
 * Extension function on launch task on main thread with optional delay.
 * If launched from Fragment/Activity then will run the task only if that is not destroyed.
 * ```
 *
 * @param task posted functional parameter
 * @param delayMs optional delay in milli-seconds
 * */
fun Any.runOnMainThread(task: () -> Any?,delayMs:Long=0L){
    Handler(Looper.getMainLooper()).postDelayed( {
        if (this is LifecycleOwner){
            runIfNotDestroyed { task() }
        }else if(this is Activity){
            runIfNotDestroyed { task() }
        }else {
            task()
        }
    },delayMs)
}

/**
 * Extension function on check if running on Main/UI thread
 *
 * @return true if on main thread else false
 *
 */
fun isOnMainThread() = (Thread.currentThread() == Looper.getMainLooper().thread)

/**
 * Get currency string for double value
 *
 * @return currency string for subject value
 * */
fun Double.getCurrencyString():String{
    return NumberFormat.getCurrencyInstance().format(this).substring(1)
}

/**
 * Get currency string for Long value
 *
 * @return currency string for subject value
 * */
fun Long.getCurrencyString():String{
    return NumberFormat.getCurrencyInstance().format(this).substring(1)
}

/**
 * Extension function on launch task from activity
 * Task will run only if activity is not destroyed
 *
 * @param task posted functional parameter
 * */
fun Activity.runIfNotDestroyed(task:()->Any?){
    if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !isDestroyed
        } else {
            true
        }
    ) {
        task()
    }
}

/**
 * Extension function on launch task from any LifecycleOwner
 * Task will run only if LifecycleOwner is not destroyed
 *
 * @param task posted functional parameter
 * */
fun LifecycleOwner.runIfNotDestroyed(task:()->Any?){
    if (this.lifecycle.currentState != Lifecycle.State.DESTROYED){
        task()
    }
}

/**
 * Extension function on launch task from any LifecycleOwner
 * Task will run only if LifecycleOwner is resumed
 *
 * @param task posted functional parameter
 * */
fun LifecycleOwner.runIfResumed(task:()->Any?){
    if (this.lifecycle.currentState == Lifecycle.State.RESUMED){
        task()
    }
}