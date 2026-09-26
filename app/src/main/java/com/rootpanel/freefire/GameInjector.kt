package com.rootpanel.freefire

class GameInjector(private val rootUtils: RootUtils) {
    
    // Free Fire packages
    private val FF_PACKAGES = listOf(
        "com.dts.freefire",      // Global
        "com.dts.freefireth",    // Thailand
        "com.dts.freefirebd"     // Bangladesh
    )
    
    private val FF_PACKAGE = detectFFPackage()
    private val FF_PID = getPID(FF_PACKAGE)
    
    /**
     * Free Fire package detect karo
     */
    private fun detectFFPackage(): String {
        return FF_PACKAGES.firstOrNull { packageName ->
            rootUtils.isPackageInstalled(packageName)
        } ?: "com.dts.freefire"
    }
    
    /**
     * Aim Bot inject karo
     */
    fun injectAimBot(smooth: Float = 5.0f) {
        Thread {
            val command = """
                echo 'Injecting Aim Bot with smooth: $smooth'
                echo 'aim_bot=1' > /proc/ff_hooks
                echo 'aim_smooth=$smooth' > /proc/ff_hooks
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Silent Aim inject karo
     */
    fun injectSilentAim() {
        Thread {
            val command = "echo 'silent_aim=1' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Box inject karo
     */
    fun injectESPBox() {
        Thread {
            val command = """
                echo 'Injecting ESP Box'
                echo 'esp_box=1' > /proc/ff_hooks
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Line inject karo
     */
    fun injectESPLine() {
        Thread {
            val command = """
                echo 'Injecting ESP Line'
                echo 'esp_line=1' > /proc/ff_hooks
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Name inject karo (FIXED - NAYI METHOD)
     */
    fun injectESPName() {
        Thread {
            val command = """
                echo 'Injecting ESP Name'
                echo 'esp_name=1' > /proc/ff_hooks
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Health inject karo (FIXED - NAYI METHOD)
     */
    fun injectESPHealth() {
        Thread {
            val command = """
                echo 'Injecting ESP Health'
                echo 'esp_health=1' > /proc/ff_hooks
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Distance set karo
     */
    fun setESPDistance(distance: Int = 500) {
        Thread {
            val command = "echo 'esp_distance=$distance' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * FOV set karo
     */
    fun setFOV(fov: Int = 90) {
        Thread {
            val command = "echo 'fov=$fov' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Headshot Only Mode inject karo
     */
    fun injectHeadshotOnly() {
        Thread {
            val command = """
                echo 'Injecting Headshot Only Mode'
                echo 'headshot_only=1' > /proc/ff_hooks
                echo 'aim_bone=neck' > /proc/ff_hooks
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Aim Bot disable karo
     */
    fun killAimBot() {
        Thread {
            val command = "echo 'aim_bot=0' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Box disable karo
     */
    fun killESPBox() {
        Thread {
            val command = "echo 'esp_box=0' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * ESP Line disable karo
     */
    fun killESPLine() {
        Thread {
            val command = "echo 'esp_line=0' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Headshot Only Mode disable karo
     */
    fun disableHeadshotOnly() {
        Thread {
            val command = "echo 'headshot_only=0' > /proc/ff_hooks"
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Process ID nikalo
     */
    private fun getPID(packageName: String): Int {
        return rootUtils.getPID(packageName)
    }
}
