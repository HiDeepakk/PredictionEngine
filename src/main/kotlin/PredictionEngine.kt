import model.DesiredConfig
import model.Resolution
import kotlin.math.abs

/**
 * The core engine that predicts the resolution a device will select.
 */
object PredictionEngine {

    // A tolerance factor to prevent excessive upscaling. A device will likely not choose a resolution
    // that is more than 50% larger in total pixels than the one requested.
    private const val UPSCALE_TOLERANCE = 1.50

    /**
     * Predicts the selected resolution based on a desired config and a list of available hardware resolutions.
     */
    fun predictSelectedResolution(
        desiredConfig: DesiredConfig,
        availableResolutions: List<Resolution>
    ): Resolution {

        val desired = desiredConfig.resolution
        val desiredPixels = desired.width * desired.height

        // This chain of fallbacks finds the best initial resolution from the camera's list.
        val initialSelection = availableResolutions.find { it == desired }?.also {
            println("--> Prediction for ${desiredConfig.name} ($desired): Found EXACT MATCH -> $it")
        } ?: availableResolutions.filter {
            // Rule: Width must match AND it must not be an excessive upscale.
            it.width == desired.width && (it.width * it.height) <= (desiredPixels * UPSCALE_TOLERANCE)
        }.minByOrNull { abs(it.height - desired.height) }?.also {
            println("--> Prediction for ${desiredConfig.name} ($desired): Found WIDTH MATCH (within tolerance) -> $it")
        } ?: availableResolutions.filter {
            // Rule: Height must match AND it must not be an excessive upscale.
            it.height == desired.height && (it.width * it.height) <= (desiredPixels * UPSCALE_TOLERANCE)
        }.minByOrNull { abs(it.width - desired.width) }?.also {
            println("--> Prediction for ${desiredConfig.name} ($desired): Found HEIGHT MATCH (within tolerance) -> $it")
        } ?: availableResolutions.filter { (it.width * it.height) <= desiredPixels }.maxByOrNull { it.width * it.height }?.also {
            println("--> Prediction for ${desiredConfig.name} ($desired): Using BEST FALLBACK -> $it")
        } ?: availableResolutions.minByOrNull { it.width * it.height }!!.also {
            println("--> Prediction for ${desiredConfig.name} ($desired): Using ULTIMATE FAILSAFE -> $it")
        }

        // Stage 2: Validate the selection for encoder compatibility (Macroblock Alignment)
        val finalResolution = validateForEncoder(initialSelection)

        if (initialSelection != finalResolution) {
            println("--> NOTE: Initial selection of $initialSelection was adjusted for encoder constraints to -> $finalResolution")
        }

        return finalResolution
    }

    /**
     * Simulates the hardware encoder's requirement for dimensions to be divisible by 16 (macroblock alignment).
     */
    private fun validateForEncoder(resolution: Resolution): Resolution {
        val finalWidth = resolution.width - (resolution.width % 16)
        val finalHeight = resolution.height - (resolution.height % 16)
        return if (finalWidth != resolution.width || finalHeight != resolution.height) {
            Resolution(finalWidth, finalHeight)
        } else {
            resolution
        }
    }
}

