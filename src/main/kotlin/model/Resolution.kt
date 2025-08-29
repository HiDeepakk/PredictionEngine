package model

/**
 * A simple data class to represent a video resolution.
 * Includes a calculated property for the aspect ratio.
 */
data class Resolution(val width: Int, val height: Int) {
    val aspectRatio: Double = width.toDouble() / height.toDouble()
    override fun toString(): String {
        return "${width}x${height}"
    }
}