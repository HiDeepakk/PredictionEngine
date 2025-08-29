package model

/**
 * Represents the ideal configuration your application wants to use.
 * For now, it only contains the resolution as requested.
 */
data class DesiredConfig(val name: String, val resolution: Resolution)