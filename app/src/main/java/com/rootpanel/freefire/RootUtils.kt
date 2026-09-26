package com.rootpanel.freefire

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class RootUtils {
    
    private val TAG = "RootUtils"
    private val ROOT_TIMEOUT_SECONDS = 5L
    
    /**
     * Check karo ke device rooted hai ya nahi
     * Improved with timeout to prevent hanging
     */
    fun testRootAccess(): Boolean {
        return try {
            Log.d(TAG, "Testing root access...")
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            
            // Set timeout to prevent infinite waiting
            val completed = process.waitFor(ROOT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            
            if (!completed) {
                Log.w(TAG, "Root test timed out")
                process.destroyForcibly()
                return false
            }
            
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()
            reader.close()
            process.destroy()
            
            Log.d(TAG, "Root test output: $output")
            val hasRoot = output.contains("uid=0") || output.isNotEmpty()
            Log.d(TAG, "Root access: $hasRoot")
            
            hasRoot
        } catch (e: Exception) {
            Log.e(TAG, "Root test failed: ${e.message}", e)
            false
        }
    }
    
    /**
     * Root command execute karo (without su prefix)
     */
    fun executeCommand(command: String): String {
        return try {
            Log.d(TAG, "Executing command: $command")
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            
            val completed = process.waitFor(ROOT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            
            if (!completed) {
                Log.w(TAG, "Command execution timed out: $command")
                process.destroyForcibly()
                return ""
            }
            
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            
            reader.use {
                it.forEachLine { line ->
                    output.append(line).append("\n")
                }
            }
            
            process.destroy()
            val result = output.toString()
            Log.d(TAG, "Command output: $result")
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Command execution error: ${e.message}", e)
            ""
        }
    }
    
    /**
     * su -c command execute karo (Magisk compatible)
     * Improved with timeout handling
     */
    fun executeSuperUserCommand(command: String): String {
        return try {
            Log.d(TAG, "Executing superuser command: $command")
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            
            val completed = process.waitFor(ROOT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            
            if (!completed) {
                Log.w(TAG, "Superuser command timed out: $command")
                process.destroyForcibly()
                return ""
            }
            
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            
            reader.use {
                it.forEachLine { line ->
                    output.append(line).append("\n")
                }
            }
            
            process.destroy()
            val result = output.toString()
            Log.d(TAG, "Superuser command output: $result")
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Superuser command execution error: ${e.message}", e)
            ""
        }
    }
    
    /**
     * Process ID nikalo
     */
    fun getPID(packageName: String): Int {
        return try {
            Log.d(TAG, "Getting PID for package: $packageName")
            val result = executeCommand("pidof $packageName")
            val pid = result.trim().split(" ").firstOrNull()?.toIntOrNull() ?: -1
            Log.d(TAG, "PID for $packageName: $pid")
            pid
        } catch (e: Exception) {
            Log.e(TAG, "Error getting PID: ${e.message}", e)
            -1
        }
    }
    
    /**
     * Package installed hai ya nahi check karo
     */
    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            Log.d(TAG, "Checking if package installed: $packageName")
            val result = executeCommand("pm list packages | grep $packageName")
            val installed = result.isNotEmpty()
            Log.d(TAG, "Package $packageName installed: $installed")
            installed
        } catch (e: Exception) {
            Log.e(TAG, "Error checking package: ${e.message}", e)
            false
        }
    }
    
    /**
     * Check if device has Magisk
     */
    fun hasMagisk(): Boolean {
        return try {
            Log.d(TAG, "Checking for Magisk...")
            val result = executeCommand("test -d /data/adb/magisk && echo 1 || echo 0")
            val hasMagisk = result.contains("1")
            Log.d(TAG, "Has Magisk: $hasMagisk")
            hasMagisk
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Magisk: ${e.message}", e)
            false
        }
    }
}
