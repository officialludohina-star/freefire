package com.rootpanel.freefire

import java.io.BufferedReader
import java.io.InputStreamReader

class RootUtils {
    
    /**
     * Check karo ke device rooted hai ya nahi
     */
    fun testRootAccess(): Boolean {
        return try {
            val result = executeCommand("su -c 'echo test'")
            result.isNotEmpty() && result.contains("test")
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Root command execute karo
     */
    fun executeCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            reader.close()
            process.waitFor()
            output.toString()
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * su -c command execute karo (Magisk compatible)
     */
    fun executeSuperUserCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            reader.close()
            process.waitFor()
            output.toString()
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Process ID nikalo
     */
    fun getPID(packageName: String): Int {
        return try {
            val result = executeCommand("pidof $packageName")
            result.trim().split(" ").firstOrNull()?.toIntOrNull() ?: -1
        } catch (e: Exception) {
            -1
        }
    }
    
    /**
     * Package installed hai ya nahi check karo
     */
    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            val result = executeCommand("pm list packages | grep $packageName")
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
