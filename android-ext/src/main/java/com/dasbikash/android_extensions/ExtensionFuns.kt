package com.dasbikash.android_extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
 * Extension function on Fragment to run task with context.
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
 * Extension function on Fragment to run task with activity.
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
 * Extension function to launch task on main thread with optional delay.
 * If launched from Fragment/Activity then will run the task if not destroyed.
 * ```
 *
 * @param task posted functional parameter
 * @param delayMs optional delay in milli-seconds
 * */
fun Any.runOnMainThread(task: () -> Any?,delayMs:Long=0L){
    Handler(Looper.getMainLooper()).postDelayed( {
        if (this is AppCompatActivity){
            runIfNotDestroyed { task() }
        }else if(this is Fragment){
            runIfNotDestroyed { task() }
        }else if(this is Activity){
            runIfNotDestroyed { task() }
        }else {
            task()
        }
    },delayMs)
}

/**
 * Extension function to check if running on Main/UI thread
 *
 * @return true if on main thread else false
 *
 */
fun isOnMainThread() = (Thread.currentThread() == Looper.getMainLooper().thread)

/**
 * ```
 * Extension function to launch task from activity
 * Task will run only if activity is not destroyed
 *```
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
 * ```
 * Extension function to launch task from any AppCompatActivity
 * Task will run only if not destroyed
 *```
 *
 * @param task posted functional parameter
 * */
fun AppCompatActivity.runIfNotDestroyed(task:()->Any?){
    if (this.lifecycle.currentState != Lifecycle.State.DESTROYED){
        task()
    }
}

/**
 * ```
 * Extension function to launch task from any Fragment
 * Task will run only if not destroyed
 *```
 *
 * @param task posted functional parameter
 * */
fun Fragment.runIfNotDestroyed(task:()->Any?){
    if (this.lifecycle.currentState != Lifecycle.State.DESTROYED){
        task()
    }
}

/**
 * Extension function to launch task from AppCompatActivity.
 * Task will run only if resumed.
 *
 * @param task posted functional parameter
 * */
fun AppCompatActivity.runIfResumed(task:()->Any?){
    if (this.lifecycle.currentState == Lifecycle.State.RESUMED){
        task()
    }
}

/**
 * Extension function to launch task from any Fragment.
 * Task will run only if resumed.
 *
 * @param task posted functional parameter
 * */
fun Fragment.runIfResumed(task:()->Any?){
    if (this.lifecycle.currentState == Lifecycle.State.RESUMED){
        task()
    }
}

/**
 * Extension Method to display Html text on textview.
 *
 * @param text | text containing Html tags
 * */
fun TextView.displayHtmlText(text: String) {
    this.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

/**
 * Method for Dp to Pixel conversion
 *
 * @param context | Android Context
 * @param dp | Dp in Int
 * @return returns pixels in float
 * */
fun Any.dpToPx(dp: Int, context: Context): Float =
    (dp * context.getResources().getDisplayMetrics().density)

/**
 * Method for Pixel to Dp conversion
 *
 * @param context | Android Context
 * @param px | pixels in float
 * @return returns Dp in float
 * */
fun Any.pxToDp(px: Int, context: Context): Float =
    (px / context.getResources().getDisplayMetrics().density)

/**
 * Extension function on launch async task
 * suspending any suspension function
 *
 * @param task posted functional parameter
 * */
suspend fun <T:Any> runSuspended(task:()->T):T {
    coroutineContext().let {
        return withContext(it) {
            return@withContext async(Dispatchers.IO) { task() }.await()
        }
    }
}

/**
 * Extension function on access CoroutineContext from inside of any suspension function
 *
 * @return subject CoroutineContext
 * */
suspend fun coroutineContext(): CoroutineContext = suspendCoroutine { it.resume(it.context) }